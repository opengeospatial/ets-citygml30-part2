package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LandUseModuleValidation extends CommonFixture {

	final boolean MODULE_ENABLE = true;

	String MODULE_NAME = "LandUse";

	/**
	 * <p>
	 * Verify that instance documents using the LandUse XML elements listed in
	 * <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#landuse-xml-elements">Table
	 * 20</a> validate against the XML schema specified in <a href=
	 * "http://schemas.opengis.net/citygml/landuse/3.0/landUse.xsd">landUse.xsd</a>.
	 * </p>
	 */
	@Test(enabled = MODULE_ENABLE, groups = { "Module" })
	public void VerifyLandUseModule() {
		boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
		Assert.assertTrue(foundAtLeastOne, "No " + MODULE_NAME + " element was found in the document.");
	}

}
