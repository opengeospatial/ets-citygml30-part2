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

import static org.opengis.cite.citygml30part2.util.ValidationUtils.getXmlns;

public class CityObjectGroupModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = false;
    String MODULE_NAME = "CityObjectGroup";

    @Test(enabled = MODULE_ENABLE)
    public void verifyBridgeModule() throws Exception{
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    @Test(enabled = MODULE_ENABLE)
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

    @Test(enabled = MODULE_ENABLE)
    public void verifyCityObjectGroupElementBoundaries() {
        String moduleNS = getXmlns(MODULE_NAME);
        String[] moduleElementNameList = { "CityObjectGroup" };
        String sb = String.join(", ", moduleElementNameList);

        NodeList rootElementList = this.testSubject.getChildNodes();

        boolean foundAtLeastOne = false;

        for(int i=0; i<rootElementList.getLength(); i++)
        {
            DeferredElementNSImpl element = (DeferredElementNSImpl) rootElementList.item(i);
            for(int j = 0 ; j< moduleElementNameList.length; j++) {
                NodeList nodeList = element.getElementsByTagNameNS(moduleNS, moduleElementNameList[j]);
                if(nodeList.getLength()>0) {
                    foundAtLeastOne = true;
                }
            }
        }

        Assert.assertTrue(foundAtLeastOne,"None of " + sb + " elements was found in the document.");
    }
}
