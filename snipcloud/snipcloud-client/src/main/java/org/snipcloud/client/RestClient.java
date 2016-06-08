package org.snipcloud.client;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.snipcloud.core.AccountRegistration;
import org.snipcloud.core.Snippet;
import org.snipcloud.core.User;
import org.snipcloud.core.query.SimpleQuery;

public class RestClient {
	private WebTarget root;
	private Authentication auth;

	public RestClient(ApiEndpoint endpoint, Authentication auth) {
		JerseyClientBuilder builder = new JerseyClientBuilder();
		builder.property(ClientProperties.FOLLOW_REDIRECTS, true);

		if (endpoint.getKeyStore() != null) {
			try {
				builder.sslContext(SSLContext.getDefault());
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
			builder.trustStore(endpoint.getKeyStore());
		}
		
		final URI baseUrl = endpoint.getBaseUrl();
		builder.hostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return hostname.equals(baseUrl.getHost());
			}
		});
		Client client = builder.build();
		client.register(JacksonFeature.class);
		root = client.target(baseUrl);
		this.auth = auth;
	}

	public RestClient(ApiEndpoint endpoint) {
		this(endpoint, new NoAuthentication());
	}
	
	public URI getURI() {
		return root.getUri();
	}

	public String test() {
		return auth.request(root.path("test")).accept(MediaType.TEXT_PLAIN)
				.get(String.class);
	}

	/**
	 * Test whether rest client is authenticated
	 * @return true, if authenticated, else false
	 */
	public boolean testAuth() {
		try {
			auth.request(root.path("testAuth")).accept(MediaType.TEXT_PLAIN)
				.get(String.class);
			return true;
		} catch (NotAuthorizedException | ForbiddenException e) {
			return false;
		}
	}

	public Snippet getSnippetById(long id) {
		return auth.request(root.path("snippet/").path(Long.toString(id)))
				.accept(MediaType.APPLICATION_JSON).get(Snippet.class);
	}
	
	/**
	 * Query snippets with a simple query
	 * @param query
	 * @return List of Snippet ids mathcing the query.
	 */
	public List<Long> query(SimpleQuery query) {
		return auth
				.request(root.path("snippet/query/"))
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(query,
						MediaType.APPLICATION_JSON),
						new GenericType<List<Long>>() {
						});
	}

	public long newSnippet(Snippet snippet) {
		long id = auth
				.request(root.path("snippet/new"))
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(snippet, MediaType.APPLICATION_JSON),
						Long.class);
		return id;
	}

	public void deleteSnippet(long snippetId) {
		auth.request(
				root.path("snippet/").path(Long.toString(snippetId))).delete();
	}

	public List<Snippet> getSnippetsByTag(String tag) {
		return auth
				.request(root.path("snippet/byTag/").path(tag))
				.accept(MediaType.APPLICATION_JSON).get(new GenericType<List<Snippet>>(){});
	}

	public void upvote(long snippetId) {
		auth.request(
				root.path("snippet/").path(Long.toString(snippetId))
						.path("vote/up")).get();
	}

	public void downvote(long snippetId) {
		auth.request(
				root.path("snippet/").path(Long.toString(snippetId))
						.path("vote/down")).get();
	}

	public void use(long snippetId) {
		auth.request(
				root.path("snippet/").path(Long.toString(snippetId))
						.path("use")).get();
	}

	public void unuse(long snippetId) {
		auth.request(
				root.path("snippet/").path(Long.toString(snippetId))
						.path("unuse")).get();
	}

	public void setTags(long snippetId, List<String> tags) {
		auth.request(
				root.path("snippet/").path(Long.toString(snippetId))
						.path("setTags"))
				.post(Entity.entity(tags,
						MediaType.APPLICATION_JSON));
	}

	public User getUserById(long id) {
		return auth.request(root.path("user/").path(Long.toString(id)))
				.accept(MediaType.APPLICATION_JSON).get(User.class);
	}
	
	public User getUserByName(String username) {
		return auth.request(root.path("user/byName/").path(username))
				.accept(MediaType.APPLICATION_JSON).get(User.class);
	}
	
	public long register(AccountRegistration registration) {
		long id = auth
				.request(root.path("user/register"))
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(registration, MediaType.APPLICATION_JSON),
						Long.class);
		return id;
	}
}
