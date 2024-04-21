package org.opengis.cite.citygml30part2.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathExpressionException;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Verifies the behavior of the XMLUtils class.
 */
public class VerifyXMLUtils {

    private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
    private static final String EX_NS = "http://example.org/ns1";

    private static final String GML_NS = "http://www.opengis.net/gml/3.2";
    private static final String CORE_NS = "http://www.opengis.net/citygml/3.0";

    private static DocumentBuilder docBuilder;

    public VerifyXMLUtils() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        docBuilder = dbf.newDocumentBuilder();
    }

    @Test
    public void writeDocToString() throws SAXException, IOException {
        Document doc = docBuilder.parse(this.getClass().getResourceAsStream("/LocalCRS_CityGML3.gml"));
        String content = XMLUtils.writeNodeToString(doc);
        // Regular expression to match XML comments
        Matcher matcher = Pattern.compile("<!--(.*?)-->", Pattern.DOTALL).matcher(content);
        content =  matcher.replaceAll("");
        Assert.assertTrue("String should start with '<core:CityModel'", content.startsWith("<core:CityModel"));
    }

    @Test
    public void evaluateXPathExpression_match() throws XPathExpressionException, SAXException, IOException {
        Document doc = docBuilder.parse(this.getClass().getResourceAsStream("/LocalCRS_CityGML3.gml"));
        Map<String, String> nsBindings = new HashMap<String, String>();
        nsBindings.put(CORE_NS, "core");
        nsBindings.put(GML_NS, "gml");
        String expr = "//core:engineeringCRS/gml:EngineeringCRS";
        NodeList results = XMLUtils.evaluateXPath(doc, expr, nsBindings);
        Assert.assertTrue("Expected 1 node in results.",
                results.getLength() == 1);
        Assert.assertEquals("EngineeringCRS", results.item(0).getLocalName());
    }

    @Test
    public void evaluateXPathExpression_noMatch() throws XPathExpressionException, SAXException, IOException {
        Document doc = docBuilder.parse(this.getClass().getResourceAsStream("/LocalCRS_CityGML3.gml"));
        Map<String, String> nsBindings = new HashMap<String, String>();
        nsBindings.put(CORE_NS, "core");
        nsBindings.put(GML_NS, "gml");
        String expr = "//core:engineeringCRS/gml:EngineeringCRS2";
        NodeList results = XMLUtils.evaluateXPath(doc, expr, nsBindings);

        Assert.assertTrue("Expected empty results.", results.getLength() == 0);
    }

    @Test(expected = XPathExpressionException.class)
    public void evaluateXPathExpression_booleanResult() throws XPathExpressionException, SAXException, IOException {
        Document doc = docBuilder.parse(this.getClass().getResourceAsStream("/LocalCRS_CityGML3.gml"));
        String expr = "count(//core:cityObjectMember) > 0";
        Map<String, String> nsBindings = new HashMap<String, String>();
        nsBindings.put(CORE_NS, "core");
        NodeList results = XMLUtils.evaluateXPath(doc, expr, nsBindings);
        Assert.assertNull(results);
    }

    @Test
    public void createElement_Alpha() {
        QName qName = new QName("http://example.org", "Alpha");
        Element elem = XMLUtils.createElement(qName);
        Assert.assertEquals("Alpha", elem.getLocalName());
        Assert.assertNull(elem.getParentNode());
        Assert.assertNotNull(elem.getOwnerDocument());
    }

    @Test
    public void evaluateXPath2ExpressionAgainstDocument() throws SAXException, IOException, SaxonApiException, XPathException {
        Document doc = docBuilder.parse(this.getClass().getResourceAsStream("/LocalCRS_CityGML3.gml"));
        String expr = "matches(//gml:EngineeringCRS/gml:description, 'engineering')";
        Map<String, String> nsBindings = new HashMap<String, String>();
        nsBindings.put(GML_NS, "gml");
        XdmValue result = XMLUtils.evaluateXPath2(new DOMSource(doc), expr, nsBindings);
        Assert.assertTrue("Expected non-empty result.", result.size() > 0);
        Assert.assertEquals("Result has unexpected string value.", "true",
                result.getUnderlyingValue().getStringValue());
    }

    @Test
    public void evaluateXPath2ExpressionAgainstElement() throws SAXException, IOException, SaxonApiException, XPathException {
        Document doc = docBuilder.parse(this.getClass().getResourceAsStream("/LocalCRS_CityGML3.gml"));
        Node entry = doc.getElementsByTagNameNS(GML_NS, "EngineeringCRS").item(0);
        String expr = "matches(gml:description, 'engineering')";
        Map<String, String> nsBindings = new HashMap<String, String>();
        nsBindings.put(GML_NS, "gml");
        XdmValue result = XMLUtils.evaluateXPath2(new DOMSource(entry), expr, nsBindings);
        Assert.assertTrue("Expected non-empty result.", result.size() > 0);
        Assert.assertEquals("Result has unexpected string value.", "true",
                result.getUnderlyingValue().getStringValue());
    }

    @Test
    public void expandCharacterEntity() {
        String text = "Ce n&apos;est pas";
        String result = XMLUtils.expandReferencesInText(text);
        Assert.assertTrue("Expected result to contain an apostrophe (')",
                result.contains("'"));
    }

    @Test
    public void expandNumericCharacterReference() {
        String text = "Montr&#xe9;al";
        String result = XMLUtils.expandReferencesInText(text);
        Assert.assertEquals("Expected result to contain character é (U+00E9)",
                "Montréal", result);
    }
}
