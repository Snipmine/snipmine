//package org.snipcloud.client;
//
//import static org.junit.Assert.*;
//
//import java.util.Collections;
//
//import javax.ws.rs.ForbiddenException;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.snipcloud.client.BasicAuthentication;
//import org.snipcloud.client.NoAuthentication;
//import org.snipcloud.client.RestClient;
//import org.snipcloud.core.Snippet;
//
//public class AuthTests {
//	private RestClient restAuth, restNoAuth;
//
//	@Before
//	public void setUp() {
//		restNoAuth = new RestClient(new NoAuthentication());
//		restAuth = new RestClient(new BasicAuthentication(1, "foobar123"));
//	}
//
//	@Test
//	public void testTest() {
//		assertEquals(
//				"Hello there! Everything seems fine here. How about you?\n"
//						+ "You're not logged in.", restNoAuth.test());
//
//		assertEquals(
//				"Hello there! Everything seems fine here. How about you?\n"
//						+ "You're logged in as: Account [user=User [id=1, username=test, firstName=Max, lastName=Mustermann, emailAddress=max@mustermann.de], verified=true]", restAuth.test());
//	}
//
//	@Test
//	public void testTestAuth() {
//		assertFalse(restNoAuth.testAuth());
//		assertTrue(restAuth.testAuth());
//	}
//
//	@Test
//	public void testNewSnippet() {
//		try {
//			restNoAuth.newSnippet(new Snippet(Collections.<String> emptyList(), ""));
//			fail();
//		} catch (ForbiddenException e) {
//		}
//	}
//
//}
