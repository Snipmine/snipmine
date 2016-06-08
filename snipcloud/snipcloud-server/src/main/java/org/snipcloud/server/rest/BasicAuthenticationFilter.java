package org.snipcloud.server.rest;

import java.sql.SQLException;

import javax.annotation.Priority;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.snipcloud.server.Account;
import org.snipcloud.server.DbWrapper;


/**
 * A simple authentication method using HTTP headers.
 * Note that this isn't "HTTP Basic Authentication", but a custom method.
 */
@Priority(Priorities.AUTHENTICATION)
public class BasicAuthenticationFilter implements ContainerRequestFilter {
	private DbWrapper db = new DbWrapper();

	@Override
	public void filter(ContainerRequestContext containerRequest) {
		try {
			_filter(containerRequest);
		} catch (SQLException | DecoderException e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	private void _filter(ContainerRequestContext containerRequest) throws SQLException, DecoderException {
		/* read user ID and password from HTTP header */
		String userIdStr = containerRequest.getHeaderString("X-UserId");
		String password = containerRequest.getHeaderString("X-Password");

		/* try to match credentials against a known user account */
		Account account = null;
		if (userIdStr != null && password != null) {
			long userId;
			try {
				userId = Long.parseLong(userIdStr);
			} catch (NumberFormatException e) {
				throw new WebApplicationException(401);
			}
			
			/* password is encoded as base64 */
			password = new String(Base64.decodeBase64(password.getBytes()));
			
			account = db.getAccountById(userId);
			if (!account.checkPassword(password)) {
				throw new WebApplicationException("Wrong username or password", 401);
			}
			if (!account.isVerified()) {
				throw new WebApplicationException("Account not verified", 401);
			}
		}
		
		/* inject SecurityContext */
		SecurityContext securityContext = new BasicSecurityContext(account);
		containerRequest.setSecurityContext(securityContext);
	}

}
