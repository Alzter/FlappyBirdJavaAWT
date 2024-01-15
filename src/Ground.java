import java.awt.Point;

public class Ground extends GameObject{

    private static final String sprite = "images/ground.png";
    public static final Point size = new Point(168,56);

    public Ground(double x, double y){
        super(x,y,size.x,size.y,sprite,2);
    }
}