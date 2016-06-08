package org.snipcloud.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import org.snipcloud.core.query.SimpleQuery;
import org.snipcloud.core.query.SimpleQueryClause;
import org.snipcloud.core.query.SimpleQueryLiteral;

public class QueryCompiler {
	private class QueryPart {
		private String sql;
		private List<String> tags;
		
		public QueryPart(String sql, List<String> tags) {
			this.sql = sql;
			this.tags = tags;
		}
		public String getSQL() {
			return sql;
		}
		public List<String> getTags() {
			return tags;
		}
	}
	
	private Connection conn;
	private SimpleQuery query;
	
	public QueryCompiler(Connection conn, SimpleQuery query) {
		this.conn = conn;
		this.query = query;
	}
	
	public PreparedStatement compileQuery() throws SQLException {
		QueryPart q = compileQuery(query);
		PreparedStatement stmt = conn.prepareStatement(q.getSQL());
		int i = 1;
		for (String tag: q.getTags()) {
			stmt.setString(i++, tag);
		}
		return stmt;
	}
	
	private QueryPart compileQuery(SimpleQuery query) {
		if (query.getClauses().isEmpty()) {
			throw new IllegalArgumentException("Empty queries are not allowed!");
		}
		List<String> tags = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (SimpleQueryClause c : query) {
			if (!c.getLiterals().isEmpty()) {
				QueryPart p = compileClause(c);
				sb.append(p.getSQL());
				tags.addAll(p.getTags());
				sb.append(" UNION ");
			}
		}
		sb.setLength(sb.length()-(" UNION ".length()));
		return new QueryPart(sb.toString(), tags);
	}
	
	private QueryPart compileClause(SimpleQueryClause clause) {
		if (clause.getLiterals().isEmpty()) {
			throw new IllegalArgumentException("Empty clauses are not allowed!");
		}
		List<String> tags = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		//TODO this could be a join
		sb.append("(SELECT snippet FROM hastag WHERE tag IN (SELECT id FROM tags WHERE");
		for (SimpleQueryLiteral l : clause) {
			if (l.isNegative()) {
				sb.append(" tag NOT LIKE ");
			} else {
				sb.append(" tag LIKE ");
			}
			tags.add(l.getTag());
			sb.append("? AND");
		}
		sb.setLength(sb.length()-(" AND".length()));
		sb.append("))");

		return new QueryPart(sb.toString(), tags);
	}

}
