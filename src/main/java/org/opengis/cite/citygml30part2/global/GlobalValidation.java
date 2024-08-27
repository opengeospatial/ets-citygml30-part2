package org.opengis.cite.citygml30part2.global;

import org.apache.xerces.dom.DeferredAttrNSImpl;
import org.apache.xerces.dom.DeferredElementNSImpl;
import org.opengis.cite.citygml30part2.CommonFixture;
import org.opengis.cite.citygml30part2.util.ValidationUtils;
import org.opengis.cite.citygml30part2.util.XMLUtils;
import org.testng.Assert;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlobalValidation extends CommonFixture {
    final boolean GLOBAL_ENABLE = true;
    String MODULE_NAME = "Global";

    /**
     * Verify that XML instance documents claiming conformance to this specification validate against the XML schema files.
     * @throws Exception
     */
    @Test(enabled = GLOBAL_ENABLE)
    public void VerifyCityGMLInstanceDoc() throws Exception {
        ArrayList<String> arrayList = XMLUtils.GetToValidateXsdPathArrayList(this.testSubject);
        boolean result;
        String exceptionMessage = "";
        try {
            result = XMLUtils.isMultipleXMLSchemaValid(this.testSubject, arrayList);
        }
        catch (Exception e) {
            exceptionMessage = e.getMessage();
            result = false;
        }
        Assert.assertTrue(result, "Invalid CityGML Instance Document. " + exceptionMessage);
    }

    /**
     *
     * Verify that XLinks are not used to reference geometries, except for geometries of ImplicitGeometry elements, from another top-level feature.
     * @throws Exception
     */
    @Test(enabled = GLOBAL_ENABLE)
    public void VerifyGlobalReferencingGeometries1() throws Exception {
        String xlinkAttribute = "xlink:href";
        String findReferenceExpression = "//*/@" + xlinkAttribute;
        boolean exist = true;

        NodeList xlinkNodeList = XMLUtils.getNodeListByXPath(this.testSubject, findReferenceExpression);
        for (int i = 0; i < xlinkNodeList.getLength(); i++) {
            DeferredAttrNSImpl n = (DeferredAttrNSImpl) xlinkNodeList.item(i);
            String hrefName = n.getValue();

            hrefName = hrefName.replace("#","");
            String gmlIdItem = "//*[@gml:id='"+hrefName+"']";
            NodeList targetNode = XMLUtils.getNodeListByXPath(this.testSubject, gmlIdItem);
            if (!(targetNode.getLength() > 0)) {
                exist = false;
            }
        }
        Assert.assertTrue(exist,"XLinks SHALL NOT be used to reference geometries, except for geometries of ImplicitGeometry elements, from another top-level feature..");
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
    public void VerifyGlobalReferencingGeometries2() throws XPathExpressionException {
        boolean valid = true;
        try {
            List<String> spaceBoundaryNamesList = ValidationUtils.getTypeData("AbstractSpaceBoundary");
            List<String> spaceNameList = ValidationUtils.getTypeData("AbstractSpace");
            XPath xPath = XMLUtils.getCityGMLXPath();

            String exprBoundary = "//*[local-name()='boundary']";

            NodeList boundariesNode = (NodeList) xPath.evaluate(exprBoundary, this.testSubject, XPathConstants.NODESET);
            for (int i = 0; i < boundariesNode.getLength() && valid; i++){
                Node currentBoundaryNode = boundariesNode.item(i);
                Node parentNode = currentBoundaryNode.getParentNode();
                String parentNodeName = parentNode.getNodeName();
                if (!spaceNameList.contains(parentNodeName)) {
                    valid = false;
                    break;
                }
            }

            List<NodeList> allSpaceBoundariesList = new ArrayList<>();
            for (String spaceBoundaryName : spaceBoundaryNamesList) {
                String expr = "//"+ spaceBoundaryName;

                NodeList spaceBoundariesNode = (NodeList)xPath.evaluate(expr, this.testSubject, XPathConstants.NODESET);
                if (spaceBoundariesNode == null || spaceBoundariesNode.getLength() == 0)
                    continue;
                allSpaceBoundariesList.add(spaceBoundariesNode);
            }

            List<NodeList> allSpaceList = new ArrayList<>();

            for (String spaceName : spaceNameList) {
                String spaceLocalName = spaceName.split(":")[1];
                String expr = "//*[local-name()='"+spaceLocalName+"']";
                NodeList spacesNode = (NodeList)xPath.evaluate(expr, this.testSubject, XPathConstants.NODESET);
                if (spacesNode == null || spacesNode.getLength() == 0)
                    continue;
                allSpaceList.add(spacesNode);
            }

            for (int i = 0; i < allSpaceList.size() && valid; i++) {
                NodeList currentSpaceNodeList = allSpaceList.get(i);
                for (int j = 0; j < currentSpaceNodeList.getLength() && valid; j++) {
                    Node currentSpaceNode = currentSpaceNodeList.item(j);
                    int childCount = currentSpaceNode.getChildNodes().getLength();
                    if (childCount == 0)
                    {
                        checkXLink(currentSpaceNode);
                        continue;
                    }
                    //check possible existing xlink:href references
                    //see https://github.com/opengeospatial/ets-citygml30-part2/issues/36                    
                    String expr = "//*[@xlink:href]";
                    NodeList refList = (NodeList) xPath.evaluate(expr, currentSpaceNode, XPathConstants.NODESET);
                    if (refList.getLength() > 0) {
                        for (int l = 0; l < refList.getLength(); l++) {
                            Node xlinkHrefNode = refList.item(l);
                            checkXLink(xlinkHrefNode);
                        }
                    }
                    Node gmlIdNode = currentSpaceNode.getAttributes().getNamedItem("gml:id");
                    if(gmlIdNode == null) {
                        continue;
                    }
                    String id = gmlIdNode.getNodeValue();

                    expr = "*[xlink:href='#" + id + "']";
                    refList = (NodeList) xPath.evaluate(expr, this.testSubject, XPathConstants.NODESET);
                    if (refList.getLength() > 0)
                        valid = false;
                }
            }

        } catch (Exception e) {
            valid = false;
            Assert.fail("Invalid XML document. " + e.getMessage());
        }

        Assert.assertTrue(valid, "Referencing geometries of spaces and space boundaries invalid.");
    }

    /**
     * Verify that LoDs are self-contained: Geometries are not shared between different LoDs using XLinks.
     */
    @Test(enabled = GLOBAL_ENABLE)
    public void VerifyGlobalReferencingGeometries3() {
        String lodExpr = "//*[starts-with(local-name(), 'lod')]";
        boolean selfContainedStatus = true;
        NodeList lodNodeList = XMLUtils.getNodeListByXPath(this.testSubject, lodExpr);
        for (int i = 0; i < lodNodeList.getLength(); i++) {
            Node lodNode = lodNodeList.item(i);
            Node parent = lodNode.getParentNode();
            NodeList children = parent.getChildNodes();
            int numChildElements = 0;
            for (int j = 0; j < children.getLength(); j++) {
                String itemClassName = children.item(j).getClass().toString();
                if(itemClassName.equals("class org.apache.xerces.dom.DeferredElementNSImpl")) {
                    DeferredElementNSImpl element = (DeferredElementNSImpl) children.item(j);
                    if (element.hasAttribute("xlink:href"))
                        numChildElements++ ;
                }
            }
            if (numChildElements > 1)
                selfContainedStatus = false;
        }
        Assert.assertTrue(selfContainedStatus, "LoDs SHALL be self-contained.");

        // if (selfContainedStatus) will throw an exception
        // geometries shall not be shared between different LoDs.
        boolean notShared = true;
        for (int i = 0; i < lodNodeList.getLength() && notShared; i++) {
            NodeList children = lodNodeList.item(i).getChildNodes();
            for (int j = 0; j < children.getLength() && notShared; j++) {
                if (children.item(j).getNodeType() == Node.ELEMENT_NODE){
                    Element selfContained = (Element) children.item(j);
                    if (selfContained.hasAttribute("gml:id")) {
                        String id = "#" + selfContained.getAttribute("gml:id");
                        String expressionOfId = "//*[@xlink:href='"+id+"']";
                        NodeList xLinkHrefList = XMLUtils.getNodeListByXPath(this.testSubject, expressionOfId);
                        for (int k = 0; k < xLinkHrefList.getLength() && notShared; k++) {
                            Node xlinkHrefNode = xLinkHrefList.item(k);
                            Node parentNode = xlinkHrefNode.getParentNode();

                            String localNameOfXlink = parentNode.getLocalName();
                            if (localNameOfXlink.startsWith("lod")) {
                                notShared = false;
                            }
                        }
                    }
                }
            }
        }

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
    public void VerifyGlobalReferencingGeometries4() {
        List<String> topLevelFeature = new ArrayList<>(Arrays.asList("Bridge", "Building", "CityFurniture", "CityObjectGroup", "GenericLogicalSpace", "GenericOccupiedSpace", "GenericThematicSurface", "GenericUnoccupiedSpace", "LandUse", "OtherConstruction", "PlantCover", "Railway", "ReliefFeature", "Road", "SolitaryVegetationObject", "Square", "Track", "Tunnel", "WaterBody", "Waterway"));
        boolean topLevelSeparately = true;
        String relatedExpression = "//*[core:relatedTo/core:CityObjectRelation/core:relationType='shared']";

        NodeList nodeList = XMLUtils.getNodeListByXPath(this.testSubject, relatedExpression);
        for (int i = 0; i < nodeList.getLength() && topLevelSeparately; i++) {
            Node n = nodeList.item(i);
            String localName = n.getLocalName();
            if (!topLevelFeature.contains(localName))
                continue;
            String relatedId = XMLUtils.getNodeByXPath(n, "core:relatedTo/core:CityObjectRelation/core:relatedTo/@xlink:href").getNodeValue();
            String idExpression = "//*[@gml:id='"+relatedId+"']";
            Node idNode = XMLUtils.getNodeByXPath(n, idExpression);
            if (idNode != null)
                topLevelSeparately = false;
        }

        Assert.assertTrue(topLevelSeparately, "The shared geometry is NOT stored for each top-level feature separately.");
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
     * For referencing features from alternative aggregations::
     * <ul>
     * <li>Each feature belongs to a natural aggregation hierarchy and SHALL be stored inline this hierarchy.
     *
     * <li>Alternative aggregations SHALL NOT contain the feature inline but SHALL use an XLink to reference the feature.
     * </ul>
     */
    @Test(
            enabled = GLOBAL_ENABLE)
    public void VerifyGlobalAlternativeAggregations() {
        // get any element at feature level that does not have child elements
        String query = "//core:cityObjectMember/*[not(*)]";
        NodeList nodeList = XMLUtils.getNodeListByXPath(this.testSubject, query);
        if (nodeList.getLength() == 0) {
            // try one level further down
            query = "//core:cityObjectMember/*/*[not(*)]";
            nodeList = XMLUtils.getNodeListByXPath(this.testSubject, query);
        }
        if (nodeList.getLength() != 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String nodeName = node.getNodeName();
                if (nodeName.contains("relatedTo") || node.getAttributes().getLength() == 0
                        || node.getAttributes().getNamedItem("xlink:href") == null) {
                    continue;
                }
                checkXLink(node);
            }
        }
    }

    private void checkXLink(Node node) {
        try {
            String nodeName = node.getNodeName();
            String xlinkHrefTextContent = node.getAttributes().getNamedItem("xlink:href").getTextContent();
            String xlinkHrefTextContent_Modified = xlinkHrefTextContent.replaceFirst("#", "");
            String query = String.format("//%s/*[@gml:id='%s']", nodeName , xlinkHrefTextContent_Modified);
            NodeList nodeList = XMLUtils.getNodeListByXPath(this.testSubject, query);
            Assert.assertTrue(nodeList.getLength() > 0, String.format("Element '%s' contains non resolvable xlink '%s'.", nodeName, xlinkHrefTextContent));             
        } catch (Exception e) {
            fail("Could not check XLink of node.", e);
        }      
    }
}
