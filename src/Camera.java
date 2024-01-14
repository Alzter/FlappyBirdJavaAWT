import java.awt.geom.*;

public class Camera extends Point2D.Double{
    public Point2D.Double zoom;

    public Camera(double x, double y, double zoomX, double zoomY){
        this.x = x;
        this.y = y;
        zoom = new Point2D.Double(zoomX,zoomY);
    }
}