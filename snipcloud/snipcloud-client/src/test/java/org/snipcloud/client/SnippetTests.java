//package org.snipcloud.client;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.snipcloud.core.Snippet;
//
//public class SnippetTests {
//	private RestClient restClient;
//
//	private final String code = "System.out.println(\"Hello World! \" + 42);";
//	private final int snippetId = 25;
//
//	@Before
//	public void setUp() {
//		restClient = new RestClient(new BasicAuthentication(1, "foobar123"));
//	}
//
//	@Test
//	public void testNewSnippet() {
//		List<String> tags = new ArrayList<>();
//		tags.add("hello");
//		tags.add("world");
//		tags.add("42");
//
//		Snippet snippet = new Snippet(tags, code);
//
//		long snippetId = restClient.newSnippet(snippet);
//		System.out.println("snippet id = " + snippetId);
//		if (snippetId < 0) {
//			fail("Invalid snippet ID returned");
//		}
//
//		Snippet snippet2 = restClient.getSnippetById(snippetId);
//		assertEquals(snippetId, snippet2.getId());
//		assertEquals(tags, snippet2.getTags());
//		assertEquals(code, snippet2.getCode());
//		assertEquals(1, snippet2.getAuthor().getId());
//	}
//
//	@Test
//	public void testGet() {
//		Snippet snippet = restClient.getSnippetById(snippetId);
//		System.out.println(snippet);
//	}
//
//	@Test
//	public void testUpvote() {
//		Snippet before = restClient.getSnippetById(snippetId);
//		restClient.upvote(snippetId);
//		Snippet after = restClient.getSnippetById(snippetId);
//		assertEquals(before.getRating().getVote() + 1, after.getRating().getVote());
//	}
//
//	@Test
//	public void testDownvote() {
//		Snippet before = restClient.getSnippetById(snippetId);
//		restClient.downvote(snippetId);
//		Snippet after = restClient.getSnippetById(snippetId);
//		assertEquals(before.getRating().getVote() - 1, after.getRating().getVote());
//	}
//
//	@Test
//	public void testUse() {
//		Snippet before = restClient.getSnippetById(snippetId);
//		restClient.use(snippetId);
//		Snippet after = restClient.getSnippetById(snippetId);
//		assertEquals(before.getRating().getUseCount() + 1, after.getRating().getUseCount());
//	}
//
//	@Test
//	public void testUnuse() {
//		Snippet before = restClient.getSnippetById(snippetId);
//		restClient.unuse(snippetId);
//		Snippet after = restClient.getSnippetById(snippetId);
//		assertEquals(before.getRating().getUseCount() - 1, after.getRating().getUseCount());
//	}
//}
