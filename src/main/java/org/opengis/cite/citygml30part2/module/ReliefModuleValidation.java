package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReliefModuleValidation extends CommonFixture {

	final boolean MODULE_ENABLE = true;

	String MODULE_NAME = "Relief";

	/**
	 * <p>
	 * Verify that instance documents using the Relief XML elements listed in
	 * <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#relief-xml-elements">Table
	 * 22</a> validate against the XML schema specified in
	 * <a href="http://schemas.opengis.net/citygml/relief/3.0/relief.xsd" class=
	 * "bare">relief.xsd</a>.
	 * </p>
	 */
	@Test(enabled = MODULE_ENABLE, groups = { "Module" })
	public void VerifyReliefModule() {
		boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
		Assert.assertTrue(foundAtLeastOne, "No " + MODULE_NAME + " element was found in the document.");
	}

}
