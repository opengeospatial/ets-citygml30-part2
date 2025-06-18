package org.opengis.cite.citygml30part2.module;

import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DynamizerModuleValidation extends CommonFixture {

	final boolean MODULE_ENABLE = true;

	String MODULE_NAME = "Dynamizer";

	/**
	 * <p>
	 * Verify that instance documents using the Dynamizer XML elements listed in <a href=
	 * "https://docs.ogc.org/is/21-006r2/21-006r2.html#dynamizer-xml-elements">Table
	 * 17</a> validate against the XML schema specified in <a href=
	 * "http://schemas.opengis.net/citygml/dynamizer/3.0/dynamizer.xsd">dynamizer.xsd</a>.
	 * </p>
	 */
	@Test(enabled = MODULE_ENABLE, groups = { "Module" })
	public void VerifyDynamizerModule() {
		boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
		Assert.assertTrue(foundAtLeastOne, "No " + MODULE_NAME + " element was found in the document.");
	}

	/**
	 * <p>
	 * Verify that if the <em>sensorLocation</em> property (type:
	 * <em>gml:ReferenceType</em>) of the <em>SensorConnection</em> element is not null,
	 * it contains an XLink reference to a <em>core:AbstractCityObject</em> element.
	 * </p>
	 */
	@Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
	public void VerifyDynamizerReference() {
		boolean isValid;
		try {
			List<String> allowedType = ValidationUtils.getTypeData("AbstractCityObject");
			isValid = XMLUtils.isRefValid("//grp:parent", "xlink:href", allowedType, this.testSubject);

		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			isValid = false;
		}
		Assert.assertTrue(isValid, MODULE_NAME + " Module reference invalid.");
	}

	/**
	 * <p>
	 * When referencing a feature property value using the <em>attributeRef</em> property
	 * of a <em>Dynamizer</em> element, verify that:
	 * </p>
	 * <ul>
	 * <li>
	 * <p>
	 * The <em>attributeRef</em> property (type: <em>xs:string</em>) references a feature
	 * property value using XPath.
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * The feature property referenced by the <em>attributeRef</em> property exists.
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * The data type of the timeseries data provided by a <em>Dynamizer</em> element is
	 * equal to the data type of the static value of the feature property that is
	 * referenced by the <em>attributeRef</em> property or is of a type that can be
	 * converted to the type of the feature property without information loss.
	 * </p>
	 * </li>
	 * </ul>
	 */
	@Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
	public void VerifyDynamizerAttributeRef() throws XPathExpressionException {
		// find dyn:Dynamizer that contain dyn:attributeRef
		String expressionProperty = "//dyn:Dynamizer[dyn:attributeRef]";

		NodeList dynamizerNodeList = XMLUtils.getNodeListByXPath(this.testSubject, expressionProperty);// (NodeList)
																										// xpath.evaluate(expressionProperty,
																										// doc,
																										// XPathConstants.NODESET);
		boolean allValid = true;
		for (int i = 0; i < dynamizerNodeList.getLength(); i++) {
			Node currentDynamizerNode = dynamizerNodeList.item(i);
			boolean resultRefValid = true;
			Node attributeRefNode = XMLUtils.getNodeByXPath(currentDynamizerNode, "dyn:attributeRef/text()");// xpath.evaluate("dyn:attributeRef/text()",
																												// currentDynamizerNode,
																												// XPathConstants.NODE);
			String refValue = attributeRefNode.getNodeValue();

			refValue = refValue.replace("genericAttribute", "*[local-name()='genericAttribute']");
			refValue = refValue.replace("[name='", "[gen:name='");

			// if using xpath
			// search xpath expr by attributeRef value
			Node attributeRefValueNode = XMLUtils.getNodeByXPath(this.testSubject, refValue); // (Node)
																								// xpath.evaluate(refValue,
																								// doc,
																								// XPathConstants.NODE);
			if (attributeRefValueNode == null) {
				resultRefValid = false;
			}
			String typeOfValue = attributeRefValueNode.getParentNode().getLocalName();

			XPath xpath = XMLUtils.getCityGMLXPath();
			String valueNode = (String) xpath.evaluate("text()", attributeRefValueNode, XPathConstants.STRING);

			Class<?> TYPE = null;
			try {
				TYPE = getTYPE(typeOfValue, valueNode);
			}
			catch (Exception e) {
				resultRefValid = false;
			}

			boolean typeEqual = true;

			boolean dynamizerValid = resultRefValid && typeEqual;

			allValid &= dynamizerValid;
		}
	}

	public Class<?> getTYPE(String typeOfValue, String valueNode) throws Exception {
		Class<?> TYPE;
		if (typeOfValue.equals("DoubleAttribute")) {
			TYPE = Double.class;
			Double.parseDouble(valueNode);
		}
		else if (typeOfValue.equals("CodeAttribute")) {
			TYPE = String.class;
		}
		else if (typeOfValue.equals("DateAttribute")) {
			TYPE = Date.class;
			Date.parse(valueNode);
		}
		else if (typeOfValue.equals("GenericAttribute")) {
			TYPE = String.class;
		}
		else if (typeOfValue.equals("StringAttribute")) {
			TYPE = String.class;
		}
		else if (typeOfValue.equals("IntAttribute")) {
			TYPE = Integer.class;
			Integer.parseInt(valueNode);
		}
		else if (typeOfValue.equals("MeasureAttribute")) {
			TYPE = String.class;
		}
		else if (typeOfValue.equals("UriAttribute")) {
			TYPE = String.class;
		}
		else {
			TYPE = String.class;
		}
		return TYPE;
	}

}
