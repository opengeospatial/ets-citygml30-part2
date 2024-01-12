package org.opengis.cite.citygml30part2.module;

import java.util.Hashtable;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AppearanceModuleValidation extends CommonFixture {
    final boolean MODULE_ENABLE = true;
    String MODULE_NAME = "Appearance";

    @Test(enabled = MODULE_ENABLE)
    public void verifyAppearanceModule() throws Exception{
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    /**
     * Requirement 11
        /req/appearance/target
        Surface data SHALL only be applied to surface geometries.
        The target property of a surface data element therefore SHALL only reference a subtype of gml:AbstractSurfaceType or a gml:MultiSurface.
        
        Verify that surface data is only applied to surface geometries. 
        The target property of a surface data element therefore only references a subtype of gml:AbstractSurfaceType or a gml:MultiSurface.
     */
    @Test(enabled = MODULE_ENABLE, dependsOnMethods = { "verifyAppearanceModule" })
    public void VerifyAppearanceTarget() throws Exception {
        String expressionTarget = "//app:target/text()";
        NodeList nodes = XMLUtils.getNodeListByXPath(this.testSubject, expressionTarget);
        boolean flag = true;
        for (int i = 0; i < nodes.getLength() && flag == true; i++) {
            String referenceTarget = nodes.item(i).getNodeValue().substring(1);
            String findReferenceExpression = "//*[@gml:id='"+referenceTarget+"']";
            Node node = XMLUtils.getNodeByXPath(this.testSubject, findReferenceExpression);

            if (node == null) {
                flag = false;
                break;
            }

            String nodeName = node.getLocalName();
            if (nodeName.contains("AbstractSurfaceType") || nodeName.contains("MultiSurface")) {
                break;
            }
            flag = false;
        }
        Assert.assertTrue(flag,MODULE_NAME+" reference invalid.");
    }

    @Test(enabled = MODULE_ENABLE, dependsOnMethods = { "verifyAppearanceModule" })
    public void VerifyAppearanceParameterizedTexture() {
        String expressionPath = "//app:textureCoordinates[@ring]";
        NodeList nodes = XMLUtils.getNodeListByXPath(this.testSubject, expressionPath);
        boolean flag = true;
        for (int i = 0; i < nodes.getLength(); i++) {
            Element n = (Element) nodes.item(i);
            String ringReference = n.getAttribute("ring").substring(1);
            String findReferenceExpression = "//*[@gml:id='"+ringReference+"']";
            Node node = XMLUtils.getNodeByXPath(this.testSubject, findReferenceExpression);
            if (!node.getLocalName().equals("gml:LinearRing")) {
                flag = false;
                break;
            }
        }

        Assert.assertTrue(flag,MODULE_NAME+" ring reference invalid.");

        Hashtable<String, String> textureCoordinatesTable = new Hashtable<String, String>();

        expressionPath = "//app:textureCoordinates[@ring]";
        nodes = XMLUtils.getNodeListByXPath(this.testSubject, expressionPath);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element n = (Element) nodes.item(i);
            String value = n.getAttribute("ring");

            textureCoordinatesTable.put(value, n.getTextContent().trim());
        }


        expressionPath = "//gml:LinearRing";
        nodes = XMLUtils.getNodeListByXPath(this.testSubject, expressionPath);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element n = (Element) nodes.item(i);
            if(!n.hasAttributeNS("http://www.opengis.net/gml","id"))
            {
                continue; //only check those linearings that are associated with texturecoordinates
            }

            String value = n.getAttributeNS("http://www.opengis.net/gml","id");

            if(!textureCoordinatesTable.containsKey("#"+value))
            {
                continue; //only check those linearings that are associated with texturecoordinates
            }

            String linearRingCoordinates = ((Element) n.getElementsByTagNameNS("http://www.opengis.net/gml","posList").item(0)).getTextContent().trim();
            int numberOfLinearRingCoordinates = linearRingCoordinates.split("\\s+").length;
            int numberOfTextureCoordinates = textureCoordinatesTable.get("#"+value).split("\\s+").length;
            String srsDimensionString = ((Element) n.getElementsByTagNameNS("http://www.opengis.net/gml","posList").item(0)).getAttribute("srsDimension");

            Assert.assertTrue((numberOfTextureCoordinates/2)==(numberOfLinearRingCoordinates/Integer.parseInt(srsDimensionString)),"Problem with "+value+". Each boundary point of the surface must receive a corresponding coordinate pair in texture space.");
        }
    }

}
