package org.snipcloud.client;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.internal.util.Base64;
import org.snipcloud.core.User;

public class BasicAuthentication implements Authentication {
	private long userId;
	private String password;
	
	public BasicAuthentication(long userId, String password) {
		this.userId = userId;
		this.password = password;
	}
	
	public Builder request(WebTarget target) {
		Builder builder = target.request();
		builder.header("X-UserId", Long.toString(userId));
		builder.header("X-Password", Base64.encodeAsString(password));
		return builder;
	}
	
}
