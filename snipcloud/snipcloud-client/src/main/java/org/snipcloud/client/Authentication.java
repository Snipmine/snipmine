package org.snipcloud.client;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

public interface Authentication {
	Builder request(WebTarget target);
}
