package org.snipcloud.client;

import java.net.URI;
import java.security.KeyStore;

public interface ApiEndpoint {
	public KeyStore getKeyStore();
	public URI getBaseUrl();
	boolean isProxy();
}

