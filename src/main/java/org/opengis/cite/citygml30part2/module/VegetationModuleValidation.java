package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class VegetationModuleValidation  extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "Vegetation";

    /**
     * <p>Verify that instance documents using the Vegetation XML elements listed in <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#vegetation-xml-elements">Table 27</a> validate against the XML schema specified in <a href="http://schemas.opengis.net/citygml/vegetation/3.0/vegetation.xsd">vegetation.xsd</a>.</p>
     */
    @Test(enabled = MODULE_ENABLE, groups = { "Module" })
    public void VerifyVegetationModule() {
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    /**
     * <p>For each Vegetation space element, verify that if the space element is bounded by thematic surface boundaries using the property <em>core:boundary</em> (type: <em>core:AbstractSpaceBoundaryPropertyType</em>), each property contains exactly one surface element from <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#vegetation-boundaries-table">Table 28</a> that is supported for the specific space element. If no surface element is supported, the space element is not bounded by thematic surface boundaries.</p>
     */
    @Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
    public void VerifyVegetationBoundaries() {
        String[] allowedBoundaries = { "veg:PlantCover", "veg:SolitaryVegetationObject", "veg:AbstractVegetationObjectPropertyType" };
        boolean foundAtLeastOne = ValidationUtils.boundriesValidation(this.testSubject, allowedBoundaries);
        Assert.assertTrue(foundAtLeastOne,"None of Allowed Boundaries elements was found in the document.");
    }
}
