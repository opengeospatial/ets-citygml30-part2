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

    /**
     * <p>Verify that instance documents using the Versioning XML elements listed in <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#versioning-xml-elements">Table 29</a> validate against the XML schema specified in <a href="http://schemas.opengis.net/citygml/versioning/3.0/versioning.xsd">versioning.xsd</a>.</p>
     */
    @Test(enabled = MODULE_ENABLE, groups = { "Module" })
    public void VerifyVersioningModule() {
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    /**
     * <p>For the following properties, verify that:</p>
     * <ul>
     * <li><p>If the <em>from</em> property (type: <em>gml:ReferenceType</em>) of the <em>VersionTransition</em> element is not null, it only contains an XLink reference to a <em>Version</em> element.</p></li>
     * <li><p>If the <em>to</em> property (type: <em>gml:ReferenceType</em>) of the <em>VersionTransition</em> element is not null, it only contains an XLink reference to a <em>Version</em> element.</p></li>
     * <li><p>If the <em>oldFeature</em> property (type: <em>gml:ReferenceType</em>) of the <em>Transaction</em> element is not null, it only contains an XLink reference to a <em>core:AbstractFeatureWithLifespan</em> element.</p></li>
     * <li><p>If the <em>newFeature</em> property (type: <em>gml:ReferenceType</em>) of the <em>Transaction</em> element is not null, it only contains an XLink reference to a <em>core:AbstractFeatureWithLifespan</em> element.</p></li>
     * <li><p>If the <em>versionMember</em> property (type: <em>gml:AbstractFeatureMemberType</em>) of the <em>Version</em> element is not null, it only  contains an XLink reference to a <em>core:AbstractFeatureWithLifespan</em> element.</p></li>
     * </ul>
     */
    @Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
    public void VerifyVersioningReference() {
        try {
            String expressionProperty = "//vers:from";
            String shouldHasAttribute = "xlink:href";
            NodeList result = XMLUtils.GetNodeListByXPath(this.testSubject, expressionProperty);
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
                NodeList targetNode = XMLUtils.GetNodeListByXPath(this.testSubject, findReferenceExpression);
                if (targetNode.getLength() <= 0 || targetNode.item(0).getLocalName() != "Version") {
                    isValid = false;
                    break;
                }
            }

            if (isValid) {
                expressionProperty = "//vers:to";
                result = XMLUtils.GetNodeListByXPath(this.testSubject, expressionProperty);
                for (int i = 0; i < result.getLength(); i++) {
                    Element n = (Element) result.item(i);
                    String hrefName = n.getAttribute(shouldHasAttribute);
                    if (hrefName.isEmpty()) {
                        isValid = false;
                        break;
                    }
                    hrefName = hrefName.replace("#","");
                    String findReferenceExpression = "//*[@gml:id='"+hrefName+"']";
                    NodeList targetNode = XMLUtils.GetNodeListByXPath(this.testSubject, findReferenceExpression);
                    if (targetNode.getLength() <= 0 || targetNode.item(0).getLocalName() != "Version") {
                        isValid = false;
                        break;
                    }
                }
            }

            if (isValid) {
                expressionProperty = "//vers:oldFeature";
                result = XMLUtils.GetNodeListByXPath(this.testSubject, expressionProperty);
                for (int i = 0; i < result.getLength(); i++) {
                    Element n = (Element) result.item(i);
                    String hrefName = n.getAttribute(shouldHasAttribute);
                    if (hrefName.isEmpty()) {
                        isValid = false;
                        break;
                    }
                    hrefName = hrefName.replace("#","");
                    String findReferenceExpression = "//*[@gml:id='"+hrefName+"']";
                    NodeList targetNode = XMLUtils.GetNodeListByXPath(this.testSubject, findReferenceExpression);
                    if (targetNode.getLength() <= 0) {
                        isValid = false;
                        break;
                    }
                }
            }

            if (isValid) {
                expressionProperty = "//vers:newFeature";
                result = XMLUtils.GetNodeListByXPath(this.testSubject, expressionProperty);
                for (int i = 0; i < result.getLength(); i++) {
                    Element n = (Element) result.item(i);
                    String hrefName = n.getAttribute(shouldHasAttribute);
                    if (hrefName.isEmpty()) {
                        isValid = false;
                        break;
                    }
                    hrefName = hrefName.replace("#","");
                    String findReferenceExpression = "//*[@gml:id='"+hrefName+"']";
                    NodeList targetNode = XMLUtils.GetNodeListByXPath(this.testSubject, findReferenceExpression);
                    if (targetNode.getLength() <= 0) {
                        isValid = false;
                        break;
                    }
                }
            }

            if (isValid) {
                expressionProperty = "//vers:versionMember";
                result = XMLUtils.GetNodeListByXPath(this.testSubject, expressionProperty);
                for (int i = 0; i < result.getLength(); i++) {
                    Element n = (Element) result.item(i);
                    String hrefName = n.getAttribute(shouldHasAttribute);
                    if (hrefName.isEmpty()) {
                        isValid = false;
                        break;
                    }
                    hrefName = hrefName.replace("#","");
                    String findReferenceExpression = "//*[@gml:id='"+hrefName+"']";
                    NodeList targetNode = XMLUtils.GetNodeListByXPath(this.testSubject, findReferenceExpression);
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
