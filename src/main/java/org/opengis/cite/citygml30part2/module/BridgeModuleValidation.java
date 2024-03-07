package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.xerces.dom.DeferredElementNSImpl;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import static org.opengis.cite.citygml30part2.util.ValidationUtils.getXmlns;

public class BridgeModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = false;
    String MODULE_NAME = "Bridge";

    @Test(enabled = MODULE_ENABLE)
    public void verifyBridgeModule() throws Exception{
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    @Test(enabled = MODULE_ENABLE)
    public void verifyBridgeElementBoundaries() throws Exception {
        String[] allowedBoundaries = { "core:ClosureSurface","gen:GenericThematicSurface" };
        boolean foundAtLeastOne = ValidationUtils.boundriesValidation(this.testSubject, allowedBoundaries);
        Assert.assertTrue(foundAtLeastOne,"None of Allowed Boundaries elements was found in the document.");
    }
}
