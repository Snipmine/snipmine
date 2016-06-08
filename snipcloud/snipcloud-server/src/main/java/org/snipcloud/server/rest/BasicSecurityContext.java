package org.snipcloud.server.rest;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import org.snipcloud.server.Account;

public class BasicSecurityContext implements SecurityContext {
	/* if this is null, we're not authenticated */
	private Account account;
	
	public BasicSecurityContext(Account account) {
		this.account = account;
	}
	
	@Override
	public String getAuthenticationScheme() {
		return BASIC_AUTH;
	}

	@Override
	public Principal getUserPrincipal() {
		return account;
	}

	@Override
	public boolean isSecure() {
		/* we do ONLY use HTTPS!! EVAR!!!! */
		return true;
	}

	@Override
	public boolean isUserInRole(String role) {
		if (account == null) {
			return false;
		}
		else {
			return account.isUserInRole(role);
		}
	}

}
