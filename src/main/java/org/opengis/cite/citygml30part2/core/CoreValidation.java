package org.opengis.cite.citygml30part2.core;

import java.util.ArrayList;
import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_CORE;

public class CoreValidation extends CommonFixture {
    final boolean CORE_ENABLE = false;
    String MODULE_NAME = "Core";

    @Test(enabled = CORE_ENABLE)
    public void verifyCoreModule() throws Exception{
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    @Test(enabled = CORE_ENABLE)
    public void VerifyCoreReference() {
        try {
            String expressionGeneralizesTo = "//core:generalizesTo";
            String shouldHasAttribute = "xlink:href";
            NodeList result = XMLUtils.getNodeListByXPath(this.testSubject, expressionGeneralizesTo);
            boolean hasGmlIdAttribute = false;
            for (int i = 0; i < result.getLength(); i++) {
                Element n = (Element) result.item(i);
                hasGmlIdAttribute = XMLUtils.hasChildWithAttribute(n, shouldHasAttribute);
            }
            if (result.getLength() > 0)
                Assert.assertTrue(hasGmlIdAttribute, "message");
            
            //----//
            String expressionRelatedTo = "//core:relatedTo";
            result = XMLUtils.getNodeListByXPath(this.testSubject, expressionRelatedTo);
            hasGmlIdAttribute = false;
            for (int i = 0; i < result.getLength(); i++) {
                Element n = (Element) result.item(i);
                hasGmlIdAttribute = XMLUtils.hasChildWithAttribute(n, shouldHasAttribute);
            }
            if (result.getLength() > 0)
                Assert.assertTrue(hasGmlIdAttribute, "message");

        } catch (Exception exception) {
            System.out.println("Exception: " + exception.getMessage());
        }

    }
}
