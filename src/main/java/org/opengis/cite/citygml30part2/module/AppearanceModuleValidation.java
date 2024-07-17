package org.opengis.cite.citygml30part2.module;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.*;
import org.testng.SkipException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.xpath.XPathConstants;

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
    public void VerifyAppearanceTarget() throws Exception  {
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
            // TODO: add  gml:AbstractSurfaceType, gml:MultiSurface and their inheritance
            List<String> allowedRef = new ArrayList<>();


            if (allowedRef.contains(nodeName)) {
                break;
            }
            flag = false;
        }
        throw new SkipException("Not implemented yet.");
        //Assert.assertTrue(flag,MODULE_NAME+" reference invalid.");
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
        // Test A (verify two double value)
        String expressionTextureCoordList = "//app:TexCoordList/app:textureCoordinates/text()";

        NodeList textureCoordinatesNodes = XMLUtils.getNodeListByXPath(this.testSubject, expressionTextureCoordList);

        boolean allCoordValid = true;
        for (int i = 0; i < textureCoordinatesNodes.getLength() && allCoordValid; i++) {
            String rawValue = textureCoordinatesNodes.item(i).getNodeValue();
            String[] splitString = rawValue.split(" ");
            if (splitString.length > 2) {
                allCoordValid = false;
                break;
            }
            try {
                Double.parseDouble(splitString[0]);
                Double.parseDouble(splitString[1]);
            } catch (Exception e) {
                allCoordValid = false;
            }
        }

        Assert.assertTrue(allCoordValid,MODULE_NAME+" texture coordinates invalid.");

        // Test B (verify the number of textureCoordinates and ring properties are identical)
        String expressionTexCoordList = "//app:TexCoordList";

        NodeList texCoordListNodes = XMLUtils.getNodeListByXPath(this.testSubject, expressionTexCoordList);

        boolean propertiesCountValid = true;
        for (int i = 0; i < texCoordListNodes.getLength() && propertiesCountValid; i++) {
            NodeList children = texCoordListNodes.item(i).getChildNodes();
            int textureCoordinatesCount = 0;
            int ringCount = 0;
            for (int j = 0; j < children.getLength(); j++) {
                if (children.item(j).getNodeType() != Node.ELEMENT_NODE)
                    continue;
                Element childElement = (Element) children.item(j);
                String childName = childElement.getNodeName();
                switch (childName) {
                    case "app:textureCoordinates":
                        textureCoordinatesCount ++;
                        break;
                    case "app:ring":
                        ringCount ++;
                        break;
                }
            }
            if (textureCoordinatesCount!= ringCount) {
                propertiesCountValid = false;
                System.out.println("number of textureCoordinates and ring properties are NOT identical");
            }
        }

        Assert.assertTrue(propertiesCountValid,MODULE_NAME+": number of textureCoordinates and ring properties are NOT identical");

        // Test D
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

        // Test E
        String expressionRingRef = "//app:textureCoordinates[@ring]";
        NodeList ringRefNodes = XMLUtils.getNodeListByXPath(this.testSubject, expressionRingRef);
        boolean valueCountValid = true;
        for (int i = 0; i < ringRefNodes.getLength() && valueCountValid; i++) {
            Element elementTextureCoord = (Element) ringRefNodes.item(i);
            String ringRefId = elementTextureCoord.getAttribute("ring").substring(1);
            String findReferenceExpression = "//*[@gml:id='"+ringRefId+"']";
            Node gmlIdNode = XMLUtils.getNodeByXPath(elementTextureCoord, expressionRingRef);
            if (gmlIdNode.getNodeName().equals("gml:LinearRing")) {
                Node postList = XMLUtils.getNodeByXPath(gmlIdNode, "gml:posList");
                Element postListElement = (Element) postList;
                int srsDimensionValue = Integer.parseInt(postListElement.getAttribute("srsDimension"));
                String rawValue = postListElement.getTextContent();
                rawValue = rawValue.replace("\t", " ").replace("\r", " ").replace("\n", " ");
                List<String> valueList3d = new ArrayList<String>();
                for (String rawString : rawValue.split(" ")) {
                    if (!rawString.equals(""))
                        valueList3d.add(rawString);
                }
                int lengthOf3D = valueList3d.size() / srsDimensionValue;
                if (valueList3d.size() % srsDimensionValue!= 0) {
                    System.out.println("value size invalid");
                    valueCountValid = false;
                    break;
                }
                String textureCoordRawValue = elementTextureCoord.getTextContent();
                List<String> valueList2d = new ArrayList<String>();
                for (String rawString : textureCoordRawValue.split(" ")) {
                    if (!rawString.equals(""))
                        valueList2d.add(rawString);
                }
                int lengthOf2D = valueList2d.size() / 2;
                if (valueList2d.size() % 2!= 0) {
                    System.out.println("value size invalid");
                    valueCountValid = false;
                    break;
                }
                if (lengthOf2D != lengthOf3D) {
                    System.out.println("value size not match invalid");
                    valueCountValid = false;
                    break;
                }
            }
        }
        Assert.assertTrue(valueCountValid,"textureCoordinates value count invalid.");

        // Test F
        String expressionLinearRing = "//gml:LinearRing[@gml:id]";
        NodeList linearRingNodes = XMLUtils.getNodeListByXPath(this.testSubject, expressionLinearRing);
        List<String> linearRingId = new ArrayList<String>();
        for (int i = 0; i < linearRingNodes.getLength(); i++) {
            Element element = (Element) linearRingNodes.item(i);
            String id = element.getAttribute("gml:id");
            linearRingId.add(id);
        }

        List<String> ringRef = new ArrayList<String>();
        for (int i = 0; i < ringRefNodes.getLength(); i++) {
            Element element = (Element) ringRefNodes.item(i);
            String ring = element.getAttribute("ring").substring(1);
            ringRef.add(ring);
        }
        boolean orderValid = true;
        if (ringRef.size() != linearRingId.size()) {
            System.out.println("Invalid ring size");
            orderValid = false;
        }
        for (int i = 0; i < linearRingId.size() && orderValid; i++) {
            if (!linearRingId.get(i).equals(ringRef.get(i))) {
                orderValid = false;
                break;
            }
        }

        Assert.assertTrue(orderValid,"Ring reference and LinearRing invalid.");
    }

    /**
     * Using GeoreferencedTexture elements is subject to the following restrictions:
     * <ul>
     *     <li>A: GeoreferencedTexture element SHALL define the geo-reference either inline using the referencePoint and orientation properties or externally inside the texture image (e.g., by using the GeoTIFF image format) or through an accompanying world file.</li>
     *     <li>B: The referencePoint property (type: gml:PointPropertyType) SHALL only contain or reference a 2D point geometry.</li>
     * </ul>
     */
    @Test(enabled = MODULE_ENABLE, dependsOnGroups = { "Module" })
    public void VerifyAppearanceGeoreferencedtexture() {
        // Test A
        String expressionGeoreferencedTexture = "//app:GeoreferencedTexture";

        NodeList geoRefNodes = XMLUtils.getNodeListByXPath(this.testSubject, expressionGeoreferencedTexture);
        boolean allGeorefencedValid = true;
        for (int i = 0; i < geoRefNodes.getLength(); i++) {
            Node currentGeoRefNode = geoRefNodes.item(i);
            NodeList children = currentGeoRefNode.getChildNodes();
            List<String> nodeNameList = new ArrayList<String>();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    nodeNameList.add(child.getNodeName());
                }
            }

            if (nodeNameList.contains("app:referencePoint") || nodeNameList.contains("app:orientation")) {
                if (!(nodeNameList.contains("app:referencePoint") && nodeNameList.contains("app:orientation"))) {
                    allGeorefencedValid = false;
                    System.out.println("The geo-reference either inline using the referencePoint and orientation properties or externally inside the texture image (e.g., by using the GeoTIFF image format) or through an accompanying world file.");
                }
            } else if (nodeNameList.contains("app:target")) {
                Node preferNode = XMLUtils.getNodeByXPath(currentGeoRefNode, "app:preferWorldFile");
                if (preferNode != null && preferNode.getNodeValue() == "false") {
                    allGeorefencedValid = false;
                    System.out.println("The preferWorldFile property SHALL not be false if there are referencing a external resource");
                }
            } else {
                allGeorefencedValid = false;
                System.out.println("There are no referencePoint and orientation or external references");
            }

            Assert.assertTrue(allGeorefencedValid, "GeoreferencedTexture invalid.");

            // Test B
            boolean referencePointValid = true;
            for (int a = 0; a < geoRefNodes.getLength(); a++) {
                Node currentGeoRefNode2 = geoRefNodes.item(a);

                NodeList referencePointNodeList = XMLUtils.getNodeListByXPath(currentGeoRefNode2, "app:referencePoint");
                for (int j = 0; j < referencePointNodeList.getLength(); j++) {
                    Node referencePointNode = referencePointNodeList.item(j);
                    Node pointNode = XMLUtils.getNodeByXPath(referencePointNode, "gml:Point/gml:pos/text()");
                    if (pointNode == null) {
                        Element elementRefPoint = (Element) referencePointNode;
                        if (elementRefPoint.hasAttribute("xlink:href")) {
                            String hrefName = elementRefPoint.getAttribute("xlink:href");
                            hrefName = hrefName.replace("#","");
                            String findReferenceExpression = "//*[@gml:id='"+hrefName+"']";

                            NodeList xlinkNode = XMLUtils.getNodeListByXPath(this.testSubject, findReferenceExpression);
                            if (xlinkNode == null) {
                                referencePointValid = false;
                                System.out.println("The referencePoint property (type: gml:PointPropertyType) SHALL only contain or reference a 2D point geometry.");
                                break;
                            }
                        } else {
                            referencePointValid = false;
                            break;
                        }
                    }
                    String pointRawValue = pointNode.getNodeValue();
                    String[] pointValues = pointRawValue.split(" ");
                    if (pointValues.length > 2) {
                        referencePointValid = false;
                        System.out.println("The referencePoint property (type: gml:PointPropertyType) SHALL only contain or reference a 2D point geometry.");
                    }
                }
            }

            Assert.assertTrue(referencePointValid, "The referencePoint property invalid");
        }
    }
}
