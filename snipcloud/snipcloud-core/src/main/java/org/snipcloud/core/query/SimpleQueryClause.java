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
public class SimpleQueryClause implements Iterable<SimpleQueryLiteral>{
	@XmlElement
	private Set<SimpleQueryLiteral> literals;

	public SimpleQueryClause(Set<SimpleQueryLiteral> literals) {
		this.literals = literals;
	}
	
	public SimpleQueryClause() {
		literals = new HashSet<>(); 
	}

	public boolean match(Snippet snippet) {
		for (SimpleQueryLiteral literal : literals) {
			if (literal.match(snippet)) {
				return true;
			}
		}
		return false;
	}

	public Set<SimpleQueryLiteral> getLiterals() {
		return literals;
	}
	
	public String toString() {
			StringBuilder sb = new StringBuilder();
			Iterator<SimpleQueryLiteral> literalsIt = literals.iterator();
			
			while (literalsIt.hasNext()) {
				sb.append(literalsIt.next().toString());
				if (literalsIt.hasNext()) {
					sb.append(",");
				}
			}
			
			return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((literals == null) ? 0 : literals.hashCode());
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
		if (!(obj instanceof SimpleQueryClause)) {
			return false;
		}
		SimpleQueryClause other = (SimpleQueryClause) obj;
		if (literals == null) {
			if (other.literals != null) {
				return false;
			}
		} else if (!literals.equals(other.literals)) {
			return false;
		}
		return true;
	}

	// Xml jaxb magic
	// Do not call any of these methods
	
	public static SimpleQueryClause newInstance() {
		return new SimpleQueryClause();
	}

	@Override
	public Iterator<SimpleQueryLiteral> iterator() {
		return literals.iterator();
	}
}
