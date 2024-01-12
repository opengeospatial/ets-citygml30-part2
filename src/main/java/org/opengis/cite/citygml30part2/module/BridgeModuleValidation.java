package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.NodeList;
import org.apache.xerces.dom.DeferredElementNSImpl;

import static org.opengis.cite.citygml30part2.util.ValidationUtils.getXmlns;

public class BridgeModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "Bridge";

    @Test(enabled = MODULE_ENABLE)
    public void verifyBridgeModule() throws Exception{
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    @Test(enabled = MODULE_ENABLE)
    public void verifyBridgeElementBoundaries() {
        String moduleNS = getXmlns(MODULE_NAME);
        String[] moduleElementNameList = {
                "AbstractBridge", "Bridge", "BridgeConstructiveElement", "BridgeFurniture",
                "BridgeInstallation", "BridgePart", "BridgeRoom"};
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
