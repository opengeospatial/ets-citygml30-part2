package org.opengis.cite.citygml30part2.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import org.opengis.cite.citygml30part2.Namespaces;

/**
 * Provides namespace bindings for evaluating XPath 1.0 expressions using the
 * JAXP XPath API. A namespace name (URI) may be bound to only one prefix.
 */
public class NamespaceBindings implements NamespaceContext {

    private Map<String, String> bindings = new HashMap<String, String>();

    @Override
    public String getNamespaceURI(String prefix) {
        String nsName = null;
        for (Map.Entry<String, String> binding : bindings.entrySet()) {
            if (binding.getValue().equals(prefix)) {
                nsName = binding.getKey();
                break;
            }
        }
        return nsName;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return bindings.get(namespaceURI);
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        return Arrays.asList(getPrefix(namespaceURI)).iterator();
    }

    /**
     * Adds a namespace binding that associates a namespace name with a prefix.
     * If a binding for a given namespace name already exists it will be
     * replaced.
     * 
     * @param namespaceURI
     *            A String denoting a namespace name (an absolute URI value).
     * @param prefix
     *            A prefix associated with the namespace name.
     */
    public void addNamespaceBinding(String namespaceURI, String prefix) {
        bindings.put(namespaceURI, prefix);
    }

    /**
     * Adds all of the supplied namespace bindings to the existing set of
     * entries.
     * 
     * @param nsBindings
     *            A Map containing a collection of namespace bindings where the
     *            key is an absolute URI specifying the namespace name and the
     *            value denotes the associated prefix.
     */
    public void addAllBindings(Map<String, String> nsBindings) {
        if (null != nsBindings)
            bindings.putAll(nsBindings);
    }

    /**
     * Returns an unmodifiable view of the declared namespace bindings.
     * 
     * @return An immutable Map containing zero or more namespace bindings where
     *         the key is an absolute URI specifying the namespace name and the
     *         value is the associated prefix.
     */
    public Map<String, String> getAllBindings() {
        return Collections.unmodifiableMap(this.bindings);
    }

    /**
     * Creates a NamespaceBindings object that declares the following namespace
     * bindings:
     * 
     * <ul>
     * <li>ows: {@value org.opengis.cite.citygml30part2.Namespaces#OWS}</li>
     * <li>xlink: {@value org.opengis.cite.citygml30part2.Namespaces#XLINK}</li>
     * <li>gml: {@value org.opengis.cite.citygml30part2.Namespaces#GML}</li>
     * </ul>
     * 
     * @return A NamespaceBindings object.
     */
    public static NamespaceBindings withStandardBindings() {
        NamespaceBindings nsBindings = new NamespaceBindings();
        nsBindings.addNamespaceBinding(Namespaces.OWS, "ows");
        nsBindings.addNamespaceBinding(Namespaces.XLINK, "xlink");
        nsBindings.addNamespaceBinding(Namespaces.GML, "gml");
        return nsBindings;
    }

    @Override
    public String toString() {
        return "NamespaceBindings:\n" + bindings;
    }

    public static Map<String, String> getNamespaceMap() {
        HashMap<String, String> cityGmlMap = new HashMap<>();

        cityGmlMap.put("http://www.opengis.net/citygml/appearance/3.0", "app");
        cityGmlMap.put("http://www.opengis.net/citygml/bridge/3.0", "brid");
        cityGmlMap.put("http://www.opengis.net/citygml/building/3.0", "bldg");
        cityGmlMap.put("http://www.opengis.net/citygml/pointcloud/3.0", "pcl");
        cityGmlMap.put("http://www.opengis.net/citygml/cityfurniture/3.0", "frn");
        cityGmlMap.put("http://www.opengis.net/citygml/cityobjectgroup/3.0", "grp");
        cityGmlMap.put("http://www.opengis.net/citygml/construction/3.0", "con");
        cityGmlMap.put("http://www.opengis.net/citygml/3.0", "core");
        cityGmlMap.put("http://www.opengis.net/citygml/dynamizer/3.0", "dyn");
        cityGmlMap.put("http://www.opengis.net/citygml/generics/3.0", "gen");
        cityGmlMap.put("http://www.opengis.net/citygml/landuse/3.0", "luse");
        cityGmlMap.put("http://www.opengis.net/citygml/relief/3.0", "dem");
        cityGmlMap.put("http://www.opengis.net/citygml/transportation/3.0", "tran");
        cityGmlMap.put("http://www.opengis.net/citygml/tunnel/3.0", "tun");
        cityGmlMap.put("http://www.opengis.net/citygml/vegetation/3.0", "veg");
        cityGmlMap.put("http://www.opengis.net/citygml/versioning/3.0", "vers");
        cityGmlMap.put("http://www.opengis.net/citygml/waterbody/3.0", "wtr");

        return cityGmlMap;
    }

    public static String getNameSpacePrefix(String ns) {
        Map<String, String> cityGmlMap = getNamespaceMap();
        return cityGmlMap.get(ns);
    }
}
