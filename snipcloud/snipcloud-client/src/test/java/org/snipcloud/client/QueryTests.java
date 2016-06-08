//package org.snipcloud.client;
//
//import java.util.Collection;
//import static org.junit.Assert.*;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.snipcloud.client.NoAuthentication;
//import org.snipcloud.client.RestClient;
//import org.snipcloud.core.Snippet;
//import org.snipcloud.core.query.SimpleQuery;
//
//public class QueryTests {
//	private RestClient restClient;
//
//	@Before
//	public void setUp() {
//		restClient = new RestClient(new NoAuthentication());
//	}
//
//	@Test
//	public void testQuery() {
//		Collection<Snippet> snippets = restClient.query(SimpleQuery.fromString("java,sort,array,integer,bubble"));
//
//		System.out.println("Server returned " + snippets.size() + " snippets.");
//		for (Snippet snippet: snippets) {
//			System.out.println(snippet);
//			System.out.println(snippet.getCode());
//			System.out.println(snippet.getTags());
//			System.out.println("");
//		}
//
//		assertEquals(1, snippets.size());
//	}
//
//}
