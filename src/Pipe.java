import java.awt.Point;

public class Pipe extends GameObject{

    private static final String spriteTop = "images/pipe_top.png";
    private static final String spriteBottom = "images/pipe_bottom.png";
    public static final Point size = new Point(26,400);

    public Pipe(double x, double y, boolean top){
        super(x,y - size.y * boolToInt(top),size.x,size.y,(top) ? spriteTop : spriteBottom);
        
    }

    private static int boolToInt(boolean b){
        return (b) ? 1 : 0;
    }
}