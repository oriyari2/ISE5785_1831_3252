package geometries;

import primitives.*;
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
        // 1. מוצאים חיתוך עם המישור של המשולש
        List<Point> planeIntersections = plane.findIntersections(ray);
        // אם אין נקודת חיתוך עם המישור - אין חיתוך עם המשולש
        if (planeIntersections == null) {
            return null;
        }

        // ניקח את נקודת החיתוך היחידה
        Point P = planeIntersections.get(0);

        // 2. בדיקה האם הנקודה בתוך המשולש
        Point p1 = vertices.get(0);
        Point p2 = vertices.get(1);
        Point p3 = vertices.get(2);

        // בדיקה אם הנקודה P זהה לאחד הקודקודים
        if (P.equals(p1) || P.equals(p2) || P.equals(p3)) {
            return null; // הנקודה בדיוק על קודקוד - לא נחשב כחיתוך
        }

        // נבנה וקטורים מהקודקודים לנקודה P
        Vector v1, v2, v3;
        try {
            v1 = P.subtract(p1); // שינוי כיוון הווקטור - מהקודקוד לנקודה P
            v2 = P.subtract(p2);
            v3 = P.subtract(p3);
        } catch (IllegalArgumentException e) {
            // אם נזרקה שגיאה, סימן שאחת הנקודות זהה ל-P
            return null;
        }

        // בדיקה אם הנקודה על אחת מצלעות המשולש
        // נשתמש בווקטורים של צלעות המשולש
        Vector e1 = p2.subtract(p1);
        Vector e2 = p3.subtract(p2);
        Vector e3 = p1.subtract(p3);

        // בדיקה אם P על צלע - אם כן, נחזיר null
        if (isColinear(v1, e1) || isColinear(v2, e2) || isColinear(v3, e3)) {
            return null;
        }

        // נמצא את הנורמל למישור של המשולש
        Vector n = plane.getNormal(P);

        // נבצע את שיטת המכפלות הווקטוריות בצורה בטוחה
        try {
            // נשתמש בצלעות המשולש ובווקטורים מהקודקודים לנקודה P
            Vector n1 = e1.crossProduct(v1);
            Vector n2 = e2.crossProduct(v2);
            Vector n3 = e3.crossProduct(v3);

            // נבדוק אם כל המכפלות הסקלריות שלהם עם הנורמל באותו סימן
            double s1 = Util.alignZero(n1.dotProduct(n));
            double s2 = Util.alignZero(n2.dotProduct(n));
            double s3 = Util.alignZero(n3.dotProduct(n));

            // בדיקה אם נקודה על צלע או קודקוד
            if (Util.isZero(s1) || Util.isZero(s2) || Util.isZero(s3)) {
                return null;
            }

            // בדיקה אם כל הסימנים זהים
            if ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0)) {
                // הנקודה בתוך המשולש
                return List.of(P);
            }
        } catch (IllegalArgumentException e) {
            // אם נזרקה שגיאה במכפלה וקטורית, סימן שהנקודה על צלע
            return null;
        }

        // הנקודה מחוץ למשולש
        return null;
    }

    // פונקציית עזר לבדיקה אם וקטורים קווים (colinear)
    private boolean isColinear(Vector v1, Vector v2) {
        try {
            Vector crossProduct = v1.crossProduct(v2);
            return Util.isZero(crossProduct.lengthSquared());
        } catch (IllegalArgumentException e) {
            // אם נזרקה שגיאה, סימן שהווקטורים קווים
            return true;
        }
    }
}

