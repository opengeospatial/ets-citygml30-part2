package org.opengis.cite.citygml30part2;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;

import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.*;
import javax.ws.rs.core.MediaType;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.opengis.cite.citygml30part2.util.ClientUtils;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.*;
import static org.opengis.cite.citygml30part2.util.ValidationUtils.getXmlns;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * A supporting base class that sets up a common test fixture. These
 * configuration methods are invoked before those defined in a subclass.
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
    protected ClientResponse response;

    protected Document testSubject;

    /**
     * Initializes the common test fixture with a client component for 
     * interacting with HTTP endpoints.
     *
     * @param testContext The test context that contains all the information for
     * a test run, including suite attributes.
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
     * Obtains the (XML) response entity as a DOM Document. This convenience
     * method wraps a static method call to facilitate unit testing (Mockito
     * workaround).
     *
     * @param response A representation of an HTTP response message.
     * @param targetURI The target URI from which the entity was retrieved (may
     * be null).
     * @return A Document representing the entity.
     *
     * @see ClientUtils#getResponseEntityAsDocument
     */
    public Document getResponseEntityAsDocument(ClientResponse response,
            String targetURI) {
        return ClientUtils.getResponseEntityAsDocument(response, targetURI);
    }

    /**
     * Builds an HTTP request message that uses the GET method. This convenience
     * method wraps a static method call to facilitate unit testing (Mockito
     * workaround).
     *
     * @param endpoint A URI indicating the target resource.
     * @param qryParams A Map containing query parameters (may be null);
     * @param mediaTypes A list of acceptable media types; if not specified,
     * generic XML ("application/xml") is preferred.
     * @return A ClientRequest object.
     *
     * @see ClientUtils#buildGetRequest
     */
    public ClientRequest buildGetRequest(URI endpoint,
            Map<String, String> qryParams, MediaType... mediaTypes) {
        return ClientUtils.buildGetRequest(endpoint, qryParams, mediaTypes);
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

    public ArrayList<String> GetToValidateXsdPathArrayList(Document doc){
		//
		HashMap<String, String> hashMap = new LinkedHashMap<String, String>();
        hashMap.put(getXmlns("CORE"), XSD_CORE);
        hashMap.put(getXmlns("APPEARANCE"), XSD_APPEARANCE);
        hashMap.put(getXmlns("BRIDGE"), XSD_BRIDGE);
        hashMap.put(getXmlns("BUILDING"), XSD_BUILDING);
        hashMap.put(getXmlns("CITYFURNITURE"), XSD_CITYFURNITURE);
        hashMap.put(getXmlns("CITYOBJECTGROUP"), XSD_CITYOBJECTGROUP);
        hashMap.put(getXmlns("CONSTRUCTION"), XSD_CONSTRUCTION);
        hashMap.put(getXmlns("DYNAMIZER"), XSD_DYNAMIZER);
        hashMap.put(getXmlns("GENERICS"), XSD_GENERICS);
        hashMap.put(getXmlns("LANDUSE"), XSD_LANDUSE);
        hashMap.put(getXmlns("POINTCLOUD"), XSD_POINTCLOUD);
        hashMap.put(getXmlns("RELIEF"), XSD_RELIEF);
        hashMap.put(getXmlns("TRANSPORTATION"), XSD_TRANSPORTATION);
        hashMap.put(getXmlns("TUNNEL"), XSD_TUNNEL);
        hashMap.put(getXmlns("VEGETATION"), XSD_VEGETATION);
        hashMap.put(getXmlns("VERSIONING"), XSD_VERSIONING);
        hashMap.put(getXmlns("WATERBODY"), XSD_WATERBODY);
		//

        Element rootElement = doc.getDocumentElement();
		NamedNodeMap namedNodeMap = rootElement.getAttributes();
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node attr = namedNodeMap.item(i);
			String attrName = attr.getNodeName();
			String namespaceUri = attr.getNodeValue();
			if (attrName.contains("xmlns")) {
				if (hashMap.containsKey(namespaceUri)) {
					arrayList.add(hashMap.get(namespaceUri));
					//System.out.println(attr.getNodeName()+ " = \"" + attr.getNodeValue() + "\"");
				}
			}
		}

		return arrayList;
	}

}
