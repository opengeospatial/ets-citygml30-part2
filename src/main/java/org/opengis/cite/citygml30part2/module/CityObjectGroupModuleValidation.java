package org.opengis.cite.citygml30part2.module;

import java.util.List;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CityObjectGroupModuleValidation extends CommonFixture {

	final boolean MODULE_ENABLE = true;

	String MODULE_NAME = "CityObjectGroup";

	/**
	 * Verify that instance documents using the CityObjectGroup XML elements listed in
	 * <a href=
	 * "https://docs.ogc.org/is/21-006r2/21-006r2.html#cityobjectgroup-xml-elements">Table
	 * 13</a>validate against the XML schema specified in<a href=
	 * "http://schemas.opengis.net/citygml/cityobjectgroup/3.0/cityObjectGroup.xsd">cityObjectGroup.xsd</a>.
	 */
	@Test(enabled = MODULE_ENABLE, groups = { "Module" })
	public void VerifyBridgeModule() {
		boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
		Assert.assertTrue(foundAtLeastOne, "No " + MODULE_NAME + " element was found in the document.");
	}

	/**
	 * For the following properties, verify that:
	 * <ul>
	 * <li>If the parent property (type: gml:ReferenceType) of the CityObjectGroup element
	 * is not null, it contains an XLink reference to a core:AbstractCityObject
	 * element.</li>
	 * <li>If the groupMember property (type: gml:AbstractFeatureMemberType) of the Role
	 * element is not null, it contains an XLink reference to a core:AbstractCityObject
	 * element.</li>
	 * </ul>
	 */
	@Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
	public void VerifyCityObjectGroupReference() {
		boolean isValid;
		try {
			List<String> allowedType = ValidationUtils.getTypeData("AbstractCityObject");
			isValid = XMLUtils.isRefValid("//grp:parent", "xlink:href", allowedType, this.testSubject);

		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			isValid = false;
		}
		Assert.assertTrue(isValid, MODULE_NAME + " Module reference invalid.");

		try {
			List<String> allowedType = ValidationUtils.getTypeData("AbstractCityObject");
			isValid = XMLUtils.isRefValid("//grp:parent", "xlink:href", allowedType, this.testSubject);

		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			isValid = false;
		}
		Assert.assertTrue(isValid, MODULE_NAME + " Module reference invalid.");
		try {
			List<String> allowedType = ValidationUtils.getTypeData("AbstractCityObject");
			isValid = XMLUtils.isRefValid("//grp:groupMember/grp:Role", "xlink:href", allowedType, this.testSubject);

		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			isValid = false;
		}
		Assert.assertTrue(isValid, MODULE_NAME + " Module reference invalid.");
	}

	/**
	 * <p>
	 * For each CityObjectGroup space element verify that if the space element is bounded
	 * by thematic surface boundaries using the property core:boundary (type:
	 * core:AbstractSpaceBoundaryPropertyType), each property contains exactly one surface
	 * element from <a href=
	 * "https://docs.ogc.org/is/21-006r2/21-006r2.html#cityobjectgroup-boundaries-table">Table
	 * 14</a> that is supported for the specific space element.
	 * </p>
	 * <p>
	 * If no surface element is supported, the space element is not bounded by thematic
	 * surface boundaries.
	 * </p>
	 */
	@Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
	public void VerifyCityObjectGroupElementBoundaries() {
		String[] allowedSpace = { "grp:CityObjectGroup" };
		boolean boundaryStatus = ValidationUtils.isBoundariesValid(this.testSubject, allowedSpace);
		Assert.assertTrue(boundaryStatus, "None of Allowed Boundaries elements was found in the document.");
	}

}
