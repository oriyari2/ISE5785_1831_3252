package scene;

import geometries.*;
import lighting.AmbientLight;
import org.w3c.dom.*;
import primitives.Color;
import primitives.Double3;
import primitives.Point;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * A utility class to build a {@link Scene} object by parsing an XML file.
 * <p>
 * The XML defines the scene's name, background color, ambient light, and geometries.
 */
public class SceneBuilderXML {

    /**
     * A map of geometry XML tag names to their corresponding parsing functions.
     */
    private static final Map<String, Function<Element, Geometry>> geometryParsers = Map.of(
            "sphere", SceneBuilderXML::parseSphere,
            "triangle", SceneBuilderXML::parseTriangle
            // Add more types as needed, e.g. "plane", "polygon"
    );

    /**
     * Parses a {@link Scene} object from the given XML file.
     *
     * @param filePath path to the XML scene description file
     * @return a constructed {@link Scene} instance
     * @throws Exception if the file cannot be read or parsed
     */
    public static Scene loadSceneFromXML(String filePath) throws Exception {
        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new File(filePath));

        Element root = document.getDocumentElement();
        Scene scene = new Scene(root.getAttribute("name"));

        // Parse background color if present
        Optional.ofNullable(root.getAttribute("background-color"))
                .filter(s -> !s.isEmpty())
                .map(SceneBuilderXML::parseColor)
                .ifPresent(scene::setBackground);

        // Parse ambient light if present
        Optional.ofNullable(root.getElementsByTagName("ambient-light").item(0))
                .filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
                .map(n -> (Element) n)
                .map(e -> e.getAttribute("color"))
                .map(SceneBuilderXML::parseColor)
                .map(AmbientLight::new)
                .ifPresent(scene::setAmbientLight);

        // Parse geometries list
        Node geometriesNode = root.getElementsByTagName("geometries").item(0);
        if (geometriesNode != null && geometriesNode.getNodeType() == Node.ELEMENT_NODE) {
            parseGeometries((Element) geometriesNode, scene);
        }

        return scene;
    }

    /**
     * Parses the geometries defined in the XML element and adds them to the scene.
     *
     * @param geometriesElement XML element containing child geometry elements
     * @param scene             the {@link Scene} to which geometries are added
     */
    private static void parseGeometries(Element geometriesElement, Scene scene) {
        NodeList geometryList = geometriesElement.getChildNodes();
        for (int i = 0; i < geometryList.getLength(); i++) {
            Node node = geometryList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                Function<Element, Geometry> parser = geometryParsers.get(elem.getTagName());
                if (parser != null) {
                    Geometry geometry = parser.apply(elem);
                    if (geometry != null) {
                        scene.geometries.add(geometry);
                    }
                } else {
                    System.err.println("Unknown geometry type: " + elem.getTagName());
                }
            }
        }
    }

    /**
     * Parses a {@link Color} object from a space-separated RGB string.
     *
     * @param colorStr RGB string (e.g., "255 200 150")
     * @return a {@link Color} instance
     */
    private static Color parseColor(String colorStr) {
        Double3 rgb = parseDouble3(colorStr);
        return new Color(rgb.d1(), rgb.d2(), rgb.d3());
    }


    /**
     * Parses a {@link Point} object from a space-separated string.
     *
     * @param str string representing a 3D point (e.g., "1.0 2.0 -3.5")
     * @return a {@link Point} instance
     */
    private static Point parsePoint(String str) {
        return new Point(parseDouble3(str));
    }

    /**
     * Parses a {@link Double3} object from a space-separated string of 3 numbers.
     *
     * @param str the input string (e.g., "1.0 2.0 3.0")
     * @return a {@link Double3} instance
     * @throws IllegalArgumentException if the string format is invalid
     */
    private static Double3 parseDouble3(String str) {
        String[] parts = str.trim().split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid format for Double3: " + str);
        }
        return new Double3(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])
        );
    }

    /**
     * Parses a {@link Sphere} from its corresponding XML element.
     *
     * @param element XML element describing a sphere
     * @return a {@link Sphere} instance
     */
    private static Sphere parseSphere(Element element) {
        Point center = parsePoint(getRequiredAttribute(element, "center"));
        double radius = Double.parseDouble(getRequiredAttribute(element, "radius"));
        return new Sphere(center, radius);
    }

    /**
     * Parses a {@link Triangle} from its corresponding XML element.
     *
     * @param element XML element describing a triangle
     * @return a {@link Triangle} instance
     */
    private static Triangle parseTriangle(Element element) {
        Point p0 = parsePoint(getRequiredAttribute(element, "p0"));
        Point p1 = parsePoint(getRequiredAttribute(element, "p1"));
        Point p2 = parsePoint(getRequiredAttribute(element, "p2"));
        return new Triangle(p0, p1, p2);
    }

    /**
     * Retrieves a required attribute from an XML element.
     *
     * @param element  XML element
     * @param attrName name of the required attribute
     * @return the attribute value
     * @throws IllegalArgumentException if the attribute is missing or empty
     */
    private static String getRequiredAttribute(Element element, String attrName) {
        String value = element.getAttribute(attrName);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Missing required attribute: " + attrName);
        }
        return value;
    }
}
