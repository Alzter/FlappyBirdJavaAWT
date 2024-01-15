import java.awt.geom.Point2D;
import java.awt.Point;

public class Camera{
    private Point2D.Double position;
    private Point2D.Double zoom;
    private Point windowSize;

    public Camera(double x, double y, double zoomX, double zoomY, int windowSizeX, int windowSizeY){
        position = new Point2D.Double(x,y);
        zoom = new Point2D.Double(zoomX,zoomY);
        windowSize = new Point(windowSizeX, windowSizeY);
    }

    public double getX(){
        return this.position.x - windowSize.x * 0.5 / zoom.x;
    }

    public double getY(){
        return this.position.y - windowSize.y * 0.5 / zoom.y;
    }

    public void setX(double x){ this.position.x = x; }
    public void setY(double y){ this.position.y = y; }

    public double getZoomX(){ return zoom.x; }
    public double getZoomY(){ return zoom.y; }
    public void setZoomX(double zoomX){ this.zoom.x = zoomX; }
    public void setZoomY(double zoomY){ this.zoom.y = zoomY; }
}