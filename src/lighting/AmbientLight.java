package lighting;
import primitives.Color;


/**
 * Ambient light class represents the ambient light in the scene.
 * It is used to illuminate the scene uniformly, without any direction.
 * The intensity of the ambient light is represented by a color.
 *
 */
public class AmbientLight extends Light {

    /**
     * A constant representing no ambient light (black).
     */
   public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructor for the AmbientLight class.
     * @param IA The intensity of the ambient light.
     */
    public AmbientLight(Color IA) {
        super(IA);
    }
}
