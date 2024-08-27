package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class TransportationModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "Transportation";

    /**
     * <p>Verify that instance documents using the Transportation XML elements listed in <a href="#transportation-xml-elements">Table 23</a> validate against the XML schema specified in <a href="http://schemas.opengis.net/citygml/transportation/3.0/transportation.xsd">transportation.xsd</a>.</p>
     */
    @Test(enabled = MODULE_ENABLE, groups = { "Module" })
    public void VerifyTransportationModule(){
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    /**
     * <p>For the following properties, verify that:</p>
     * <ul>
     * <li>
     * <p>if the <em>predecessor</em> property (type: <em>gml:ReferenceType</em>) of the <em>TrafficSpace</em> element is not null, it contains an XLink reference to a <em>TrafficSpace</em> element.</p>
     * </li>
     * <li>
     * <p>if the <em>successor</em> property (type: <em>gml:ReferenceType</em>) of the <em>TrafficSpace</em> element is not null, it contains an XLink reference to a <em>TrafficSpace</em> element.</p>
     * </li>
     * </ul>
     */
    @Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
    public void VerifyTransportationReference() {
        boolean isValid;
        List<String> allowedType = new ArrayList<>();
        allowedType.add("tran:TrafficSpace");
        try {

            isValid = XMLUtils.isRefValid("//tran:predecessor", "xlink:href", allowedType, this.testSubject);

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            isValid = false;
        }
        Assert.assertTrue(isValid,MODULE_NAME+" Module reference invalid.");

        try {
            isValid = XMLUtils.isRefValid("//tran:successor", "xlink:href", allowedType, this.testSubject);

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            isValid = false;
        }
        Assert.assertTrue(isValid,MODULE_NAME+" Module reference invalid.");
    }

    /**
     * <p>For each Transportation space element verify that if the space element is bounded by thematic surface boundaries using the property <em>core:boundary</em> (type: <em>core:AbstractSpaceBoundaryPropertyType</em>), each property contains exactly one surface element from <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#transportation-boundaries-table">Table 24</a> that is supported for the specific space element. If no surface element is supported, the space element is not bounded by thematic surface boundaries.</p>
     */
    @Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
    public void VerifyTransportationBoundaries() throws Exception {
        String[] allowedSpace = {
                "tran:AbstractTransportationSpace",
                "tran:AuxiliaryTrafficSpace",
                "tran:ClearanceSpace",
                "tran:Hole",
                "tran:Intersection",
                "tran:Railway",
                "tran:Road",
                "tran:Section",
                "tran:Square",
                "tran:Track",
                "tran:TrafficSpace",
                "tran:Waterway"};
        boolean boundaryStatus = ValidationUtils.isBoundariesValid(this.testSubject, allowedSpace);
        Assert.assertTrue(boundaryStatus,"None of Allowed Boundaries elements was found in the document.");
    }
}
