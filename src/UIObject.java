// UI Objects are game objects which always draw to the center of the screen.

import java.awt.geom.*;
import java.util.ArrayList;

public class UIObject extends GameObject{

    private Point2D.Double positionOffset = new Point2D.Double(0, 0);

    public void process(double delta, GameInput inputs, ArrayList<GameObject> objects, Camera camera){
        super.process(delta, inputs, objects, camera);

        // Move the UI object to the center of the screen
        x = camera.getAbsoluteX() - (width * 0.5) + positionOffset.x;
        y = camera.getAbsoluteY() - (height * 0.5) + positionOffset.y;
    }

    // Create a UI object at the center of the screen. A camera object is required for this.
    public UIObject(Camera c, double width, double height, String spriteFilePath, int zIndex){

        super(
            // Get the local position for the center of the screen minus half of the object's width and height.
            // This will give us a position that will draw the object at the center of the screen.
            c.getAbsoluteX() - (width * 0.5),
            c.getAbsoluteY() - (height * 0.5),
            width,
            height,
            spriteFilePath,
            zIndex
        );
    }

    // Create a UI object at the center of the screen with a configurable X and Y offset.
    public UIObject(Camera c, double xOffset, double yOffset, double width, double height, String spriteFilePath, int zIndex){
        this(c,width,height,spriteFilePath,zIndex);

        positionOffset = new Point2D.Double(xOffset, yOffset);
        x += positionOffset.x;
        y += positionOffset.y;
    }
}