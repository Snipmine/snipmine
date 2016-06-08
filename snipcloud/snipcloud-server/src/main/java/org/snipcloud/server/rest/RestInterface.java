package org.snipcloud.server.rest;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.snipcloud.core.AccountRegistration;
import org.snipcloud.core.RegistrationError;
import org.snipcloud.core.Snippet;
import org.snipcloud.core.User;
import org.snipcloud.core.query.SimpleQuery;
import org.snipcloud.server.Account;
import org.snipcloud.server.DbWrapper;
import org.snipcloud.server.Server;

@Path("/api")
@PermitAll
public class RestInterface {
	private static Logger log = Logger.getLogger(RestInterface.class.getName());
	private static DbWrapper db = new DbWrapper();

	/* the current security context */
	@Context
	private SecurityContext securityContext;

	/**
	 * Returns the user account, if authenticated
	 * 
	 * @return
	 */
	private Account getAccount() {
		return (Account) securityContext.getUserPrincipal();
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String banner() {
		// Any browser visiting /api should be redirected to the API documentation
		//TODO fix this
		return "<html><head><meta http-equiv=\"refresh\" content=\"0; URL=https://134.96.235.19/doc/api\"></head></html>";
	}

	/**
	 * A simple test endpoint
	 * 
	 * @return Some testing stuff
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("test")
	public String test() {
		String prolog = "Hello there! Everything seems fine here. How about you?\n";
		Account account = getAccount();
		log.info("Test resource requested by " + account);
		log.info("Account: " + account);
		if (account != null) {
			return prolog + "You're logged in as: " + account;
		} else {
			return prolog + "You're not logged in.";
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("testAuth")
	@RolesAllowed("user")
	public String testAuth() {
		log.info("AuthTest resource requested by " + getAccount());
		return "Hey, you're authenticated :)";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/{id}")
	public Snippet snippetById(@PathParam("id") int id) throws SQLException {
		log.info("byId(" + id + ")");
		Snippet snippet = db.getSnippetById(id);
		log.info(snippet.toString());
		return snippet;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/query/")
	public List<Long> snippetSimpleQuery(SimpleQuery query) throws SQLException {
		log.info("simpleQuery(" + query + ")");
		return db.getSnippetsByQuery(query);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/byTag/{tag}")
	public List<Snippet> byTag(@PathParam("tag") String tag) throws SQLException {
		log.info("byTag(" + tag + ")");
		return db.getSnippetByTag(tag);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/byAuthor/{author}")
	public List<Snippet> snippetByAuthor(@PathParam("author") String author) throws SQLException {
		log.info("byAuthor("+author+")");
		return db.getSnippetByAuthor(author);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/latest/{number}")
	public List<Snippet> latestSnippets(@PathParam("number") int number) throws SQLException {
		log.info("latest "+number+" snippets");
		return db.getLatestSnippets(number);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/best/{number}")
	public List<Snippet> bestSnippets(@PathParam("number") int number) throws SQLException {
		log.info("best "+number+" snippets");
		return db.getBestSnippets(number);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/new")
	@RolesAllowed("user")
	public long snippetNew(Snippet snippet) throws SQLException {
		log.info("new(" + snippet + ")");
		return db.newSnippet(snippet, getAccount());
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/{id}/setTags")
	@RolesAllowed("user")
	public String setTags(@PathParam("id") long id, List<String> tags) throws SQLException {
		log.info("setTags(" + id + ")");
		db.setTags(id, tags);
		return "";
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/{id}")
	@RolesAllowed("user")
	public String snippetDelete(@PathParam("id") long id) throws SQLException {
		log.info("delete(" + id + ")");
		return "success";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/{id}/vote/up")
	@RolesAllowed("user")
	public String snippetUpvote(@PathParam("id") long id) throws SQLException {
		log.info("upvote "+ id);
		db.vote(id, getAccount(), +1);
		return "";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/{id}/vote/down")
	@RolesAllowed("user")
	public String snippetDownvote(@PathParam("id") long id) throws SQLException {
		log.info("down "+ id);
		db.vote(id, getAccount(), -1);
		return "";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/{id}/use/")
	@RolesAllowed("user")
	public String snippetUse(@PathParam("id") long id) throws SQLException {
		db.use(id, getAccount(), +1);
		return "";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("snippet/{id}/unuse")
	@RolesAllowed("user")
	public String snippetUnuse(@PathParam("id") long id) throws SQLException {
		db.use(id, getAccount(), -1);
		return "";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("user/{id}")
	public User userById(@PathParam("id") long id) throws SQLException {
		return db.getUserById(id);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("user/byName/{name}")
	public User userByName(@PathParam("name") String name) throws SQLException {
		return db.getUserByName(name);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("user/register")
	public long accountRegister(AccountRegistration registration) throws RegistrationError, SQLException, AddressException, MessagingException {
		registration.checkPassword();
		byte[] passwordSalt = Account.generateSalt();
		byte[] passwordHash = Account.calculatePasswordHash(passwordSalt, registration.getPassword());
		
		try {
			db.getUserByName(registration.getUser().getUsername());
			throw new RegistrationError("Username already exists");
		} catch (NoSuchElementException e) {}
		try {
			db.getUserByEmail(registration.getUser().getEmailAddress());
			throw new RegistrationError("Email address already exists");
		} catch (NoSuchElementException e) {}

		String verificationToken = Account.generateVerificationToken();
		long id = db.newAccount(registration.getUser(), passwordSalt, passwordHash, verificationToken);
//		Server.getMailer().sendVerificationMail(registration.getUser().getEmailAddress(), id, verificationToken);
		return id;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("user/{id}/verify/{token}")
	public boolean accountVerify(@PathParam("id") long id, @PathParam("token") String token) throws SQLException {
		return db.verifyAccount(id, token);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("user/delete")
	@RolesAllowed("user")
	public User accountDelete(@PathParam("id") long id) throws SQLException {
		return db.getUserById(id);
	}
}
