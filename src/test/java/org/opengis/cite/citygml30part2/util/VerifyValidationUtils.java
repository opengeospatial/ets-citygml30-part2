package org.opengis.cite.citygml30part2.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.cite.validation.SchematronValidator;
import org.w3c.dom.Document;

/**
 * Verifies the behavior of the ValidationUtils class.
 */
public class VerifyValidationUtils {

	private static DocumentBuilder docBuilder;

	public VerifyValidationUtils() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		docBuilder = dbf.newDocumentBuilder();
	}

	@Test
	public void testBuildSchematronValidator() {
		String schemaRef = "http://schemas.opengis.net/gml/3.2.1/SchematronConstraints.xml";
		String phase = "";
		SchematronValidator result = ValidationUtils.buildSchematronValidator(schemaRef, phase);
		assertNotNull(result);
	}

	@Test
	public void extractRelativeSchemaReference() throws FileNotFoundException, XMLStreamException {
		File xmlFile = new File("src/test/resources/LocalCRS_CityGML3.gml");
		Set<URI> xsdSet = ValidationUtils.extractSchemaReferences(new StreamSource(xmlFile), null);
		URI schemaURI = xsdSet.iterator().next();
		assertTrue("Expected schema reference building.xsd or relief.xsd",
				schemaURI.toString().endsWith("building.xsd") || schemaURI.toString().endsWith("relief.xsd"));
	}

	@Test
	public void elementValidation_expected() throws Exception {
		Document doc = docBuilder.parse(this.getClass().getResourceAsStream("/LocalCRS_CityGML3.gml"));
		boolean foundAtLeastOne = ValidationUtils.elementValidation(doc, "Building");
		assertTrue(foundAtLeastOne);
	}

}
