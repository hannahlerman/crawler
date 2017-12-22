package document;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * A document, uniquely identified and sortable by URI, and containing to
 * document's raw text and links. The links are in the order they appeared in
 * the document.
 * 
 * @author liberato
 *
 */
public class RetrievedDocument implements Comparable<RetrievedDocument> {
	public final URI uri;
	public final String text;
	public final List<URI> links;

	public RetrievedDocument(URI uri, String text, List<URI> links) {
		this.uri = uri;
		this.text = text;
		this.links = Collections.unmodifiableList(links);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RetrievedDocument other = (RetrievedDocument) obj;
		if (uri == null) {
			if (other.uri != null) return false;
		} else {
			if (!uri.equals(other.uri)) return false; 
		}
		return true;
	}

	@Override
	public int compareTo(RetrievedDocument other) {
		return uri.compareTo(other.uri);
	}
}
