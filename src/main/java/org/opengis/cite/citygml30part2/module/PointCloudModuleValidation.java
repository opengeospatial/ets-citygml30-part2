package org.opengis.cite.citygml30part2.module;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PointCloudModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "PointCloud";

    /**
     * <p>Verify that instance documents using the PointCloud XML elements listed in <a href="https://docs.ogc.org/is/21-006r2/21-006r2.html#pointcloud-xml-elements">Table 21</a> validate against the XML schema specified in <a href="http://schemas.opengis.net/citygml/pointcloud/3.0/pointCloud.xsd">pointCloud.xsd</a>.</p>
     */
    @Test(enabled = MODULE_ENABLE, groups = { "Module" })
    public void VerifyPointCloudModule() {
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

}
