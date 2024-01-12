package org.opengis.cite.citygml30part2.global;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.opengis.cite.citygml30part2.util.SchemaPathConst.XSD_CORE;

public class GlobalValidation extends CommonFixture {

    @Test()
    public void verifyCityGMLinstanceDoc() throws Exception {
        ArrayList<String> arrayList = GetToValidateXsdPathArrayList(this.testSubject);
        boolean result = XMLUtils.isMultipleXMLSchemaValid(this.testSubject, arrayList);
        Assert.assertTrue(result, "Invalid CityGML Instance Document");
    }

}
