package org.opengis.cite.citygml30part2.module;

import org.apache.xerces.dom.DeferredElementNSImpl;
import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

public class BuildingModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = false;
    String MODULE_NAME = "Building";

    @Test(enabled = MODULE_ENABLE)
    public void verifyBuildingModule() throws Exception{
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    @Test(enabled = MODULE_ENABLE)
    public void verifyBuildingElementBoundaries() throws Exception {
        String[] allowedBoundaries = { "con:GroundSurface","con:RoofSurface","con:CeilingSurface","con:OuterCeilingSurface","con:FloorSurface","con:OuterFloorSurface","con:WallSurface","con:InteriorWallSurface","core:ClosureSurface","gen:GenericThematicSurface"};
        boolean foundAtLeastOne = ValidationUtils.boundriesValidation(this.testSubject, allowedBoundaries);
        Assert.assertTrue(foundAtLeastOne,"None of Allowed Boundaries elements was found in the document.");
    }
}
