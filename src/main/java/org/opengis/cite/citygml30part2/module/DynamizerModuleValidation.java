package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DynamizerModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "Dynamizer";

    /**
     * <p>Verify that instance documents using the Dynamizer XML elements listed in <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#dynamizer-xml-elements">Table 17</a> validate against the XML schema specified in <a href="http://schemas.opengis.net/citygml/dynamizer/3.0/dynamizer.xsd">dynamizer.xsd</a>.</p>
     */
    @Test(enabled = MODULE_ENABLE)
    public void verifyDynamizerModule() {
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    /**
     * <p>Verify that if the <em>sensorLocation</em> property (type: <em>gml:ReferenceType</em>) of the <em>SensorConnection</em> element is not null, it contains an XLink reference to a <em>core:AbstractCityObject</em> element.</p>
     */
    @Test(enabled = MODULE_ENABLE)
    public void VerifyDynamizerReference() {
        try {
            String expressionProperty = "//dyn:sensorLocation";
            String shouldHasAttribute = "xlink:href";
            NodeList result = XMLUtils.getNodeListByXPath(this.testSubject, expressionProperty);
            boolean isValid = true;

            for (int i = 0; i < result.getLength(); i++) {
                Element n = (Element) result.item(i);
                String hrefName = n.getAttribute(shouldHasAttribute);
                if (hrefName.isEmpty()) {
                    isValid = false;
                    break;
                }
                hrefName = hrefName.replace("#","");
                String findReferenceExpression = "//*[@gml:id='"+hrefName+"']";
                NodeList targetNode = XMLUtils.getNodeListByXPath(this.testSubject, findReferenceExpression);
                if (targetNode.getLength() <= 0) {
                    isValid = false;
                    break;
                }
            }

            Assert.assertTrue(isValid,MODULE_NAME+" Module reference invalid.");

        } catch (Exception exception) {
            System.out.println("Exception: " + exception.getMessage());
        }
    }

    /**
     * <p>When referencing a feature property value using the <em>attributeRef</em> property of a <em>Dynamizer</em> element, verify that:</p>
     * <ul>
     * <li>
     * <p>The <em>attributeRef</em> property (type: <em>xs:string</em>) references a feature property value using XPath.</p>
     * </li>
     * <li>
     * <p>The feature property referenced by the <em>attributeRef</em> property exists.</p>
     * </li>
     * <li>
     * <p>The data type of the timeseries data provided by a <em>Dynamizer</em> element is equal to the data type of the static value of the feature property that is referenced by the <em>attributeRef</em> property or is of a type that can be converted to the type of the feature property without information loss.</p>
     * </li>
     * </ul>
     */
    @Test(enabled = MODULE_ENABLE)
    public void VerifyDynamizerAttributeRef() {
        String expressionProperty = "//dyn:attributeRef";
        NodeList result = XMLUtils.getNodeListByXPath(this.testSubject, expressionProperty);
        boolean isValid = true;
        for (int i = 0; i < result.getLength(); i++) {
            Element n = (Element) result.item(i);
            String refXPath = n.getTextContent();
            NodeList refNodeList = XMLUtils.getNodeListByXPath(this.testSubject, refXPath);
            if (refNodeList.getLength() <= 0)
                isValid = false;
        }

        Assert.assertTrue(isValid,MODULE_NAME+" Module AttributeRef invalid.");
    }
}
