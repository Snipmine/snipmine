package org.snipcloud.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;

public class ProxyEndpoint implements ApiEndpoint {
	private URI url;
	
	public ProxyEndpoint(String url) throws URISyntaxException {
		this.url = new URI(url);
	}
	
	@Override
	public KeyStore getKeyStore() {
		return null;
	}

	@Override
	public URI getBaseUrl() {
		return url;
	}

	@Override
	public boolean isProxy() {
		return true;
	}

}
