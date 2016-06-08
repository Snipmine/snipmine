package org.snipcloud.client;

import org.junit.Before;
import org.snipcloud.core.AccountRegistration;
import org.snipcloud.core.User;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;

/**
 * @author Alex Schlosser
 */
public class Main {

    private static final RestClient restClient = new RestClient(new ApiEndpoint() {
        @Override
        public KeyStore getKeyStore() {
            return null;
        }

        @Override
        public URI getBaseUrl() {
            try {
                return new URI("http://snipcloud.alsclo.de/api");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isProxy() {
            return false;
        }
    }, new BasicAuthentication(1L, "sn1pm1ne"));

    public static void main(String[] args){
        System.out.println(restClient.getSnippetById(1L));
    }
}
