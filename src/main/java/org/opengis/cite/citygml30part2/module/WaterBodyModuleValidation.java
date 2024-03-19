package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WaterBodyModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = false;
    String MODULE_NAME = "WaterBody";

    @Test(enabled = MODULE_ENABLE)
    public void verifyVersioningModule() throws Exception{
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    @Test(enabled = MODULE_ENABLE)
    public void verifyVegetationBoundaries() throws Exception {
        String[] allowedBoundaries = { "wtr:WaterGroundSurface", "core:ClosureSurface", "gen:GenericThematicSurface" };
        boolean foundAtLeastOne = ValidationUtils.boundriesValidation(this.testSubject, allowedBoundaries);
        Assert.assertTrue(foundAtLeastOne,"None of Allowed Boundaries elements was found in the document.");
    }

}
