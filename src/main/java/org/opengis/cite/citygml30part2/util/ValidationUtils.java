package org.opengis.cite.citygml30part2.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.xerces.dom.DeferredElementNSImpl;
import org.apache.xerces.util.XMLCatalogResolver;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opengis.cite.citygml30part2.Namespaces;
import org.opengis.cite.validation.SchematronValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

/**
 * A utility class that provides convenience methods to support schema
 * validation.
 */
public class ValidationUtils {

    static final String ROOT_PKG = "/org/opengis/cite/citygml30part2/";
    private static final XMLCatalogResolver SCH_RESOLVER = initCatalogResolver();

    private static XMLCatalogResolver initCatalogResolver() {
        return (XMLCatalogResolver) createSchemaResolver(Namespaces.SCH);
    }

    /**
     * Creates a resource resolver suitable for locating schemas using an entity
     * catalog. In effect, local copies of standard schemas are returned instead
     * of retrieving them from external repositories.
     * 
     * @param schemaLanguage
     *            A URI that identifies a schema language by namespace name.
     * @return A {@code LSResourceResolver} object that is configured to use an
     *         OASIS entity catalog.
     */
    public static LSResourceResolver createSchemaResolver(URI schemaLanguage) {
        String catalogFileName;
        if (schemaLanguage.equals(Namespaces.XSD)) {
            catalogFileName = "schema-catalog.xml";
        } else {
            catalogFileName = "schematron-catalog.xml";
        }
        URL catalogURL = ValidationUtils.class.getResource(ROOT_PKG
                + catalogFileName);
        XMLCatalogResolver resolver = new XMLCatalogResolver();
        resolver.setCatalogList(new String[] { catalogURL.toString() });
        return resolver;
    }

    /**
     * Constructs a SchematronValidator that will check an XML resource against
     * the rules defined in a Schematron schema. An attempt is made to resolve
     * the schema reference using an entity catalog; if this fails the reference
     * is used as given.
     * 
     * @param schemaRef
     *            A reference to a Schematron schema; this is expected to be a
     *            relative or absolute URI value, possibly matching the system
     *            identifier for some entry in an entity catalog.
     * @param phase
     *            The name of the phase to invoke.
     * @return A SchematronValidator instance, or {@code null} if the validator
     *         cannot be constructed (e.g. invalid schema reference or phase
     *         name).
     */
    public static SchematronValidator buildSchematronValidator(
            String schemaRef, String phase) {
        Source source = null;
        try {
            String catalogRef = SCH_RESOLVER
                    .resolveSystem(schemaRef.toString());
            if (null != catalogRef) {
                source = new StreamSource(URI.create(catalogRef).toString());
            } else {
                source = new StreamSource(schemaRef);
            }
        } catch (IOException x) {
            TestSuiteLogger.log(Level.WARNING,
                    "Error reading Schematron schema catalog.", x);
        }
        SchematronValidator validator = null;
        try {
            validator = new SchematronValidator(source, phase);
        } catch (Exception e) {
            TestSuiteLogger.log(Level.WARNING,
                    "Error creating Schematron validator.", e);
        }
        return validator;
    }

    /**
     * Extracts a set of XML Schema references from a source XML document. The
     * document element is expected to include the standard xsi:schemaLocation
     * attribute.
     * 
     * @param source
     *            The source instance to read from; its base URI (systemId)
     *            should be set.
     * @param baseURI
     *            An alternative base URI to use if the source does not have a
     *            system identifier set or if its system id is a {@code file}
     *            URI. This will usually be the URI used to retrieve the
     *            resource; it may be null.
     * @return A Set containing absolute URI references that specify the
     *         locations of XML Schema resources.
     * @throws XMLStreamException
     *             If an error occurs while reading the source instance.
     */
    public static Set<URI> extractSchemaReferences(Source source, String baseURI)
            throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(source);
        // advance to document element
        StartElement docElem = reader.nextTag().asStartElement();
        Attribute schemaLoc = docElem.getAttributeByName(new QName(
                XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation"));
        if (null == schemaLoc) {
            throw new RuntimeException(
                    "No xsi:schemaLocation attribute found. See ISO 19136, A.3.1.");
        }
        String[] uriValues = schemaLoc.getValue().split("\\s+");
        if (uriValues.length % 2 != 0) {
            throw new RuntimeException(
                    "xsi:schemaLocation attribute contains an odd number of URI values:\n"
                            + Arrays.toString(uriValues));
        }
        Set<URI> schemaURIs = new HashSet<URI>();
        // one or more pairs of [namespace name] [schema location]
        for (int i = 0; i < uriValues.length; i += 2) {
            URI schemaURI = null;
            if (!URI.create(uriValues[i + 1]).isAbsolute()
                    && (null != source.getSystemId())) {
                String schemaRef = URIUtils.resolveRelativeURI(
                        source.getSystemId(), uriValues[i + 1]).toString();
                if (schemaRef.startsWith("file")
                        && !new File(schemaRef).exists() && (null != baseURI)) {
                    schemaRef = URIUtils.resolveRelativeURI(baseURI,
                            uriValues[i + 1]).toString();
                }
                schemaURI = URI.create(schemaRef);
            } else {
                schemaURI = URI.create(uriValues[i + 1]);
            }
            schemaURIs.add(schemaURI);
        }
        return schemaURIs;
    }

    public static Schema createMultipleSchema(String[] arrXsdPath) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema wpsSchema = null;
        try {
        	Source[] arrSource = new Source[arrXsdPath.length];
        	for (int i = 0; i<arrXsdPath.length; i++) {
        		String xsdPath = arrXsdPath[i]; 
        		URL schemaURL = ValidationUtils.class.getResource(ROOT_PKG
                        + xsdPath);
        		Source xsdSource = new StreamSource(schemaURL.toString());
        		arrSource[i] = xsdSource;
			}
            wpsSchema = schemaFactory.newSchema(arrSource);
        } catch (SAXException e) {
            TestSuiteLogger.log(Level.WARNING,
                    "Failed to create WFS Schema object.", e);
        }
        return wpsSchema;
    }
    public static List<String> getAllowedBoundaries(String spaceName) {
        String jsonPath = ROOT_PKG + "boundaries.json";
        InputStream in = ValidationUtils.class.getResourceAsStream(jsonPath);
        List<String> stringList = new ArrayList<>();

        Map<String, Map<String, List<String>>> jsonObj = readBoundariesJsonObj(in);
        Map<String, List<String>> temp = jsonObj.get(spaceName);
        if (temp == null) {
            return stringList;
        }
        Set<String> boundariesSet = new HashSet<>();
        for (List<String> values : temp.values()) {
            boundariesSet.addAll(values);
        }
        stringList = new ArrayList<>(boundariesSet);
        return stringList;
    }

