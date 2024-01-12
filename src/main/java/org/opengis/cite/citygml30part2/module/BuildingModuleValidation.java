package org.opengis.cite.citygml30part2.module;

import org.apache.xerces.dom.DeferredElementNSImpl;
import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.NodeList;

import static org.opengis.cite.citygml30part2.util.ValidationUtils.getXmlns;

public class BuildingModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "Building";

    @Test(enabled = MODULE_ENABLE)
    public void verifyBuildingModule() throws Exception{
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    @Test(enabled = MODULE_ENABLE)
    public void verifyBuildingElementBoundaries() {
        String moduleNS = getXmlns(MODULE_NAME);
        String[] moduleElementNameList = {
                "AbstractBuilding", "AbstractBuildingSubdivision", "Building","BuildingConstructiveElement",
                "BuildingFurniture", "BuildingInstallation", "BuildingPart", "BuildingRoom",
                "BuildingUnit", "Storey"};
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
