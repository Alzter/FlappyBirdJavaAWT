import java.awt.Point;

public class Pipe extends GameObject{

    private static final String sprite = "images/pipe.png";
    public static final Point size = new Point(26,160);

    public Pipe(double x, double y){
        super(x,y,size.x,size.y,sprite);
    }
}