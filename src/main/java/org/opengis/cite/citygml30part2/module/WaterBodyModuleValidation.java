package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WaterBodyModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "WaterBody";

    /**
     * <p>Verify that instance documents using the WaterBody XML elements listed in <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#waterbody-xml-elements">Table 30</a> validate against the XML schema specified in <a href="http://schemas.opengis.net/citygml/waterbody/3.0/waterBody.xsd">waterBody.xsd</a>.</p>
     */
    @Test(enabled = MODULE_ENABLE)
    public void verifyVersioningModule() {
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    /**
     * <p>For each WaterBody space element, verify that if the space element is bounded by thematic surface boundaries using the property <em>core:boundary</em> (type: <em>core:AbstractSpaceBoundaryPropertyType</em>), each property contains exactly one surface element from <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#waterbody-boundaries-table">Table 31</a> that is supported for the specific space element. If no surface element is supported, the space element is not bounded by thematic surface boundaries.</p>
     */
    @Test(enabled = MODULE_ENABLE)
    public void verifyVegetationBoundaries() {
        String[] allowedBoundaries = { "wtr:WaterGroundSurface", "core:ClosureSurface", "gen:GenericThematicSurface" };
        boolean foundAtLeastOne = ValidationUtils.boundriesValidation(this.testSubject, allowedBoundaries);
        Assert.assertTrue(foundAtLeastOne,"None of Allowed Boundaries elements was found in the document.");
    }

}
