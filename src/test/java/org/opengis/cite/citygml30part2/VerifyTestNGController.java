package org.opengis.cite.citygml30part2;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.w3c.dom.Document;

import net.sf.saxon.s9api.XdmValue;

/**
 * Verifies the results of executing a test run using the main controller
 * (TestNGController).
 *
 */
public class VerifyTestNGController {

	private static DocumentBuilder docBuilder;

	private Properties testRunProps;

	@BeforeClass
	public static void initParser() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		docBuilder = dbf.newDocumentBuilder();
	}

	@Before
	public void loadDefaultTestRunProperties() throws InvalidPropertiesFormatException, IOException {
		this.testRunProps = new Properties();
		this.testRunProps.loadFromXML(getClass().getResourceAsStream("/test-run-props.xml"));
	}

	@Test
	public void doTestRun() throws Exception {
		URL testSubject = getClass().getResource("/LocalCRS_CityGML3.gml");
		this.testRunProps.setProperty(TestRunArg.IUT.toString(), testSubject.toURI().toString());
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);
		this.testRunProps.storeToXML(outStream, "Integration test");
		Document testRunArgs = docBuilder.parse(new ByteArrayInputStream(outStream.toByteArray()));
		TestNGController controller = new TestNGController();
		Source results = controller.doTestRun(testRunArgs);
		String xpath = "/testng-results/@failed";
		XdmValue failed = XMLUtils.evaluateXPath2(results, xpath, null);
		int numFailed = Integer.parseInt(failed.getUnderlyingValue().getStringValue());
		assertEquals("Unexpected number of fail verdicts.", 15, numFailed);
	}

}
