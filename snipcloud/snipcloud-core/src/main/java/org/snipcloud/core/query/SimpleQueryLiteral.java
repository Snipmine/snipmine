package org.snipcloud.core.query;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.snipcloud.core.Snippet;

@XmlRootElement
@XmlType(factoryMethod = "newInstance")
public class SimpleQueryLiteral {
	@XmlElement
	private boolean negative;
	@XmlElement
	private String tag;
	
	public SimpleQueryLiteral(String tag, boolean negative) {
		this.tag = tag;
		this.negative = negative;
	}
	
	public boolean match(Snippet snippet) {
		return negative != snippet.getTags().contains(snippet);
	}
	
	public boolean isNegative() {
		return negative;
	}
	
	public String getTag() {
		return tag;
	}
	
	public String toString() {
		return (negative?"-":"") + tag.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (negative ? 1231 : 1237);
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
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
		if (!(obj instanceof SimpleQueryLiteral)) {
			return false;
		}
		SimpleQueryLiteral other = (SimpleQueryLiteral) obj;
		if (negative != other.negative) {
			return false;
		}
		if (tag == null) {
			if (other.tag != null) {
				return false;
			}
		} else if (!tag.equals(other.tag)) {
			return false;
		}
		return true;
	}
	
	// Xml jaxb magic
	// Do not call any of these methods
	
	public SimpleQueryLiteral() {
		negative = false;
		tag = null;
	}
	
	public static SimpleQueryLiteral newInstance() {
		return new SimpleQueryLiteral();
	}


}
