package org.snipcloud.server;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.snipcloud.core.Snippet;
import org.snipcloud.core.SnippetRating;
import org.snipcloud.core.User;
import org.snipcloud.core.query.Query;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

//TODO change DB-internal IDs to type long
public class DbWrapper {
    private static Logger log = Logger.getLogger(DbWrapper.class.getName());
    private final LinkedBlockingQueue<Connection> pool = new LinkedBlockingQueue<>();
    private final Map<Connection, Long> age = new ConcurrentHashMap<>();

    public DbWrapper() {
        try {
            for (int i = 0; i < Config.getConnectionCount(); i++) {
                release(connect());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/" + Config.getDbname(), Config.getDbuser(), Config.getDbpass());
        age.put(conn, System.currentTimeMillis());
        return conn;
    }

    //Manual connection reset every hour, since the mysql connection seems to ignore the reconnect parameter
    private Connection acquire() throws SQLException {
        try {
            Connection conn = pool.poll(5, TimeUnit.SECONDS);
            if (conn != null && System.currentTimeMillis() - age.get(conn) > 60 * 60 * 1000) {
                age.remove(conn);
                try {
                    conn.close();
                } catch (Throwable t) {
                    //Well we tried
                }
                conn = null;
            }
            if (conn == null) {
                return connect();
            } else {
                return conn;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void release(Connection conn) {
        if (conn != null) {
            pool.add(conn);
        }
    }

    private Snippet readSnippet(ResultSet res) throws SQLException {
        long id = res.getLong("id");
        String code = res.getString("code");
        long author = res.getInt("author");
        Date date = res.getDate("created");
        List<String> tags = getTags(id);
        return new Snippet(id, tags, author, code, readRating(res), date);
    }

    private List<String> getTags(final long snippetId) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT t.tag FROM (hastag h JOIN tags t ON h.tag = t.id) WHERE h.snippet = ?");
            stmt.setInt(1, (int) snippetId);

            List<String> tags = new ArrayList<>();
            ResultSet tagRes = stmt.executeQuery();
            while (tagRes.next()) {
                tags.add(tagRes.getString(1));
            }
            stmt.close();
            return tags;
        } finally {
            release(c);
        }
    }

    private void addTagToSnippet(long tag, long snippet) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("INSERT IGNORE INTO hastag (snippet, tag) VALUES (?, ?)");
            stmt.setInt(1, (int) snippet);
            stmt.setInt(2, (int) tag);
            stmt.executeUpdate();
            stmt.close();
        } finally {
            release(c);
        }
    }

    private long addTagIfNotExists(final String tag) throws SQLException {
        try {
            return getTagId(tag);
        } catch (SQLException e) {
            Connection c = acquire();
            try {
                PreparedStatement stmt = c.prepareStatement("INSERT INTO tags (tag) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, tag);
                stmt.executeUpdate();
                ResultSet res = stmt.getGeneratedKeys();
                if (res.next()) {
                    long result = res.getInt(1);
                    stmt.close();
                    return result;
                } else {
                    throw new SQLException("Could not add tag.");
                }
            } finally {
                release(c);
            }
        }
    }

    private SnippetRating readRating(ResultSet res) throws SQLException {
        int vote = res.getInt("vote");
        int useCount = res.getInt("useCount");
        return new SnippetRating(vote, useCount);
    }

    public Snippet getSnippetById(long id) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c
                    .prepareStatement("SELECT * FROM snippets WHERE id = ?");
            stmt.setInt(1, (int) id);
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                Snippet tmp = readSnippet(res);
                stmt.close();
                return tmp;
            } else {
                throw new NoSuchElementException("Snippet with ID " + id
                        + " does not exist.");
            }
        } finally {
            release(c);
        }
    }

    public void removeSnippet(long id) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("DELETE FROM snippets WHERE id = ?", Statement.NO_GENERATED_KEYS);
            stmt.setInt(1, (int) id);
            stmt.executeUpdate();
            stmt.close();
            stmt = c.prepareStatement("DELETE FROM hastag WHERE snippet = ?", Statement.NO_GENERATED_KEYS);
            stmt.setInt(1, (int) id);
            stmt.executeUpdate();
            stmt.close();
        } finally {
            release(c);
        }
    }

    public long newSnippet(Snippet snippet, Account author) throws SQLException {
        Connection c = acquire();
        int sid;
        try {
            PreparedStatement stmt = c.prepareStatement("INSERT INTO snippets (code, author) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, snippet.getCode());
            stmt.setInt(2, (int) author.getId());
            stmt.executeUpdate();
            ResultSet res = stmt.getGeneratedKeys();
            if (res.next()) {
                sid = res.getInt(1);
                stmt.close();
            } else {
                throw new SQLException("Could not retrieve ID of new snippet");
            }
        } finally {
            release(c);
        }
        for (String tag : snippet.getTags()) {
            long tagId = addTagIfNotExists(tag);
            addTagToSnippet(tagId, sid);
        }
        return sid;
    }

    public List<Long> getSnippetsByQuery(Query query)
            throws SQLException {
        long t0 = System.currentTimeMillis();
        Connection c = acquire();
        try {
            QueryCompiler qc = new QueryCompiler(acquire(), query.simplify());
            PreparedStatement sqlQuery = qc.compileQuery();

            log.info("SQL Query: " + sqlQuery.toString());

            ResultSet res = sqlQuery.executeQuery();
            List<Long> snippets = new ArrayList<>();

            while (res.next()) {
                snippets.add((long)res.getInt("snippet"));
            }
            sqlQuery.close();

            long dt = System.currentTimeMillis() - t0;
            log.info("Took " + dt + " ms to execute query");

            return snippets;
        } finally {
            release(c);
        }
    }

    public long getTagId(String tag) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT id FROM tags WHERE tag = ?");
            stmt.setString(1, tag);

            log.info("SQL Query: " + stmt.toString());
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                long tmp = res.getLong("id");
                stmt.close();
                return tmp;
            } else {
                throw new SQLException("Invalid tagname");
            }
        } finally {
            release(c);
        }
    }

    public List<Snippet> getSnippetByTag(String tag) throws SQLException {
        long tagid;
        try {
            tagid = getTagId(tag);
        } catch (SQLException e) {
            return Collections.emptyList();
        }
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM (snippets s JOIN hastag t ON s.id = t.snippet) WHERE t.tag = ?");
            stmt.setLong(1, tagid);

            log.info("SQL Query: " + stmt.toString());
            ResultSet res = stmt.executeQuery();
            List<Snippet> snippets = new ArrayList<>();
            while (res.next()) {
                Snippet snippet = readSnippet(res);
                snippets.add(snippet);
            }
            stmt.close();
            return snippets;
        } finally {
            release(c);
        }
    }

    public List<Snippet> getSnippetByAuthor(String author) throws SQLException {
        User user = getUserByName(author);
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM snippets WHERE author = ?");
            stmt.setLong(1, user.getId());
            ResultSet res = stmt.executeQuery();
            List<Snippet> snippets = new ArrayList<>();
            while (res.next()) {
                Snippet snippet = readSnippet(res);
                snippets.add(snippet);
            }
            log.info("SQL Query: " + stmt.toString());
            stmt.close();
            return snippets;
        } finally {
            release(c);
        }
    }

    public List<Snippet> getLatestSnippets(int number) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM snippets ORDER BY created DESC LIMIT ?");
            stmt.setInt(1, number);
            ResultSet res = stmt.executeQuery();
            List<Snippet> snippets = new ArrayList<>();
            while (res.next()) {
                Snippet snippet = readSnippet(res);
                snippets.add(snippet);
            }
            log.info("SQL Query: " + stmt.toString());
            stmt.close();
            return snippets;
        } finally {
            release(c);
        }
    }

    public List<Snippet> getBestSnippets(int number) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM snippets ORDER BY vote DESC, useCount DESC LIMIT ?");
            stmt.setInt(1, number);
            ResultSet res = stmt.executeQuery();
            List<Snippet> snippets = new ArrayList<>();
            while (res.next()) {
                Snippet snippet = readSnippet(res);
                snippets.add(snippet);
            }
            log.info("SQL Query: " + stmt.toString());
            stmt.close();
            return snippets;
        } finally {
            release(c);
        }
    }

    public void vote(long snippetId, Account account, int vote) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("UPDATE snippets SET vote = vote + ? WHERE id = ?");
            stmt.setInt(1, vote);
            stmt.setInt(2, (int) snippetId);
            stmt.executeUpdate();
            log.info(stmt.toString());
            stmt.close();
        } finally {
            release(c);
        }
    }

    public void use(long snippetId, Account account, int use) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("UPDATE snippets SET useCount = useCount + ? WHERE id = ?");
            stmt.setInt(1, use);
            stmt.setInt(2, (int) snippetId);
            stmt.executeUpdate();
            log.info(stmt.toString());
            stmt.close();
        } finally {
            release(c);
        }
    }


    private User readUser(ResultSet res) throws SQLException {
        return new User(res.getInt("id"), res.getString("username"),
                res.getString("firstname"), res.getString("lastname"),
                res.getString("email"));
    }

    private Account readAccount(ResultSet res) throws SQLException,
            DecoderException {
        User user = readUser(res);

        String passwordHash = res.getString("password_hash");
        String passwordSalt = res.getString("password_salt");
        boolean verified = res.getBoolean("verified");
        String verificationToken = res.getString("verification_token");

        return new Account(user, Hex.decodeHex(passwordHash.toCharArray()),
                Hex.decodeHex(passwordSalt.toCharArray()), verified, verificationToken);
    }

    public User getUserById(long userId) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c
                    .prepareStatement("SELECT id, username, firstname, lastname, email FROM users WHERE id = ? AND verified = TRUE LIMIT 1");
            stmt.setInt(1, (int) userId);
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                User tmp = readUser(res);
                stmt.close();
                return tmp;
            } else {
                throw new NoSuchElementException("User with ID " + userId
                        + " does not exist.");
            }
        } finally {
            release(c);
        }
    }

    public Account getAccountById(long userId) throws SQLException,
            DecoderException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c
                    .prepareStatement("SELECT * FROM users WHERE id = ? LIMIT 1");
            stmt.setInt(1, (int) userId);
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                Account tmp = readAccount(res);
                stmt.close();
                return tmp;
            } else {
                throw new NoSuchElementException("Account with ID " + userId
                        + " does not exist.");
            }
        } finally {
            release(c);
        }
    }

    public User getUserByName(String username) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c
                    .prepareStatement("SELECT id, username, firstname, lastname, email FROM users WHERE username = ? LIMIT 1");
            stmt.setString(1, username);
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                User tmp = readUser(res);
                stmt.close();
                return tmp;
            } else {
                throw new NoSuchElementException("User " + username
                        + " does not exist.");
            }
        } finally {
            release(c);
        }
    }

    public User getUserByEmail(String email) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c
                    .prepareStatement("SELECT id, username, firstname, lastname, email FROM users WHERE email = ? LIMIT 1");
            stmt.setString(1, email);
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                User tmp = readUser(res);
                stmt.close();
                return tmp;
            } else {
                throw new NoSuchElementException("User with email address " + email
                        + " does not exist.");
            }
        } finally {
            release(c);
        }
    }

    public long newAccount(User user, byte[] passwordSalt, byte[] passwordHash, String verificationToken) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("INSERT INTO users (firstname, lastname, email, username, password_hash, password_salt, verification_token) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmailAddress());
            stmt.setString(4, user.getUsername());
            stmt.setString(5, new String(Hex.encodeHex(passwordHash)));
            stmt.setString(6, new String(Hex.encodeHex(passwordSalt)));
            stmt.setString(7, verificationToken);
            stmt.executeUpdate();
            ResultSet res = stmt.getGeneratedKeys();
            if (res.next()) {
                long tmp = res.getInt(1);
                stmt.close();
                return tmp;
            } else {
                throw new SQLException("Could not retrieve ID of new account");
            }
        } finally {
            release(c);
        }
    }

    public boolean verifyAccount(long id, String token) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("UPDATE users SET verified = TRUE, verification_token = NULL WHERE id = ? AND verification_token = ?");
            stmt.setInt(1, (int) id);
            stmt.setString(2, token);
            return stmt.executeUpdate() == 1;
        } finally {
            release(c);
        }
    }

    public void printResult(ResultSet res) throws SQLException {
        ResultSetMetaData rsmd = res.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int i = 1; i <= columns; i++) {
            int jdbcType = rsmd.getColumnType(i);
            String name = rsmd.getColumnTypeName(i);
            System.out.print("Column " + i + " is JDBC type " + jdbcType);
            System.out.println(", which the DBMS calls " + name);
        }
        System.out.println("Table Columns: ");
        for (int i = 1; i <= columns; i++) {
            if (i > 1)
                System.out.print(",  ");
            String columnName = rsmd.getColumnName(i);
            System.out.print(columnName);
        }
    }

    public void deleteSnippet(final long id) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("DELETE FROM snippets WHERE id = ?");
            stmt.setInt(1, (int) id);
            stmt.executeUpdate();
            log.info(stmt.toString());
            stmt.close();
            stmt = c.prepareStatement("DELETE FROM hastag WHERE snippet = ?");
            stmt.setInt(1, (int) id);
            stmt.executeUpdate();
            log.info(stmt.toString());
            stmt.close();
        } finally {
            release(c);
        }
    }

    public void setTags(final long id, final List<String> tags) throws SQLException {
        Connection c = acquire();
        try {
            PreparedStatement stmt = c.prepareStatement("DELETE FROM hastag WHERE snippet = ?");
            stmt.setInt(1, (int) id);
            stmt.executeUpdate();
            log.info(stmt.toString());
            stmt.close();
        } finally {
            release(c);
        }
        for (String t : tags) {
            long tag = addTagIfNotExists(t);
            addTagToSnippet(tag, id);
        }
    }
}
