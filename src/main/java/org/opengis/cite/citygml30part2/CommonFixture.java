package org.opengis.cite.citygml30part2;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.glassfish.jersey.client.ClientRequest;
import org.opengis.cite.citygml30part2.util.ClientUtils;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.w3c.dom.Document;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;

/**
 * A supporting base class that sets up a common test fixture. These configuration methods
 * are invoked before those defined in a subclass.
 */
public class CommonFixture {

	/**
	 * Root test suite package (absolute path).
	 */
	public static final String ROOT_PKG_PATH = "/org/opengis/cite/citygml30part2/";

	/**
	 * HTTP client component (JAX-RS Client API).
	 */
	protected Client client;

	/**
	 * An HTTP request message.
	 */
	protected ClientRequest request;

	/**
	 * An HTTP response message.
	 */
	protected Response response;

	protected Document testSubject;

	/**
	 * Initializes the common test fixture with a client component for interacting with
	 * HTTP endpoints.
	 * @param testContext The test context that contains all the information for a test
	 * run, including suite attributes.
	 */
	@BeforeClass
	public void initCommonFixture(ITestContext testContext) {
		Object obj = testContext.getSuite().getAttribute(SuiteAttribute.CLIENT.getName());
		if (null != obj) {
			this.client = Client.class.cast(obj);
		}
		obj = testContext.getSuite().getAttribute(SuiteAttribute.TEST_SUBJECT.getName());
		if (null == obj) {
			throw new SkipException("Test subject not found in ITestContext.");
		}
		if (Document.class.isAssignableFrom(obj.getClass())) {
			this.testSubject = Document.class.cast(obj);
			this.testSubject.getDocumentElement().normalize();
		}
	}

	@BeforeMethod
	public void clearMessages() {
		this.request = null;
		this.response = null;
	}

	/**
	 * Obtains the (XML) response entity as a DOM Document. This convenience method wraps
	 * a static method call to facilitate unit testing (Mockito workaround).
	 * @param response A representation of an HTTP response message.
	 * @param targetURI The target URI from which the entity was retrieved (may be null).
	 * @return A Document representing the entity.
	 *
	 * @see ClientUtils#getResponseEntityAsDocument
	 */
	public Document getResponseEntityAsDocument(Response response, String targetURI) {
		return ClientUtils.getResponseEntityAsDocument(response, targetURI);
	}

	/**
	 * Transform XML Document to UTF-8 String
	 * @param xmlDoc The XML Document
	 * @return A String data type of XML Document
	 * @throws Exception TransformerConfigurationException, TransformerException
	 */
	public String TransformXMLDocumentToXMLString(Document xmlDoc) throws Exception {
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(xmlDoc), new StreamResult(out));
		return out.toString();
	}

}
