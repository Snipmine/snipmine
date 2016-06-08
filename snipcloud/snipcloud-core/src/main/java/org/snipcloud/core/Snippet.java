package org.snipcloud.core;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(factoryMethod = "newInstance")
public class Snippet implements Comparable<Snippet> {
	@XmlElement
	private final long id;
	@XmlElement
	private final List<String> tags;
	@XmlElement
	private final long authorId;
	@XmlElement
	private final String code;
	@XmlElement
	private final SnippetRating rating;
	@XmlElement
	private final Date lastUpdate;

	/**
	 * Creates a full snippet with all information
	 */
	public Snippet(final long id, final List<String> tags, final long authorId, final String source,
			final SnippetRating rating, final Date lastUpdate) {
		this.id = id;
		this.tags = tags;
		this.authorId = authorId;
		code = source;
		this.rating = rating;
		this.lastUpdate = lastUpdate;
	}

	public Snippet(final List<String> tags, final String source) {
		this(0, tags, -1L, source, null, null);
	}

	public long getId() {
		return id;
	}

	public List<String> getTags() {
		return Collections.unmodifiableList(tags);
	}

	public SnippetRating getRating() {
		return rating;
	}
	
	public String getCode() {
		return code;
	}

	public long getAuthorId() {
		return authorId;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}



	@Override
	public String toString() {
		return "Snippet [id=" + id + ", tags=" + tags + ", author=" + authorId
				+ ", rating=" + rating + ", lastUpdate=" + lastUpdate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Snippet other = (Snippet) obj;
		if (id != other.id) return false;
		return true;
	}

	@Override
	public int compareTo(final Snippet o) {
		return (int) (id - o.id);
	}

	// Xml jaxb magic
	// Do not call any of these methods
	private Snippet() {
		id = -1L;
		tags = null;
		authorId = -1L;
		code = null;
		rating = null;
		lastUpdate = null;
	}

	public static Snippet newInstance() {
		return new Snippet();
	}
}
