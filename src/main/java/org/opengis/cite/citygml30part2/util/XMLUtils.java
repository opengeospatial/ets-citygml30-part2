package org.opengis.cite.citygml30part2.util;

import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_APPEARANCE;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_BRIDGE;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_BUILDING;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_CITYFURNITURE;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_CITYOBJECTGROUP;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_CONSTRUCTION;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_CORE;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_DYNAMIZER;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_GENERICS;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_LANDUSE;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_POINTCLOUD;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_RELIEF;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_TRANSPORTATION;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_TUNNEL;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_VEGETATION;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_VERSIONING;
import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_WATERBODY;
import static org.opengis.cite.citygml30part2.util.ValidationUtils.getXmlns;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sf.saxon.s9api.DOMDestination;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

/**
 * Provides various utility methods for accessing or manipulating XML representations.
 */
public class XMLUtils {

	private static final Logger LOGR = Logger.getLogger(XMLUtils.class.getPackage().getName());

	private static final XMLInputFactory STAX_FACTORY = initXMLInputFactory();

	private static final XPathFactory XPATH_FACTORY = initXPathFactory();

	private static XPathFactory initXPathFactory() {
		XPathFactory factory = XPathFactory.newInstance();
		return factory;
	}

	private static XMLInputFactory initXMLInputFactory() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
		return factory;
	}

	/**
	 * Writes the content of a DOM Node to a string. The XML declaration is omitted and
	 * the character encoding is set to "US-ASCII" (any character outside of this set is
	 * serialized as a numeric character reference).
	 * @param node The DOM Node to be serialized.
	 * @return A String representing the content of the given node.
	 */
	public static String writeNodeToString(Node node) {
		if (null == node) {
			return "";
		}
		Writer writer = null;
		try {
			Transformer idTransformer = TransformerFactory.newInstance().newTransformer();
			Properties outProps = new Properties();
			outProps.setProperty(OutputKeys.ENCODING, "UTF-8");
			outProps.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			outProps.setProperty(OutputKeys.INDENT, "yes");
			idTransformer.setOutputProperties(outProps);
			writer = new StringWriter();
			idTransformer.transform(new DOMSource(node), new StreamResult(writer));
		}
		catch (TransformerException ex) {
			TestSuiteLogger.log(Level.WARNING, "Failed to serialize node " + node.getNodeName(), ex);
		}
		return writer.toString();
	}

	/**
	 * Writes the content of a DOM Node to a byte stream. An XML declaration is always
	 * omitted.
	 * @param node The DOM Node to be serialized.
	 * @param outputStream The destination OutputStream reference.
	 */
	public static void writeNode(Node node, OutputStream outputStream) {
		try {
			Transformer idTransformer = TransformerFactory.newInstance().newTransformer();
			Properties outProps = new Properties();
			outProps.setProperty(OutputKeys.METHOD, "xml");
			outProps.setProperty(OutputKeys.ENCODING, "UTF-8");
			outProps.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			outProps.setProperty(OutputKeys.INDENT, "yes");
			idTransformer.setOutputProperties(outProps);
			idTransformer.transform(new DOMSource(node), new StreamResult(outputStream));
		}
		catch (TransformerException ex) {
			String nodeName = (node.getNodeType() == Node.DOCUMENT_NODE)
					? Document.class.cast(node).getDocumentElement().getNodeName() : node.getNodeName();
			TestSuiteLogger.log(Level.WARNING, "Failed to serialize DOM node: " + nodeName, ex);
		}
	}

	/**
	 * Evaluates an XPath 1.0 expression using the given context and returns the result as
	 * a node set.
	 * @param context The context node.
	 * @param expr An XPath expression.
	 * @param namespaceBindings A collection of namespace bindings for the XPath
	 * expression, where each entry maps a namespace URI (key) to a prefix (value).
	 * Standard bindings do not need to be declared (see
	 * {@link NamespaceBindings#withStandardBindings()}.
	 * @return A NodeList containing nodes that satisfy the expression (it may be empty).
	 * @throws XPathExpressionException If the expression cannot be evaluated for any
	 * reason.
	 */
	public static NodeList evaluateXPath(Node context, String expr, Map<String, String> namespaceBindings)
			throws XPathExpressionException {
		Object result = evaluateXPath(context, expr, namespaceBindings, XPathConstants.NODESET);
		if (!NodeList.class.isInstance(result)) {
			throw new XPathExpressionException("Expression does not evaluate to a NodeList: " + expr);
		}
		return (NodeList) result;
	}

	/**
	 * Evaluates an XPath expression using the given context and returns the result as the
	 * specified type.
	 *
	 * <p>
	 * <strong>Note:</strong> The Saxon implementation supports XPath 2.0 expressions when
	 * using the JAXP XPath APIs (the default implementation will throw an exception).
	 * </p>
	 * @param context The context node.
	 * @param expr An XPath expression.
	 * @param namespaceBindings A collection of namespace bindings for the XPath
	 * expression, where each entry maps a namespace URI (key) to a prefix (value).
	 * Standard bindings do not need to be declared (see
	 * {@link NamespaceBindings#withStandardBindings()}.
	 * @param returnType The desired return type (as declared in {@link XPathConstants} ).
	 * @return The result converted to the desired returnType.
	 * @throws XPathExpressionException If the expression cannot be evaluated for any
	 * reason.
	 */
	public static Object evaluateXPath(Node context, String expr, Map<String, String> namespaceBindings,
			QName returnType) throws XPathExpressionException {
		NamespaceBindings bindings = NamespaceBindings.withStandardBindings();
		bindings.addAllBindings(namespaceBindings);
		XPathFactory factory = XPATH_FACTORY;
		// WARNING: If context node is Saxon NodeOverNodeInfo, the factory must
		// use the same Configuration object to avoid IllegalArgumentException
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(bindings);
		Object result = xpath.evaluate(expr, context, returnType);
		return result;
	}

	/**
	 * Evaluates an XPath 2.0 expression using the Saxon s9api interfaces.
	 * @param xmlSource The XML Source.
	 * @param expr The XPath expression to be evaluated.
	 * @param nsBindings A collection of namespace bindings required to evaluate the XPath
	 * expression, where each entry maps a namespace URI (key) to a prefix (value); this
	 * may be {@code null} if not needed.
	 * @return An XdmValue object representing a value in the XDM data model; this is a
	 * sequence of zero or more items, where each item is either an atomic value or a
	 * node.
	 * @throws SaxonApiException If an error occurs while evaluating the expression; this
	 * always wraps some other underlying exception.
	 */
	public static XdmValue evaluateXPath2(Source xmlSource, String expr, Map<String, String> nsBindings)
			throws SaxonApiException {
		Processor proc = new Processor(false);
		XPathCompiler compiler = proc.newXPathCompiler();
		if (null != nsBindings) {
			for (String nsURI : nsBindings.keySet()) {
				compiler.declareNamespace(nsBindings.get(nsURI), nsURI);
			}
		}
		XPathSelector xpath = compiler.compile(expr).load();
		DocumentBuilder builder = proc.newDocumentBuilder();
		XdmNode node = null;
		if (DOMSource.class.isInstance(xmlSource)) {
			DOMSource domSource = (DOMSource) xmlSource;
			node = builder.wrap(domSource.getNode());
		}
		else {
			node = builder.build(xmlSource);
		}
		xpath.setContextItem(node);
		return xpath.evaluate();
	}

	/**
	 * Evaluates an XQuery 1.0 expression using the Saxon s9api interfaces.
	 * @param source The XML Source.
	 * @param query The query expression.
	 * @param nsBindings A collection of namespace bindings required to evaluate the
	 * query, where each entry maps a namespace URI (key) to a prefix (value).
	 * @return An XdmValue object representing a value in the XDM data model.
	 * @throws SaxonApiException If an error occurs while evaluating the query (this
	 * always wraps some other underlying exception).
	 */
	public static XdmValue evaluateXQuery(Source source, String query, Map<String, String> nsBindings)
			throws SaxonApiException {
		Processor proc = new Processor(false);
		XQueryCompiler xqCompiler = proc.newXQueryCompiler();
		if (null != nsBindings) {
			for (String nsURI : nsBindings.keySet()) {
				xqCompiler.declareNamespace(nsBindings.get(nsURI), nsURI);
			}
		}
		XQueryExecutable xqExec = xqCompiler.compile(query);
		XQueryEvaluator xqEval = xqExec.load();
		xqEval.setSource(source);
		return xqEval.evaluate();
	}

	/**
	 * Creates a new Element having the specified qualified name. The element must be
	 * {@link Document#adoptNode(Node) adopted} when inserted into another Document.
	 * @param qName A QName object.
	 * @return An Element node (with a Document owner but no parent).
	 */
	public static Element createElement(QName qName) {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		Element elem = doc.createElementNS(qName.getNamespaceURI(), qName.getLocalPart());
		return elem;
	}

	/**
	 * Returns a List of all descendant Element nodes having the specified [namespace
	 * name] property. The elements are listed in document order.
	 * @param node The node to search from.
	 * @param namespaceURI An absolute URI denoting a namespace name.
	 * @return A List containing elements in the specified namespace; the list is empty if
	 * there are no elements in the namespace.
	 */
	public static List<Element> getElementsByNamespaceURI(Node node, String namespaceURI) {
		List<Element> list = new ArrayList<Element>();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (child.getNamespaceURI().equals(namespaceURI))
				list.add((Element) child);
		}
		return list;
	}

	/**
	 * Transforms the content of a DOM Node using a specified XSLT stylesheet.
	 * @param xslt A Source object representing a stylesheet (XSLT 1.0 or 2.0).
	 * @param source A Node representing the XML source. If it is an Element node it will
	 * be imported into a new DOM Document.
	 * @return A DOM Document containing the result of the transformation.
	 */
	public static Document transform(Source xslt, Node source) {
		Document sourceDoc = null;
		Document resultDoc = null;
		try {
			resultDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			if (source.getNodeType() == Node.DOCUMENT_NODE) {
				sourceDoc = (Document) source;
			}
			else {
				sourceDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				sourceDoc.appendChild(sourceDoc.importNode(source, true));
			}
		}
		catch (ParserConfigurationException pce) {
			throw new RuntimeException(pce);
		}
		Processor processor = new Processor(false);
		XsltCompiler compiler = processor.newXsltCompiler();
		try {
			XsltExecutable exec = compiler.compile(xslt);
			XsltTransformer transformer = exec.load();
			transformer.setSource(new DOMSource(sourceDoc));
			transformer.setDestination(new DOMDestination(resultDoc));
			transformer.transform();
		}
		catch (SaxonApiException e) {
			throw new RuntimeException(e);
		}
		return resultDoc;
	}

	/**
	 * Expands character entity ({@literal &name;}) and numeric references (
	 * {@literal &#xhhhh;} or {@literal &dddd;}) that occur within a given string value.
	 * It may be necessary to do this before processing an XPath expression.
	 * @param value A string representing text content.
	 * @return A string with all included references expanded.
	 */
	public static String expandReferencesInText(String value) {
		StringBuilder wrapper = new StringBuilder("<value>");
		wrapper.append(value).append("</value>");
		Reader reader = new StringReader(wrapper.toString());
		String str = null;
		try {
			XMLStreamReader xsr = STAX_FACTORY.createXMLStreamReader(reader);
			xsr.nextTag(); // document element
			str = xsr.getElementText();
		}
		catch (XMLStreamException xse) {
			LOGR.log(Level.WARNING, xse.getMessage(), xse);
		}
		return str;
	}

	/**
	 * Creates a DOM Document with the given Element as the document element. A deep copy
	 * of the element is imported; the source element is not altered.
	 * @param elem An Element node.
	 * @return A Document node.
	 */
	public static Document importElement(Element elem) {
		javax.xml.parsers.DocumentBuilder docBuilder = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			docBuilder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException ex) {
			LOGR.log(Level.WARNING, null, ex);
		}
		Document newDoc = docBuilder.newDocument();
		Node newNode = newDoc.importNode(elem, true);
		newDoc.appendChild(newNode);
		return newDoc;
	}

	/**
	 * Returns a List view of the nodes in the given NodeList collection.
	 * @param nodeList An ordered collection of DOM nodes.
	 * @return A List containing the original sequence of Node objects.
	 */
	public static List<Node> asList(NodeList nodeList) {
		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			nodes.add(nodeList.item(i));
		}
		return nodes;
	}

	/**
	 * Transform XML Document to UTF-8 String
	 * @param xmlDoc The XML Document
	 * @return A String data type of XML Document
	 * @throws Exception TransformerConfigurationException, TransformerException
	 */
	public static String TransformXMLDocumentToXMLString(Document xmlDoc) throws Exception {
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(xmlDoc), new StreamResult(out));
		return out.toString();
	}

	public static boolean isMultipleXMLSchemaValid(String xmlString, String[] arrXsdPath) throws Exception {
		try {
			Schema schema = ValidationUtils.createMultipleSchema(arrXsdPath);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new StringReader(xmlString)));
		}
		catch (IOException | SAXException e) {
			System.out.println("Exception: " + e.getMessage());
			String s = "Exception: " + e.getMessage();
			throw new Exception(s);
		}
		return true;
	}

	public static boolean isMultipleXMLSchemaValid(Document doc, ArrayList<String> arrayList) throws Exception {
		String[] arrayXsdPath = new String[arrayList.size()];
		arrayList.toArray(arrayXsdPath);
		return isMultipleXMLSchemaValid(doc, arrayXsdPath);
	}

	public static boolean isMultipleXMLSchemaValid(Document doc, String[] arrXsdPath) throws Exception {
		try {
			String str = XMLUtils.TransformXMLDocumentToXMLString(doc);
			return isMultipleXMLSchemaValid(str, arrXsdPath);
		}
		catch (Exception e) {
			throw e;
		}
	}

	public static boolean hasChildWithAttribute(Element element, String attributeName) {
		NodeList childNodes = element.getChildNodes();
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = childNodes.item(j);
			if (childNode instanceof Element && ((Element) childNode).hasAttribute(attributeName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isRefValid(String xpathExpr, String attr, List<String> allowedElement, Document doc)
			throws Exception {
		NodeList nodeList = getNodeListByXPath(doc, xpathExpr);

		boolean isRefValid = true;
		for (int i = 0; i < nodeList.getLength() && isRefValid; i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element currentElement = (Element) currentNode;
			if (!currentElement.hasAttribute(attr))
				return false;

			String hrefName = currentElement.getAttribute("xlink:href").replace("#", "");

			String findReferenceExpression = "//*[@gml:id='" + hrefName + "']";

			Node xlinkNode = getNodeByXPath(doc, findReferenceExpression);
			String name = xlinkNode.getNodeName();
			if (!allowedElement.contains(name))
				return false;
		}
		return true;
	}

	public static XPath getCityGMLXPath() {
		XPathFactory xpathfactory = XPathFactory.newInstance();
		XPath xpath = xpathfactory.newXPath();

		Map<String, String> namespaceBindings = NamespaceBindings.getNamespaceMap();

		NamespaceBindings bindings = NamespaceBindings.withStandardBindings();
		bindings.addAllBindings(namespaceBindings);
		xpath.setNamespaceContext(bindings);
		return xpath;
	}

	/**
	 * Return a NodeList of all Element nodes that match the XPath expression
	 * @param doc Document of XML Source
	 * @param expression XPath expression
	 * @return NodeList of Element nodes that match the XPath expression
	 */
	public static NodeList getNodeListByXPath(Document doc, String expression) {
		try {
			XPath xpath = getCityGMLXPath();
			NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
			return nodes;
		}
		catch (Exception exception) {
			System.out.println("Exception: " + exception.getMessage());
			return null;
		}
	}

	public static NodeList getNodeListByXPath(NodeList node, String expression) {
		try {
			XPath xpath = getCityGMLXPath();
			NodeList nodes = (NodeList) xpath.evaluate(expression, node, XPathConstants.NODESET);
			return nodes;
		}
		catch (Exception exception) {
			System.out.println("Exception: " + exception.getMessage());
			return null;
		}
	}

	public static NodeList getNodeListByXPath(Node node, String expression) {
		try {
			XPath xpath = getCityGMLXPath();
			NodeList nodes = (NodeList) xpath.evaluate(expression, node, XPathConstants.NODESET);
			return nodes;
		}
		catch (Exception exception) {
			System.out.println("Exception: " + exception.getMessage());
			return null;
		}
	}

	public static Node getNodeByXPath(Document doc, String expression) {
		try {
			XPath xpath = getCityGMLXPath();
			Node nodes = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
			return nodes;
		}
		catch (Exception exception) {
			System.out.println("Exception: " + exception.getMessage());
			return null;
		}
	}

	public static Node getNodeByXPath(Node node, String expression) {
		try {
			XPath xpath = getCityGMLXPath();
			Node nodes = (Node) xpath.evaluate(expression, node, XPathConstants.NODE);
			return nodes;
		}
		catch (Exception exception) {
			System.out.println("Exception: " + exception.getMessage());
			return null;
		}
	}

	public static ArrayList<String> GetToValidateXsdPathArrayList(Document doc) {
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
		hashMap.put("urn:oasis:names:tc:ciq:xal:3", "xsd/opengis/citygml/schema/xAL/xAL.xsd");
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
					// System.out.println(attr.getNodeName()+ " = \"" +
					// attr.getNodeValue() + "\"");
				}
			}
		}
		arrayList.add("xsd/opengis/citygml/schema/CityGML.xsd");
		arrayList.add("xsd/opengis/gml/3.2.1/gml-3.2.1.xsd");
		/* arrayList.add("xsd/opengis/gml/3.2/gml-3.2.2.xsd"); */
		return arrayList;
	}

}
