package org.opengis.cite.citygml30part2.module;

import org.apache.xerces.dom.DeferredElementNSImpl;
import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import static org.opengis.cite.citygml30part2.util.ValidationUtils.getXmlns;

public class CityObjectGroupModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "CityObjectGroup";

    /**
     * Verify that instance documents using the CityObjectGroup XML elements listed in <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#cityobjectgroup-xml-elements">Table 13</a>validate against the XML schema specified in<a href="http://schemas.opengis.net/citygml/cityobjectgroup/3.0/cityObjectGroup.xsd">cityObjectGroup.xsd</a>.
     */
    @Test(enabled = MODULE_ENABLE, groups = { "Module" })
    public void VerifyBridgeModule(){
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    /**
     * For the following properties, verify that:
     * <ul>
     *     <li>If the parent property (type: gml:ReferenceType) of the CityObjectGroup element is not null, it contains an XLink reference to a core:AbstractCityObject element.</li>
     *     <li>If the groupMember property (type: gml:AbstractFeatureMemberType) of the Role element is not null, it contains an XLink reference to a core:AbstractCityObject element.</li>
     * </ul>
     */
    @Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
    public void VerifyCityObjectGroupReference() {
        try {
            String expressionProperty = "//grp:parent";
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
            if (isValid) {
                expressionProperty = "//grp:groupMember";
                result = XMLUtils.getNodeListByXPath(this.testSubject, expressionProperty);
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
            }

            Assert.assertTrue(isValid,MODULE_NAME+" Module reference invalid.");

        } catch (Exception exception) {
            System.out.println("Exception: " + exception.getMessage());
        }
    }

    /**
     * <p>For each CityObjectGroup space element verify that if the space element is bounded by thematic surface boundaries using the property core:boundary (type: core:AbstractSpaceBoundaryPropertyType), each property contains exactly one surface element from <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#cityobjectgroup-boundaries-table">Table 14</a> that is supported for the specific space element. </p>
     * <p>If no surface element is supported, the space element is not bounded by thematic surface boundaries.</p>
     */
    @Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
    public void VerifyCityObjectGroupElementBoundaries() {
        String[] allowedBoundaries = { "core:ClosureSurface","gen:GenericThematicSurface" };
        boolean foundAtLeastOne = ValidationUtils.boundriesValidation(this.testSubject, allowedBoundaries);
        Assert.assertTrue(foundAtLeastOne,"None of Allowed Boundaries elements was found in the document.");
    }
}
