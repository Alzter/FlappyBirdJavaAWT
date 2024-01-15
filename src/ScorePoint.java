import java.awt.geom.*;

public class ScorePoint extends GameObject{

    public static final Point2D.Double size = new Point2D.Double(12, 6000);

    public ScorePoint(double x, double y){
        super(x, y, size.x, size.y);
    }
}