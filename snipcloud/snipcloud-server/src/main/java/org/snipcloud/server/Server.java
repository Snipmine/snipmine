package org.snipcloud.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.SQLException;

import javax.ws.rs.ProcessingException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.snipcloud.server.rest.BasicAuthenticationFilter;
import org.snipcloud.server.rest.RestInterface;

public class Server {

	public static void main(String[] args) throws IllegalArgumentException,
			IOException, ProcessingException, URISyntaxException, SQLException,
			NoSuchAlgorithmException, CertificateException, KeyStoreException,
			UnrecoverableKeyException, KeyManagementException, InterruptedException {
		/* mailer */
		//mailer = new SnipcloudMailer("smtp.uni-saarland.de", "snipcloud@uni-saarland.de");
		
		/* start REST server */
		final ResourceConfig config = new ResourceConfig()
				.register(new BasicAuthenticationFilter())
				.register(RolesAllowedDynamicFeature.class)
				.register(RestInterface.class)
				.register(JacksonFeature.class);

		final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(new URI(
				Config.getProtocol() + "://" + Config.getHost() + ":" + Config.getPort()), config);
		server.start();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				server.stop();
			}
		}, "shutdownHook"));

		Thread.currentThread().join();
	}

}
