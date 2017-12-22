package crawler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerUtils {
	/**
	 * Returns a parsed Document, or null if the URI cannot be parsed.
	 * 
	 * @param uri
	 *            the URI to parse.
	 */
	public static Document parse(URI uri) {
		File file = new File(uri);
		try {
			return Jsoup.parse(file, null, uri.toString());
		} catch (IOException ioe) {
			return null;
		}
	}

	/**
	 * Returns the list of links in the Document.
	 * 
	 * @param document a document, as returned by parse
	 * @return 
	 */
	public static List<URI> getFileUriLinks(Document document) {
		Elements links = document.select("a[href]");
		List<URI> fileUris = new ArrayList<>();
		for (Element link : links) {
			String absHref = link.attr("abs:href");
			try {
				URI nextUri = new URI(absHref);
				if (!nextUri.getScheme().equals("file")) {
					continue;
				}
				fileUris.add(nextUri);
			} catch (URISyntaxException use) {
				continue;
			}
		}
		return fileUris;
	}
}
