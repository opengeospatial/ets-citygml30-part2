package org.opengis.cite.citygml30part2.module;

import org.apache.xerces.dom.DeferredElementNSImpl;
import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.opengis.cite.citygml30part2.util.ValidationUtils.getXmlns;

public class TransportationModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "Transportation";

    /**
     * <p>Verify that instance documents using the Transportation XML elements listed in <a href="#transportation-xml-elements">Table 23</a> validate against the XML schema specified in <a href="http://schemas.opengis.net/citygml/transportation/3.0/transportation.xsd">transportation.xsd</a>.</p>
     */
    @Test(enabled = MODULE_ENABLE)
    public void verifyTransportationModule(){
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    /**
     * <p>For the following properties, verify that:</p>
     * <ul>
     * <li>
     * <p>if the <em>predecessor</em> property (type: <em>gml:ReferenceType</em>) of the <em>TrafficSpace</em> element is not null, it contains an XLink reference to a <em>TrafficSpace</em> element.</p>
     * </li>
     * <li>
     * <p>if the <em>successor</em> property (type: <em>gml:ReferenceType</em>) of the <em>TrafficSpace</em> element is not null, it contains an XLink reference to a <em>TrafficSpace</em> element.</p>
     * </li>
     * </ul>
     */
    @Test(enabled = MODULE_ENABLE)
    public void VerifyTransportationReference() {
        try {
            String expressionProperty = "//tran:predecessor";
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
                expressionProperty = "//tran:successor";
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
     * <p>For each Transportation space element verify that if the space element is bounded by thematic surface boundaries using the property <em>core:boundary</em> (type: <em>core:AbstractSpaceBoundaryPropertyType</em>), each property contains exactly one surface element from <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#transportation-boundaries-table">Table 24</a> that is supported for the specific space element. If no surface element is supported, the space element is not bounded by thematic surface boundaries.</p>
     */
    @Test(enabled = MODULE_ENABLE)
    public void verifyTransportationBoundaries() throws Exception {
        String[] allowedBoundaries = { "tran:Marking","core:ClosureSurface","gen:GenericThematicSurface","tran:AuxiliaryTrafficArea","tran:HoleSurface","tran:TrafficArea" };
        boolean foundAtLeastOne = ValidationUtils.boundriesValidation(this.testSubject, allowedBoundaries);
        Assert.assertTrue(foundAtLeastOne,"None of Allowed Boundaries elements was found in the document.");
    }
}
