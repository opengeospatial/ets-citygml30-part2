package org.opengis.cite.citygml30part2.global;

import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Console;
import java.util.ArrayList;

public class GlobalValidation extends CommonFixture {
    final boolean GLOBAL_ENABLE = true;
    String MODULE_NAME = "Global";

    /**
     * Verify that XML instance documents claiming conformance to this specification validate against the XML schema files.
     * @throws Exception
     */
    @Test(enabled = GLOBAL_ENABLE)
    public void verifyCityGMLInstanceDoc() throws Exception {
        ArrayList<String> arrayList = GetToValidateXsdPathArrayList(this.testSubject);
        boolean result = XMLUtils.isMultipleXMLSchemaValid(this.testSubject, arrayList);
        Assert.assertTrue(result, "Invalid CityGML Instance Document");
    }

    /**
     *
     * Verify that XLinks are not used to reference geometries, except for geometries of ImplicitGeometry elements, from another top-level feature.
     * @throws Exception
     */
    @Test(enabled = GLOBAL_ENABLE)
    public void verifyGlobalReferencingGeometries1() throws Exception {
        String xlinkAttribute = "xlink:href";
        String findReferenceExpression = "//*/@" + xlinkAttribute;
        boolean onlyContainImplicit = true;

        NodeList xlinkNodeList = XMLUtils.getNodeListByXPath(this.testSubject, findReferenceExpression);
        for (int i = 0; i < xlinkNodeList.getLength(); i++) {
            Element n = (Element) xlinkNodeList.item(i);

            String hrefName = n.getAttribute(xlinkAttribute);

            hrefName = hrefName.replace("#","");
            String gmlIdItem = "//*[@gml:id='"+hrefName+"']";
            NodeList targetNode = XMLUtils.getNodeListByXPath(this.testSubject, findReferenceExpression);
            for (int j = 0; j < targetNode.getLength(); j++) {
                Node node = targetNode.item(j);
                if (!node.getLocalName().contains("ImplicitGeometry")) {
                    onlyContainImplicit = false;
                }
            }
        }
        Assert.assertTrue(onlyContainImplicit,"XLinks SHALL NOT be used to reference geometries, except for geometries of ImplicitGeometry elements, from another top-level feature..");
    }

    /**
     * When referencing geometries of spaces and space boundaries, verify that:
     * <ul>
     * <li>Geometries stored inline a space boundary are not redundantly stored as geometry of a space. If the geometry is stored with the space in addition, it references the geometry from the space boundary using XLinks.
     *
     * <li>Space boundaries do not reference geometries of a space using XLinks.
     *
     * <li>This applies for all spaces and space boundaries that are children of the same top-level feature.
     * </ul>
     */
    @Test(enabled = GLOBAL_ENABLE)
    public void verifyGlobalReferencingGeometries2() {
        String spaceGeometriesExpr = "//*:spaceType";
        NodeList spaceGeometries = XMLUtils.getNodeListByXPath(this.testSubject, spaceGeometriesExpr);
        boolean spaceStatus = true;

        String boundaryGeometriesExpr = "//*:SpaceBoundary";
        NodeList boundaryGeometries = XMLUtils.getNodeListByXPath(this.testSubject, boundaryGeometriesExpr);
        boolean boundaryStatus = true;

        // No boundary need to validate
        if (spaceGeometries == null || boundaryGeometries == null) {
            return;
        }

        for (int i = 0; i < boundaryGeometries.getLength() ; i++) {
            String boundaryGeomId = boundaryGeometries.item(i).getAttributes().getNamedItem("id").getNodeValue();
            for (int j = 0; j < spaceGeometries.getLength(); j++) {
                Node spaceGeometry = spaceGeometries.item(j);
                if (spaceGeometry.getTextContent().contains(boundaryGeomId)) {
                    spaceStatus = false;
                }
            }
        }

        for (int i = 0; i < boundaryGeometries.getLength(); i++) {
            String xlinkHref = boundaryGeometries.item(i).getAttributes().getNamedItem("xlink:href").getNodeValue();
            if (xlinkHref != null && !xlinkHref.isEmpty()) {
                boundaryStatus = false;
            }
        }

        Assert.assertTrue(spaceStatus && boundaryStatus, "Referencing geometries of spaces and space boundaries invalid.");
    }

    /**
     * Verify that LoDs are self-contained: Geometries are not shared between different LoDs using XLinks.
     */
    @Test(enabled = GLOBAL_ENABLE)
    public void verifyGlobalReferencingGeometries3() {
        String lodExpr = "//*[starts-with(name(), 'lod')]";
        boolean selfContainedStatus = true;
        NodeList lodNodeList = XMLUtils.getNodeListByXPath(this.testSubject, lodExpr);
        for (int i = 0; i < lodNodeList.getLength(); i++) {
            Node lodNode = lodNodeList.item(i);
            Node parent = lodNode.getParentNode();
            NodeList childrens = parent.getChildNodes();
            int numChildElements = 0;
            for (int j = 0; j < childrens.getLength(); j++) {
                Node child = childrens.item(j);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    numChildElements++;
                }
            }
            if (numChildElements > 1)
                selfContainedStatus = false;
        }

        Assert.assertTrue(selfContainedStatus, "LoDs SHALL be self-contained.");
    }

    /**
     * When referencing shared geometries between top-level features using CityObjectRelation elements, verify that:
     * <ul>
     * <li>If two top-level features share a common geometry, the shared geometry is stored for each top-level feature separately.
     *
     * <li>If CityObjectRelation elements are used to denote that a geometry is shared between two top-level features, then a CityObjectRelation element is stored for each feature (might be the top-level feature itself or one of its nested features), and each CityObjectRelation element is assigned the relation type “shared” and references the other feature using an XLink. Thus, the reference is bi-directional.
     * </ul>
     */
    @Test(enabled = GLOBAL_ENABLE)
    public void verifyGlobalReferencingGeometries4() {
        String cityObjectRelationExpr = "//*/core:CityObjectRelation/core:relationType";
        boolean referenceStatus = true;
        NodeList lodNodeList = XMLUtils.getNodeListByXPath(this.testSubject, cityObjectRelationExpr);
        for (int i = 0; i < lodNodeList.getLength(); i++) {
            Node lodNode = lodNodeList.item(i);

            if (lodNode.getNodeName().equals("core:relationType") && lodNode.getTextContent().equals("shared")) {
                Node parent = lodNode.getParentNode();
                NodeList parentNodes = parent.getChildNodes();
                for (int j = 0; j < parentNodes.getLength(); j++) {
                    Node node = parentNodes.item(j);
                    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("core:relatedTo")) {
                        String idString = ((Element)node).getAttribute("xlink:href");
                        String findReferenceExpression = "//*[@gml:id='"+idString+"']";
                        Node idNode = XMLUtils.getNodeByXPath(this.testSubject, findReferenceExpression);
                        if (idNode == null) {
                            referenceStatus = false;
                        }
                    }
                }

            }
        }

        Assert.assertTrue(referenceStatus, "CityObjectRelation reference invalid.");
    }

    /**
     * When referencing features from alternative aggregations, verify that:
     * <ul>
     * <li>Each feature belongs to a natural aggregation hierarchy and is stored inline this hierarchy.
     *
     * <li> Alternative aggregations do not contain the feature inline but use an XLink to reference the feature.
     * </ul>
     */

}
