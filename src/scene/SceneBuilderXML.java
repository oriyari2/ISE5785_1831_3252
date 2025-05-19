package scene;

import geometries.*;
import lighting.AmbientLight;
import org.w3c.dom.*;
import primitives.Color;
import primitives.Point;
import renderer.ImageWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * A builder class for constructing a Scene and related objects from an XML file.
 */
public class SceneBuilderXML {

    /**
     * Loads a Scene from the given XML file path.
     *
     * @param filePath the path to the XML file describing the scene
     * @return a fully constructed Scene object
     * @throws Exception if the file cannot be parsed
     */
    public static Scene loadSceneFromXML(String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filePath));
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();
        String name = root.getAttribute("name");
        Scene scene = new Scene(name);

        // Parse background color
        String backgroundColor = root.getAttribute("background-color");
        if (!backgroundColor.isEmpty()) {
            scene.setBackground(parseColor(backgroundColor));
        }

        // Parse ambient light
        Node ambientLightNode = root.getElementsByTagName("ambient-light").item(0);
        if (ambientLightNode != null) {
            String ambientColor = ((Element) ambientLightNode).getAttribute("color");
            scene.setAmbientLight(new AmbientLight(parseColor(ambientColor)));
        }

        // Parse geometries
        Node geometriesNode = root.getElementsByTagName("geometries").item(0);
        if (geometriesNode != null) {
            NodeList geometryList = geometriesNode.getChildNodes();
            for (int i = 0; i < geometryList.getLength(); i++) {
                Node geometryNode = geometryList.item(i);
                if (geometryNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element geometryElement = (Element) geometryNode;
                    switch (geometryElement.getTagName()) {
                        case "sphere":
                            scene.geometries.add(parseSphere(geometryElement));
                            break;
                        case "triangle":
                            scene.geometries.add(parseTriangle(geometryElement));
                            break;
                    }
                }
            }
        }

        return scene;
    }

    /**
     * Parses a Color object from a space-separated string.
     *
     * @param colorStr the string containing RGB values separated by spaces
     * @return a Color object
     */
    private static Color parseColor(String colorStr) {
        String[] rgb = colorStr.split(" ");
        if (rgb.length != 3) {
            throw new IllegalArgumentException("Invalid color format: " + colorStr);
        }
        return new Color(
                Double.parseDouble(rgb[0]),
                Double.parseDouble(rgb[1]),
                Double.parseDouble(rgb[2])
        );
    }

    /**
     * Parses a Sphere object from an XML element.
     *
     * @param element the XML element describing the sphere
     * @return a Sphere object
     */
    private static Sphere parseSphere(Element element) {
        String[] centerStr = element.getAttribute("center").split(" ");
        Point center = new Point(
                Double.parseDouble(centerStr[0]),
                Double.parseDouble(centerStr[1]),
                Double.parseDouble(centerStr[2])
        );
        double radius = Double.parseDouble(element.getAttribute("radius"));
        return new Sphere(radius, center);
    }

    /**
     * Parses a Triangle object from an XML element.
     *
     * @param element the XML element describing the triangle
     * @return a Triangle object
     */
    private static Triangle parseTriangle(Element element) {
        String[] p0Str = element.getAttribute("p0").split(" ");
        String[] p1Str = element.getAttribute("p1").split(" ");
        String[] p2Str = element.getAttribute("p2").split(" ");
        Point p0 = new Point(
                Double.parseDouble(p0Str[0]),
                Double.parseDouble(p0Str[1]),
                Double.parseDouble(p0Str[2])
        );
        Point p1 = new Point(
                Double.parseDouble(p1Str[0]),
                Double.parseDouble(p1Str[1]),
                Double.parseDouble(p1Str[2])
        );
        Point p2 = new Point(
                Double.parseDouble(p2Str[0]),
                Double.parseDouble(p2Str[1]),
                Double.parseDouble(p2Str[2])
        );
        return new Triangle(p0, p1, p2);
    }
}