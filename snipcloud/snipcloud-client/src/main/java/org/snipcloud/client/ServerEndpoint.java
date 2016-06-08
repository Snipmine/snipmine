package org.snipcloud.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class ServerEndpoint implements ApiEndpoint {
	private final static String KEY_STORE_FILE = "org/snipcloud/client/truststore.jks";
	private final static String KEY_STORE_PASSWORD = "xYq=@mYZ3QR@C!pi";
	private final static String SERVER_URL = "https://134.96.235.19/api";
	
	public final static ServerEndpoint DEFAULT = new ServerEndpoint(SERVER_URL);

	private KeyStore keystore;
	private URI baseUrl;

	@Deprecated
	public ServerEndpoint(){
		this(SERVER_URL);
	}

	@Deprecated
	public ServerEndpoint(String url) {
		try {
			keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream keyStoreStream = ServerEndpoint.class.getClassLoader().getResourceAsStream(KEY_STORE_FILE);
			keystore.load(keyStoreStream, KEY_STORE_PASSWORD.toCharArray());
			baseUrl = new URI(url);
		} catch (KeyStoreException | URISyntaxException | NoSuchAlgorithmException | CertificateException | IOException e) {
			System.out.println();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public KeyStore getKeyStore() {
		return keystore;
	}

	@Override
	public URI getBaseUrl() {
		return baseUrl;
	}

	@Override
	public boolean isProxy() {
		return false;
	}
}
