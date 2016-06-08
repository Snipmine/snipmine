package org.snipcloud.core.query;

import org.snipcloud.core.Snippet;

public interface Query {
	public boolean match(Snippet snippet);

	/*
	 * advanced queries would have to be transformed to disjunctive normal form,
	 * before passing them to DbWrapper
	 */
	public SimpleQuery simplify();
}
