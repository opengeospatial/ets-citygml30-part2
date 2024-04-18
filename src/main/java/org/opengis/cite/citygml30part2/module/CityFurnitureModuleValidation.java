package org.opengis.cite.citygml30part2.module;

import org.apache.xerces.dom.DeferredElementNSImpl;
import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.NodeList;

import static org.opengis.cite.citygml30part2.util.ValidationUtils.getXmlns;

public class CityFurnitureModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "CityFurniture";

    /**
     * Verify that instance documents using the CityFurniture XML elements listed in <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#cityfurniture-xml-elements">Table 11</a>validate against the XML schema specified in <a href="http://schemas.opengis.net/citygml/cityfurniture/3.0/cityFurniture.xsd">cityFurniture.xsd</a>.
     */
    @Test(enabled = MODULE_ENABLE)
    public void verifyCityFurnitureModule(){
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    /**
     * For each CityFurniture space element verify that if the space element is bounded by thematic surface boundaries using the property core:boundary (type: core:AbstractSpaceBoundaryPropertyType), each property contains exactly one surface element from <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#cityfurniture-boundaries-table">Table 12</a>that is supported for the specific space element. If no surface element is supported, the space element is not bounded by thematic surface boundaries.
     */
    @Test(enabled = MODULE_ENABLE)
    public void verifyCityFurnitureElementBoundaries() {
        String[] allowedBoundaries = { "core:ClosureSurface","gen:GenericThematicSurface" };
        boolean foundAtLeastOne = ValidationUtils.boundriesValidation(this.testSubject, allowedBoundaries);
        Assert.assertTrue(foundAtLeastOne,"None of Allowed Boundaries elements was found in the document.");
    }
}
