package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LandUseModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = false;
    String MODULE_NAME = "LandUse";

    @Test(enabled = MODULE_ENABLE)
    public void verifyLandUseModule() throws Exception{
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }
}
