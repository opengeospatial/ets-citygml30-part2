package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

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
        List<String> vers = new ArrayList<>();
        vers.add("vers:Version");
        boolean isValid;
        try {
            isValid = XMLUtils.isRefValid("//vers:from", "xlink:href", vers, this.testSubject);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            isValid = false;
        }
        Assert.assertTrue(isValid,MODULE_NAME+" Module reference invalid.");

        try {
            isValid = XMLUtils.isRefValid("//vers:to", "xlink:href", vers, this.testSubject);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            isValid = false;
        }
        Assert.assertTrue(isValid,MODULE_NAME+" Module reference invalid.");

        List<String> allowedAbstractFeatureWithLifespan = ValidationUtils.getTypeData("AbstractFeatureWithLifespan");

        try {
            isValid = XMLUtils.isRefValid("//vers:oldFeature", "xlink:href", allowedAbstractFeatureWithLifespan, this.testSubject);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            isValid = false;
        }
        Assert.assertTrue(isValid,MODULE_NAME+" Module reference invalid.");

        try {
            isValid = XMLUtils.isRefValid("//vers:newFeature", "xlink:href", allowedAbstractFeatureWithLifespan, this.testSubject);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            isValid = false;
        }
        Assert.assertTrue(isValid,MODULE_NAME+" Module reference invalid.");

        try {
            isValid = XMLUtils.isRefValid("//vers:versionMember", "xlink:href", allowedAbstractFeatureWithLifespan, this.testSubject);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            isValid = false;
        }
        Assert.assertTrue(isValid,MODULE_NAME+" Module reference invalid.");

    }

}
