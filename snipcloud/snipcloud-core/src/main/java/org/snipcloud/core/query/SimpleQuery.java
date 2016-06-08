package org.snipcloud.core.query;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.snipcloud.core.Snippet;
import org.snipcloud.core.SnippetRating;

@XmlRootElement
@XmlType(factoryMethod = "newInstance")
public class SimpleQuery implements Query, Iterable<SimpleQueryClause>{
	@XmlElement
	private Set<SimpleQueryClause> clauses;
	
	public SimpleQuery(Set<SimpleQueryClause> clauses) {
		this.clauses = clauses;
	}
	
	/* A quick & dirty "SimpleQuery" parser 
	 * We probably want to support more complex queries and therefore should write a better parser
	 * in a seperate class. 
	 */
	public static SimpleQuery fromString(String queryStr) {		
		String[] clausesStr = queryStr.split("\\s+");
		HashSet<SimpleQueryClause> clauses = new HashSet<>();
		for (String clauseStr: clausesStr) {
			String[] literalsStr = clauseStr.split(",");
			HashSet<SimpleQueryLiteral> literals = new HashSet<>();
			for (String literalStr: literalsStr) {
				boolean negative = false;
				if (literalStr.startsWith("-")) {
					 literalStr = literalStr.substring(1);
					 negative = true;
				}
				literals.add(new SimpleQueryLiteral(literalStr, negative));
			}
			clauses.add(new SimpleQueryClause(literals));
		}
		return new SimpleQuery(clauses);
	}

	public Set<SimpleQueryClause> getClauses() {
		return clauses;
	}
	
	public boolean match(Snippet snippet) {
		for (SimpleQueryClause clause: clauses) {
			if (clause.match(snippet)) {
				return true;
			}
		}
		return false;
	}

	public Set<SimpleQueryLiteral> getLiterals() {
		HashSet<SimpleQueryLiteral> tags = new HashSet<>();
		for (SimpleQueryClause clause: clauses) {
			tags.addAll(clause.getLiterals());
		}
		return tags;
	}
	
	@Override
	public SimpleQuery simplify() {
		return this;
	}
	
	public SimpleQuery() {
		this.clauses = new HashSet<SimpleQueryClause>();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<SimpleQueryClause> clauseIt = clauses.iterator();
		
		while (clauseIt.hasNext()) {
			sb.append(clauseIt.next().toString());
			if (clauseIt.hasNext()) {
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clauses == null) ? 0 : clauses.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SimpleQuery)) {
			return false;
		}
		SimpleQuery other = (SimpleQuery) obj;
		if (clauses == null) {
			if (other.clauses != null) {
				return false;
			}
		} else if (!clauses.equals(other.clauses)) {
			return false;
		}
		return true;
	}
	
	// Xml jaxb magic
	// Do not call any of these methods
	
	public static SimpleQuery newInstance() {
		return new SimpleQuery();
	}


	@Override
	public Iterator<SimpleQueryClause> iterator() {
		return clauses.iterator();
	}
}
