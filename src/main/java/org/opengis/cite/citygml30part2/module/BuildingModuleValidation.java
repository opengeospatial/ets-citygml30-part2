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
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "Building";

    /**
     * Verify that instance documents using the Building XML elements listed in <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#building-xml-elements">Table 9</a> validate against the XML schema specified in <a href="http://schemas.opengis.net/citygml/building/3.0/building.xsd">building.xsd</a>.
     */
    @Test(enabled = MODULE_ENABLE)
    public void verifyBuildingModule(){
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    /**
     * For each Building space element verify that if the space element is bounded by thematic surface boundaries using the property core:boundary (type: core:AbstractSpaceBoundaryPropertyType), each property contains exactly one surface element from <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#building-boundaries-table">Table 10</a> that is supported for the specific space element. If no surface element is supported, the space element is not bounded by thematic surface boundaries.
     */
    @Test(enabled = MODULE_ENABLE)
    public void verifyBuildingElementBoundaries() {
        String[] allowedBoundaries = { "con:GroundSurface","con:RoofSurface","con:CeilingSurface","con:OuterCeilingSurface","con:FloorSurface","con:OuterFloorSurface","con:WallSurface","con:InteriorWallSurface","core:ClosureSurface","gen:GenericThematicSurface"};
        boolean foundAtLeastOne = ValidationUtils.boundriesValidation(this.testSubject, allowedBoundaries);
        Assert.assertTrue(foundAtLeastOne,"None of Allowed Boundaries elements was found in the document.");
    }
}
