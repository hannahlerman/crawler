package crawler;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


import document.RetrievedDocument;


public class UriCrawlerTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(10000); // 10 seconds

	private static final String DEADLINK_CONTENTS = "dead link We're on a road to nowhere.";
	private static final String DUPLICATE_CONTENTS = "duplicates I link to trivial.html twice.";
	private static final String LINKED_TO_TRIVIAL_CONTENTS = "a file with a link I link to trivial.html.";
	private static final String TRIVIAL_CONTENTS = "A trivial example This is a trivial example HTML file.";
	private static final String RESOURCE_DIR = "test" + File.separator + "resources" + File.separator;
	private static final URI DEADLINK_URI = new File(RESOURCE_DIR + "deadLink.html").getAbsoluteFile().toURI();
	private static final URI DUPLICATE_URI = new File(RESOURCE_DIR + "duplicateLink.html").getAbsoluteFile().toURI();
	private static final URI LINKED_TO_TRIVIAL_URI = new File(RESOURCE_DIR + "linkedToTrivial.html").getAbsoluteFile().toURI();
	private static final URI TRIVIAL_URI = new File(RESOURCE_DIR + "trivial.html").getAbsoluteFile().toURI();
	
	@Test
	public void testAddUri() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(1);
		uriCrawler.addUri(TRIVIAL_URI);
	}

	@Test
	public void testVisitedAllOne() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(1);
		URI uri = TRIVIAL_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitAll();
	}

	@Test(expected = NoUnvisitedUrisException.class)
	public void testNoUnvisitedUris() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(2);
		URI uri = TRIVIAL_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitOne();
		uriCrawler.visitOne();
	}

	@Test
	public void testVisitedOneUriIsVisited() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(1);
		URI uri = TRIVIAL_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitOne();
		assertTrue(uriCrawler.getVistedUris().contains(uri));
	}

	@Test
	public void testVisitedOneUriDocument() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(1);
		URI uri = TRIVIAL_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitOne();
		
		Set<RetrievedDocument> set = uriCrawler.getVisitedDocuments();
		assertFalse(set.isEmpty());
		
		List<RetrievedDocument> list = new ArrayList<>(set);
		RetrievedDocument document = list.get(0);
		assertEquals(uri, document.uri);
		assertEquals(TRIVIAL_CONTENTS, document.text);
		assertTrue(document.links.isEmpty());		
	}
	
	@Test
	public void testVisitAllOne() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(1);
		URI uri = TRIVIAL_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitAll();

		Set<RetrievedDocument> set = uriCrawler.getVisitedDocuments();
		assertFalse(set.isEmpty());
		
		List<RetrievedDocument> list = new ArrayList<>(set);
		RetrievedDocument document = list.get(0);
		assertEquals(uri, document.uri);
		assertEquals(TRIVIAL_CONTENTS, document.text);
		assertTrue(document.links.isEmpty());		
	}

	@Test
	public void testVisitAllTwice() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(1);
		URI uri = TRIVIAL_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitAll();
		uriCrawler.visitAll();		

		Set<RetrievedDocument> set = uriCrawler.getVisitedDocuments();
		assertFalse(set.isEmpty());
		
		List<RetrievedDocument> list = new ArrayList<>(set);
		RetrievedDocument document = list.get(0);
		assertEquals(uri, document.uri);
		assertEquals(TRIVIAL_CONTENTS, document.text);
		assertTrue(document.links.isEmpty());		
	}
	
	@Test(expected = MaximumVisitsExceededException.class)
	public void testMaxVisitsExceeded() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(1);
		URI uri = LINKED_TO_TRIVIAL_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitOne();
		uriCrawler.visitOne();
	}

	@Test
	public void testVisitDeadAttempted() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(2);
		URI uri = DEADLINK_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitOne();
		uriCrawler.visitOne();

		Set<RetrievedDocument> set = uriCrawler.getVisitedDocuments();
		assertFalse(set.isEmpty());
		
		List<RetrievedDocument> list = new ArrayList<>(set);
		RetrievedDocument document = list.get(0);
		assertEquals(uri, document.uri);
		assertEquals(DEADLINK_CONTENTS, document.text);
		assertFalse(document.links.isEmpty());		
	}
	
	@Test
	public void testVisitOneOfTwo() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(2);
		URI uri = LINKED_TO_TRIVIAL_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitOne();
		
		Set<RetrievedDocument> set = uriCrawler.getVisitedDocuments();
		assertFalse(set.isEmpty());
		
		List<RetrievedDocument> list = new ArrayList<>(set);
		RetrievedDocument document = list.get(0);
		assertEquals(uri, document.uri);
		assertEquals(LINKED_TO_TRIVIAL_CONTENTS, document.text);
		assertFalse(document.links.isEmpty());		
	}
	
	@Test
	public void testVisitTwoOfTwo() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(2);
		URI uri = LINKED_TO_TRIVIAL_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitOne();
		uriCrawler.visitOne();
		
		Set<RetrievedDocument> set = uriCrawler.getVisitedDocuments();
		assertEquals(2, set.size());
		
		List<RetrievedDocument> list = new ArrayList<>(set);
		list.sort(null);
		RetrievedDocument document = list.get(0);
		assertEquals(uri, document.uri);
		assertEquals(LINKED_TO_TRIVIAL_CONTENTS, document.text);
		assertEquals(1, document.links.size());

		document = list.get(1);
		assertEquals(TRIVIAL_URI, document.uri);
		assertEquals(TRIVIAL_CONTENTS, document.text);
		assertTrue(document.links.isEmpty());
	}
	
	@Test
	public void testVisitAllTwo() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(2);
		URI uri = LINKED_TO_TRIVIAL_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitAll();
		
		Set<RetrievedDocument> set = uriCrawler.getVisitedDocuments();
		assertEquals(2, set.size());
		
		List<RetrievedDocument> list = new ArrayList<>(set);
		list.sort(null);
		RetrievedDocument document = list.get(0);
		assertEquals(uri, document.uri);
		assertEquals(LINKED_TO_TRIVIAL_CONTENTS, document.text);
		assertEquals(1, document.links.size());

		document = list.get(1);
		assertEquals(TRIVIAL_URI, document.uri);
		assertEquals(TRIVIAL_CONTENTS, document.text);
		assertTrue(document.links.isEmpty());
	}

	@Test
	public void testDuplicateOneByOne() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(2);
		URI uri = DUPLICATE_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitOne();
		uriCrawler.visitOne();
		
		Set<RetrievedDocument> set = uriCrawler.getVisitedDocuments();
		assertEquals(2, set.size());
		
		List<RetrievedDocument> list = new ArrayList<>(set);
		list.sort(null);
		RetrievedDocument document = list.get(0);
		assertEquals(uri, document.uri);
		assertEquals(DUPLICATE_CONTENTS, document.text);
		assertEquals(2, document.links.size());

		document = list.get(1);
		assertEquals(TRIVIAL_URI, document.uri);
		assertEquals(TRIVIAL_CONTENTS, document.text);
		assertTrue(document.links.isEmpty());
	}
	
	@Test
	public void testDuplicateAll() throws Exception {
		UriCrawler uriCrawler = new UriCrawler(2);
		URI uri = DUPLICATE_URI;
		uriCrawler.addUri(uri);
		uriCrawler.visitAll();
		
		Set<RetrievedDocument> set = uriCrawler.getVisitedDocuments();
		assertEquals(2, set.size());
		
		List<RetrievedDocument> list = new ArrayList<>(set);
		list.sort(null);
		RetrievedDocument document = list.get(0);
		assertEquals(uri, document.uri);
		assertEquals(DUPLICATE_CONTENTS, document.text);
		assertEquals(2, document.links.size());

		document = list.get(1);
		assertEquals(TRIVIAL_URI, document.uri);
		assertEquals(TRIVIAL_CONTENTS, document.text);
		assertTrue(document.links.isEmpty());
	}
}
