package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TunnelModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "Tunnel";

    /**
     * <p>Verify that instance documents using the Tunnel XML elements listed in <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#tunnel-xml-elements">Table 25</a> validate against the XML schema specified in <a href="http://schemas.opengis.net/citygml/tunnel/3.0/tunnel.xsd">/tunnel.xsd</a>.</p>
     */
    @Test(enabled = MODULE_ENABLE)
    public void verifyVersioningModule() {
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    /**
     * <p>For each Tunnel space element, verify that if the space element is bounded by thematic surface boundaries using the property <em>core:boundary</em> (type: <em>core:AbstractSpaceBoundaryPropertyType</em>), each property contains exactly one surface element from <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#tunnel-boundaries-table">Table 26</a> that is supported for the specific space element. If no surface element is supported, the space element is not bounded by thematic surface boundaries.</p>
     */
    @Test(enabled = MODULE_ENABLE)
    public void verifyVegetationBoundaries() {
        String[] allowedBoundaries = { "con:GroundSurface","con:RoofSurface","con:CeilingSurface","con:OuterCeilingSurface","con:FloorSurface","con:OuterFloorSurface","con:WallSurface","con:InteriorWallSurface","core:ClosureSurface","gen:GenericThematicSurface"};
        boolean foundAtLeastOne = ValidationUtils.boundriesValidation(this.testSubject, allowedBoundaries);
        Assert.assertTrue(foundAtLeastOne,"None of Allowed Boundaries elements was found in the document.");
    }
}
