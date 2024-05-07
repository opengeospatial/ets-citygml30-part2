package org.opengis.cite.citygml30part2;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class VerifyETSAssert {
    private static final String GML_NS = "http://www.opengis.net/gml/3.2";
    private static final String CORE_NS = "http://www.opengis.net/citygml/3.0";

    private static DocumentBuilder docBuilder;
    private static SchemaFactory factory;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public VerifyETSAssert() {
    }

    @BeforeClass
    public static void setUpClass() throws ParserConfigurationException {
        factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        docBuilder = dbf.newDocumentBuilder();
    }

    @Test
    public void validateUsingSchemaHints_expect2Errors() throws SAXException {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("2 schema validation error(s) detected");
        URL url = this.getClass().getResource("/Gamma.xml");
        Schema schema = factory.newSchema();
        Validator validator = schema.newValidator();
        ETSAssert.assertSchemaValid(validator, new StreamSource(url.toString()));
    }

    @Test
    public void assertXPathWithNamespaceBindings() throws SAXException,
            IOException {
        Document doc = docBuilder.parse(this.getClass().getResourceAsStream("/LocalCRS_CityGML3.gml"));
        Map<String, String> nsBindings = new HashMap<String, String>();
        nsBindings.put(GML_NS, "gml");
        String xpath = "//gml:description";
        ETSAssert.assertXPath(xpath, doc, nsBindings);
    }

    @Test
    public void assertXPath_expectTrue() throws SAXException, IOException {
        Document doc = docBuilder.parse(this.getClass().getResourceAsStream("/LocalCRS_CityGML3.gml"));
        Map<String, String> nsBindings = new HashMap<String, String>();
        nsBindings.put(GML_NS, "gml");
        String xpath = "//*/gml:EngineeringCRS[@gml:id='local-CRS-1']/gml:scope = 'CityGML'";
        ETSAssert.assertXPath(xpath, doc, nsBindings);
    }

    @Test
    public void assertXPath_expectFalse() throws SAXException, IOException {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("Unexpected result evaluating XPath expression");
        Document doc = docBuilder.parse(this.getClass().getResourceAsStream("/LocalCRS_CityGML3.gml"));
        Map<String, String> nsBindings = new HashMap<String, String>();
        nsBindings.put(GML_NS, "gml");
        String xpath = "//*/gml:EngineeringCRS[@gml:id='local-CRS-1']/gml:scope = 'NOTCityGML'";
        ETSAssert.assertXPath(xpath, doc, nsBindings);
    }
}
