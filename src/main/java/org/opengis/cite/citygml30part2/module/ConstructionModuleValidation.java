package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConstructionModuleValidation extends CommonFixture {

	final boolean MODULE_ENABLE = true;

	String MODULE_NAME = "Construction";

	/**
	 * <p>
	 * Verify that instance documents using the Construction XML elements listed in
	 * <a href=
	 * "https://docs.ogc.org/is/21-006r2/21-006r2.html#construction-xml-elements">Table
	 * 15</a> validate against the XML schema specified in <a href=
	 * "http://schemas.opengis.net/citygml/construction/3.0/construction.xsd">construction.xsd</a>.
	 * </p>
	 */
	@Test(enabled = MODULE_ENABLE, groups = { "Module" })
	public void VerifyConstructionModule() {
		boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
		Assert.assertTrue(foundAtLeastOne, "No " + MODULE_NAME + " element was found in the document.");
	}

	/**
	 * <p>
	 * For each Construction space element verify that if the space element is bounded by
	 * thematic surface boundaries using the property <em>core:boundary</em> (type:
	 * <em>core:AbstractSpaceBoundaryPropertyType</em>), each property contains exactly
	 * one surface element from <a href=
	 * "https://docs.ogc.org/is/21-006r2/21-006r2.htm#construction-boundaries-table">Table
	 * 16</a> that is supported for the specific space element. If no surface element is
	 * supported, the space element is not bounded by thematic surface boundaries.
	 * </p>
	 */
	@Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
	public void VerifyConstructionBoundaries() {
		String[] allowedSpace = { "con:AbstractConstruction", "con:AbstractConstructiveElement",
				"con:AbstractFillingElement", "con:AbstractFurniture", "con:AbstractInstallation", "con:Door",
				"con:OtherConstruction", "con:Window" };
		boolean boundaryStatus = ValidationUtils.isBoundariesValid(this.testSubject, allowedSpace);
		Assert.assertTrue(boundaryStatus, "None of Allowed Boundaries elements was found in the document.");
	}

}
