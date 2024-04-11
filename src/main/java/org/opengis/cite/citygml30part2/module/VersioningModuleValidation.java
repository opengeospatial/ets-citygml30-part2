package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class VersioningModuleValidation  extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "Versioning";

    @Test(enabled = MODULE_ENABLE)
    public void verifyVersioningModule() throws Exception{
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    @Test(enabled = MODULE_ENABLE)
    public void VerifyVersioningReference() {
        try {
            String expressionProperty = "//vers:from";
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
                if (targetNode.getLength() <= 0 || targetNode.item(0).getLocalName() != "Version") {
                    isValid = false;
                    break;
                }
            }

            if (isValid) {
                expressionProperty = "//vers:to";
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
                    if (targetNode.getLength() <= 0 || targetNode.item(0).getLocalName() != "Version") {
                        isValid = false;
                        break;
                    }
                }
            }

            if (isValid) {
                expressionProperty = "//vers:oldFeature";
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

            if (isValid) {
                expressionProperty = "//vers:newFeature";
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

            if (isValid) {
                expressionProperty = "//vers:versionMember";
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

}
