package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * Represents a triangle geometry, which is a type of polygon
 * defined by three points.
 */
public class Triangle extends Polygon {

    /**
     * Constructs a Triangle from three given points.
     *
     * @param q1 The first point of the triangle.
     * @param q2 The second point of the triangle.
     * @param q3 The third point of the triangle.
     */
    public Triangle(Point q1, Point q2, Point q3) {
        super(q1, q2, q3);  // Calls the constructor of Polygon to initialize the triangle with its points
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
}

