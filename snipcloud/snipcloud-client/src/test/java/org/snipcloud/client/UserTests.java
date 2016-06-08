//package org.snipcloud.client;
//
//import static org.junit.Assert.*;
//
//import javax.ws.rs.ForbiddenException;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.snipcloud.core.AccountRegistration;
//import org.snipcloud.core.User;
//
//public class UserTests {
//	private RestClient restClient;
//
//	@Before
//	public void setUp() {
//		restClient = new RestClient(new NoAuthentication());
//	}
//
//	@Test
//	public void testMaxMustermann() {
//		User max = restClient.getUserById(1);
//		assertEquals(1, max.getId());
//		assertEquals("Max", max.getFirstName());
//		assertEquals("max@mustermann.de", max.getEmailAddress());
//		assertEquals("test", max.getUsername());
//	}
//
//	@Test
//	public void testRegister() {
//		/*
//		final String password = "password123";
//		final User newUser = new User(-1, "test2", "Max", "Mustermann", "max2@mustermann.de");
//		final AccountRegistration registration = new AccountRegistration(newUser, password);
//		final long userId = restClient.register(registration);
//
//		RestClient restAuth = new RestClient(new BasicAuthentication(userId, password));
//		assertFalse("Could authenticate with new account, even if it's not verified yet.", restAuth.testAuth());
//		*/
//	}
//
//}
