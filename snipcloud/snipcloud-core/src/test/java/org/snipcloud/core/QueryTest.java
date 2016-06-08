package org.snipcloud.core;

import org.junit.Test;
import org.snipcloud.core.query.SimpleQuery;

import junit.framework.TestCase;

public class QueryTest extends TestCase {
	@Test
	public void testToString() {
		SimpleQuery query = SimpleQuery.fromString("a,b,c -a,b,-c");
		SimpleQuery query2 = SimpleQuery.fromString(query.toString());
		assertEquals(query, query2);
	}
}
