package org.opengis.cite.citygml30part2.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Verifies the behavior of the URIUtils class.
 */
public class VerifyURIUtils {

	public VerifyURIUtils() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@Ignore
	@Test
	// comment out @Ignore to run test (requires network connection)
	public void resolveHttpUriAsDocument() throws SAXException, IOException {
		URI uriRef = URI.create("http://www.w3schools.com/xml/note.xml");
		Document doc = URIUtils.parseURI(uriRef);
		Assert.assertNotNull(doc);
		Assert.assertEquals("Document element has unexpected [local name].", "note",
				doc.getDocumentElement().getLocalName());
	}

	@Test
	// comment out @Ignore to run test (requires network connection)
	public void resolveHttpUriAsFile() throws SAXException, IOException {
		URI uriRef = URI.create("http://www.w3schools.com/xml/note.xml");
		File file = URIUtils.dereferenceURI(uriRef);
		Assert.assertNotNull(file);
		Assert.assertTrue("File should not be empty", file.length() > 0);
	}

	@Test
	public void resolveClasspathResource() throws SAXException, IOException, URISyntaxException {
		URL url = this.getClass().getResource("/LocalCRS_CityGML3.gml");

		Document doc = URIUtils.parseURI(url.toURI());
		Assert.assertNotNull(doc);
		Assert.assertEquals("Document element has unexpected [local name].", "CityModel",
				doc.getDocumentElement().getLocalName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void resolveMissingClasspathResource() throws SAXException, URISyntaxException, IOException {
		URL url = this.getClass().getResource("/LocalCRS_CityGML3.xml");
		URI uri = (null != url) ? url.toURI() : null;
		Document doc = URIUtils.parseURI(uri);
		Assert.assertNull(doc);
	}

}
