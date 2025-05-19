package lighting;
import primitives.Color;


public class AmbientLight {
   private final Color intensity;
   public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    public AmbientLight(Color IA) {
        this.intensity = IA;
    }

    public Color getIntensity() {
        return intensity;
    }
}
