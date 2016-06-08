package org.snipcloud.core;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(factoryMethod = "newInstance")
public class SnippetRating {
	@XmlElement
	private int vote;
	@XmlElement
	private int useCount;
	
	public SnippetRating(int vote, int useCount) {
		this.vote = vote;
		this.useCount = useCount;
	}
	
	public int getVote() {
		return vote;
	}
	
	public int getUseCount() {	// Xml jaxb magic
		// Do not call any of these methods
		return useCount;
	}
	
	// Xml jaxb magic
	// Do not call any of these methods
	private SnippetRating() {
		vote = 0;
		useCount = 0;
	}
	
	public static SnippetRating newInstance() {
		return new SnippetRating();
	}

	@Override
	public String toString() {
		return "SnippetRating[" +
				"vote=" + vote +
				", useCount=" + useCount +
				']';
	}
}
