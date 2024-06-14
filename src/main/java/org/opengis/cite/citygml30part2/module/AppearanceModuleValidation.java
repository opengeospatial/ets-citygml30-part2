package org.opengis.cite.citygml30part2.module;

import java.util.Hashtable;
import java.util.List;

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

    /**
     * CityGML XML elements implemented by a conforming instance document shall conform to the XML schema in <a href="http://schemas.opengis.net/citygml/appearance/3.0/appearance.xsd">appearance.xsd</a>.
     */
    @Test(enabled = MODULE_ENABLE, groups = { "Module" })
    public void VerifyAppearanceModule(){
        boolean foundAtLeastOne = ValidationUtils.elementValidation(this.testSubject, MODULE_NAME);
        Assert.assertTrue(foundAtLeastOne,"No "+MODULE_NAME+" element was found in the document.");
    }

    /**
     * Surface data SHALL only be applied to surface geometries. The target property of a surface data element therefore SHALL only reference a subtype of gml:AbstractSurfaceType or a gml:MultiSurface.
     */
    @Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
    public void VerifyAppearanceTarget() {
        String expressionTarget = "//app:target/text()";
        NodeList nodes = XMLUtils.GetNodeListByXPath(this.testSubject, expressionTarget);
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

    /**
     * Assigning texture coordinates to a surface geometry using ParameterizedTexture elements is subject to the following restrictions:
     * <ul>
     *     <li>A: Texture coordinates given by the textureCoordinates property of the TexCoordList element define an explicit mapping of a surface’s boundary points to points in texture space. A point in texture space SHALL be given as a coordinate pair consisting of two doubles.</li>
     *     <li>B: The textureCoordinates and ring properties of a TexCoordList element form pairs and their order is decisive. The first textureCoordinates property in the sequence forms a pair with the first ring property in the sequence, the second textureCoordinates property forms a pair with the second ring property, and so on. As a consequence, the number of textureCoordinates and ring properties SHALL be identical.</li>
     *     <li>C: A TexCoordList element SHALL provide textureCoordinates for all gml:LinearRing elements contained in the surface geometry that is referenced by the target property of the embracing TextureAssociation. This explicitly includes both exterior and interior rings.</li>
     *     <li>D: The ring property (type: anyURI) SHALL reference the gml:id of the target gml:LinearRing using an appropriate XPointer.</li>
     *     <li>E: Each point in a ring of a surface geometry SHALL receive a point in texture space. The number of 2D points in the textureCoordinates element therefore SHALL be identical with the number of 3D points in the ring referenced by the corresponding ring property. This explicitly includes texture coordinates for the last point in a gml:LinearRing element which, by GML definition, must be coincident with the first point.</li>
     *     <li>F: The order of points in the textureCoordinates SHALL follow the order of the points in the referenced ring element as given in the CityGML document regardless of a possibly flipped surface orientation.</li>
     * </ul>
     */
    @Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
    public void VerifyAppearanceParameterizedTexture() {
        String expressionPath = "//app:textureCoordinates[@ring]";
        NodeList nodes = XMLUtils.GetNodeListByXPath(this.testSubject, expressionPath);
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
        nodes = XMLUtils.GetNodeListByXPath(this.testSubject, expressionPath);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element n = (Element) nodes.item(i);
            String value = n.getAttribute("ring");

            textureCoordinatesTable.put(value, n.getTextContent().trim());
        }

        expressionPath = "//gml:LinearRing";
        nodes = XMLUtils.GetNodeListByXPath(this.testSubject, expressionPath);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element n = (Element) nodes.item(i);
            if (!n.hasAttributeNS("http://www.opengis.net/gml","id")) {
                continue; //only check those linearings that are associated with texturecoordinates
            }

            String value = n.getAttributeNS("http://www.opengis.net/gml","id");

            if(!textureCoordinatesTable.containsKey("#"+value)) {
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
