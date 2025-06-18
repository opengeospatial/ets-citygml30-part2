package org.opengis.cite.citygml30part2.core;

import java.util.List;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CoreValidation extends CommonFixture {

	final boolean CORE_ENABLE = true;

	String MODULE_NAME = "Core";

	/**
	 * Verify that instance documents using the Core XML elements listed in Table 4
	 * validate against the XML schema specified in
	 * <a href="http://schemas.opengis.net/citygml/3.0/core.xsd">core.xsd</a>.
	 */
	@Test(enabled = CORE_ENABLE)
	public void VerifyCoreModule() {
		boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
		Assert.assertTrue(foundAtLeastOne, "No " + MODULE_NAME + " element was found in the document.");
	}

	/**
	 * For the following properties, verify that:
	 * <ul>
	 * <li>If the generalizesTo property (type: gml:ReferenceType) of the
	 * AbstractCityObject element is not null, it contains an XLink reference to an
	 * AbstractCityObject element.
	 * <li>If the relatedTo property (type: gml:ReferenceType) of the CityObjectRelation
	 * element is not null, it contains an XLink reference to an AbstractCityObject
	 * element.
	 * </ul>
	 */
	@Test(enabled = CORE_ENABLE)
	public void VerifyCoreReference() {
		boolean isValid;
		List<String> allowedType = ValidationUtils.getTypeData("AbstractCityObject");
		try {
			isValid = XMLUtils.isRefValid("//core:generalizesTo", "xlink:href", allowedType, this.testSubject);

		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			isValid = false;
		}
		Assert.assertTrue(isValid, MODULE_NAME + " Module reference invalid.");

		try {
			isValid = XMLUtils.isRefValid("//core:relatedTo", "xlink:href", allowedType, this.testSubject);
		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			isValid = false;
		}
		Assert.assertTrue(isValid, MODULE_NAME + " Module reference invalid.");
	}

	/**
	 * <p>
	 * <a href=
	 * "https://docs.ogc.org/is/21-006r2/21-006r2.html#core-boundaries-table">Table
	 * 5</a>lists the surfaces that are allowed as space boundaries of the space elements
	 * defined in the Core module.
	 * </p>
	 * <p>
	 * If a space element is bounded by space boundaries using the property core:boundary
	 * (type: core:AbstractSpaceBoundaryPropertyType), each property SHALL contain exactly
	 * one surface element from <a href=
	 * "https://docs.ogc.org/is/21-006r2/21-006r2.html#core-boundaries-table">Table
	 * 5</a>that is supported for the specific space element.
	 * </p>
	 * <p>
	 * If no surface element is supported, the space element SHALL NOT be bounded by space
	 * boundaries.
	 * </p>
	 */
	@Test(enabled = CORE_ENABLE)
	public void VerifyCoreBoundaries() {
		String[] allowedSpace = { "core:AbstractLogicalSpace", "core:AbstractOccupiedSpace",
				"core:AbstractPhysicalSpace", "core:AbstractSpace", "core:AbstractUnoccupiedSpace" };
		boolean boundaryStatus = ValidationUtils.isBoundariesValid(this.testSubject, allowedSpace);
		Assert.assertTrue(boundaryStatus, "None of Allowed Boundaries elements was found in the document.");
	}

}