    public static List<String> getTypeData(String typeName) {
        String jsonPath = ROOT_PKG + "type-inheritance.json";
        InputStream in = ValidationUtils.class.getResourceAsStream(jsonPath);
        JSONParser gson = new JSONParser();
//        Type mapType = new TypeToken<Map<String, List<String>>>(){}.getType();

        List<String> jsonObj = new ArrayList<>();
        try (InputStreamReader reader = new InputStreamReader(in)) {
            Map<String, List<String>> jsonObjTemp = (Map<String, List<String>>) gson.parse(reader);
            if (jsonObjTemp != null && !jsonObjTemp.isEmpty()) {
                jsonObj = jsonObjTemp.get(typeName);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public static String getXmlns(String ns) {
        String CITYGML_NS = "http://www.opengis.net/citygml/";
        if (ns.toLowerCase().equals("core"))
            return CITYGML_NS + "3.0";

        return CITYGML_NS + ns.toLowerCase() + "/3.0";
    }

    public static boolean elementValidation(Document doc, String moduleName) {
        String moduleNS = getXmlns(moduleName);
        NodeList rootElementList = doc.getChildNodes();

        boolean validElement = false;
        for(int a=0; a<rootElementList.getLength(); a++) {
            String itemClassName = rootElementList.item(a).getClass().toString();

            if(itemClassName.equals("class org.apache.xerces.dom.DeferredElementNSImpl")) {
                DeferredElementNSImpl element = (DeferredElementNSImpl) rootElementList.item(a);

                boolean containCityModel = element.getLocalName().equals("CityModel");
                boolean nsEqual = element.getNamespaceURI().equals(getXmlns("Core"));
                if(containCityModel && nsEqual) {
                    if (moduleName != "Core") {
                        String prefix = NamespaceBindings.getNameSpacePrefix(moduleNS);
                        String expr = "//" + prefix + ":"+ moduleName;
                        NodeList nodeList = XMLUtils.getNodeListByXPath(doc, expr);
                        if (nodeList.getLength() > 0) {
                            validElement = true;
                        }
                    } else {
                        validElement = true;
                    }
                }
            }
        }
        return validElement;
    }

    // if the document contain node named boundary, it's parent node name should inside allowedSpace list, and the boundary node should only contain 1 child
    public static boolean isBoundariesValid(Document doc, String[] allowedSpace) {
        try {
            XPath xpath = XMLUtils.getCityGMLXPath();
            List<String> spaces = Arrays.asList(allowedSpace);
            String expr = "//*[*[local-name()='boundary']]";

            NodeList boundariesParentList = (NodeList) xpath.evaluate(expr, doc, XPathConstants.NODESET);
            // not contain boundary
            if (boundariesParentList.getLength() == 0) {
                return true;
            }
            for (int i = 0; i < boundariesParentList.getLength(); i++) {
                Element element = (Element) boundariesParentList.item(i);
                String currentSpaceName = element.getNodeName();
                // not an allowed space
                if (!spaces.contains(currentSpaceName)) {
                    return false;
                }
                NodeList boundaries = element.getElementsByTagName("boundary");
                List<String> allowedBoundaries = getAllowedBoundaries(currentSpaceName);
                // No allowed boundaries
                if (allowedBoundaries.size() == 0 && boundaries.getLength() > 0) {
                    return false;
                }
                for (int j = 0; j < boundaries.getLength(); j++) {
                    NodeList childNodes = boundaries.item(j).getChildNodes();
                    int elementNodeCount = 0;
                    for (int k = 0; k < childNodes.getLength(); k++) {
                        Node childNode = childNodes.item(k);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE){
                            elementNodeCount ++;
                            String childNodeName = childNode.getNodeName();
                            // not an allowed boundary
                            if (!allowedBoundaries.contains(childNodeName)) {
                                return false;
                            }
                        }
                    }
                    // should only contain one specify boundary
                    if (elementNodeCount != 1) {
                        return false;
                    }
                }
            }
        } catch(Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public static Map<String, Map<String, List<String>>> readBoundariesJsonObj(InputStream in) {
//        Gson gson = new Gson();
        JSONParser gson = new JSONParser();
//        Type mapType = new TypeToken<Map<String, Map<String, List<String>>>>(){}.getType();

        Map<String, Map<String, List<String>>> jsonObj = new HashMap<>();
        try (InputStreamReader reader = new InputStreamReader(in)) {
            Map<String, Map<String, List<String>>> jsonObjTemp = (Map<String, Map<String, List<String>>>) gson.parse(reader);
            if (jsonObjTemp != null && !jsonObjTemp.isEmpty()) {
                jsonObj = jsonObjTemp;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }
}
