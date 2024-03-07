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
    final boolean MODULE_ENABLE = false;
    String MODULE_NAME = "Transportation";

    @Test(enabled = MODULE_ENABLE)
    public void verifyTransportationModule() throws Exception{
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

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

    @Test(enabled = MODULE_ENABLE)
    public void verifyTransportationBoundaries() throws Exception {
        String[] allowedBoundaries = { "tran:Marking","core:ClosureSurface","gen:GenericThematicSurface","tran:AuxiliaryTrafficArea","tran:HoleSurface","tran:TrafficArea" };
        boolean foundAtLeastOne = ValidationUtils.boundriesValidation(this.testSubject, allowedBoundaries);
        Assert.assertTrue(foundAtLeastOne,"None of Allowed Boundaries elements was found in the document.");
    }
}
