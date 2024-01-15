import java.awt.*;

public class Background extends GameObject{

    private static final String sprite = "images/background.png";
    public static final Point size = new Point(144,256);
    public static final double scrollSpeed = 0.25;

    public Background(double x, double y){
        super(x,y,size.x,size.y,sprite,-50,scrollSpeed,1);
    }
}