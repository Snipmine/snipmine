package org.snipcloud.client;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

public class NoAuthentication implements Authentication {

	@Override
	public Builder request(WebTarget target) {
		return target.request();
	}
	
}
