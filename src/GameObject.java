import javax.swing.JFrame;    // Window class
import java.awt.*;
import java.io.File;          // Input/Output handling classes
import java.io.IOException;   // Input/Output handling classes
import javax.imageio.ImageIO; // Image loader class
import java.util.ArrayList;   // Flexible size arrays

import java.awt.geom.*;

public class GameObject extends Rectangle2D.Double{

    private Image sprite;
    private Point spriteSize;
    public int zIndex = 0; // Sprites are sorted by Z indexes when rendering. Sprites with higher Z indexes render above ones with lower ones.
    public Point2D.Double scrollSpeed = new Point2D.Double(1,1); // Controls how quickly the sprite should move when the camera moves.
    public boolean mouseOverObject = false; // True if the mouse is hovering over the game object.
    private Point2D.Double uiPositionOffset = new Point2D.Double(0,0);

    private boolean isUIObject = false;

    // Overwritable physics process function.
    public void process(double delta, GameInput inputs, ArrayList<GameObject> objects, Camera camera){
        mouseOverObject = getMouseHover(inputs.getMousePosition(), camera);

        if (isUIObject){
            x = camera.getAbsoluteX() - (width * 0.5) + uiPositionOffset.x;
            y = camera.getAbsoluteY() - (height * 0.5) + uiPositionOffset.y;
        }
    }

    // Create a GameObject without a sprite.
    public GameObject(double x, double y, double width, double height){
        super(x,y,width,height); // Set the Rectangle parameters to the ones specified.
    }

    // Create a GameObject with a sprite.
    public GameObject(double x, double y, double width, double height, String spriteFilePath){

        this(x,y,width,height); // Call previous constructor function to set Rectangle parameters.
        sprite = loadImage(spriteFilePath);
        
        spriteSize = new Point(sprite.getWidth(null), sprite.getHeight(null));
    }

    // Create a GameObject with a sprite and a custom Z Index.
    public GameObject(double x, double y, double width, double height, String spriteFilePath, int zIndex){
        this(x,y,width,height,spriteFilePath);

        this.zIndex = zIndex;
    }

    // Create a GameObject with a sprite, custom Z Index, and custom Scroll Speed.
    public GameObject(double x, double y, double width, double height, String spriteFilePath, int zIndex, double scrollSpeedX, double scrollSpeedY){
        this(x,y,width,height,spriteFilePath, zIndex);

        scrollSpeed.x = scrollSpeedX;
        scrollSpeed.y = scrollSpeedY;
    }

    // Create a game object at the center of the screen. A camera object is required for this.
    public GameObject(Camera c, double width, double height, String spriteFilePath, int zIndex){

        this(
            // Get the local position for the center of the screen minus half of the object's width and height.
            // This will give us a position that will draw the object at the center of the screen.
            c.getAbsoluteX() - (width * 0.5),
            c.getAbsoluteY() - (height * 0.5),
            width,
            height,
            spriteFilePath,
            zIndex
        );

        isUIObject = true;
    }

    // Create a game object at the center of the screen with a configurable X and Y offset.
    public GameObject(Camera c, double xOffset, double yOffset, double width, double height, String spriteFilePath, int zIndex){
        this(c,width,height,spriteFilePath,zIndex);

        uiPositionOffset.x = xOffset;
        uiPositionOffset.y = yOffset;
        x += xOffset;
        y += yOffset;
    }


    // Sprite draw method.
    public void paint(Graphics g, Canvas c, Camera camera){

        // If a sprite Image resource was assigned to the object:
        if (sprite != null){

            // Get the position the object should render on the screen based on the camera's position and zoom level.
            Point2D.Double screenPosition = getPositionOnScreen(camera);
            
            // Set "spriteSize" to how big we want the sprite to be in pixels.
            Point2D.Double spriteCameraSize = getSpriteSizeOnScreen(camera);

            // Get the Graphics object to draw the sprite in the Frame.
            g.drawImage(sprite, (int)screenPosition.x, (int)screenPosition.y, (int)spriteCameraSize.x, (int)spriteCameraSize.y, c);
        }
    }

    private Point2D.Double getPositionOnScreen(Camera camera){
        return new Point2D.Double((x - camera.getX()) * (camera.getZoomX() * scrollSpeed.x), (y - camera.getY()) * (camera.getZoomY() * scrollSpeed.y));
    }

    private Point2D.Double getSpriteSizeOnScreen(Camera camera){
        return new Point2D.Double(spriteSize.x * camera.getZoomX(), spriteSize.y * camera.getZoomY());
    }

    
    // Returns true if the mouse is hovering over the object.
    public boolean getMouseHover(Point mousePosition, Camera camera){
        // Get the position the object should render on the screen based on the camera's position and zoom level.
        Point2D.Double screenPosition = getPositionOnScreen(camera);

        // Set "spriteSize" to how big we want the sprite to be in pixels.
        Point2D.Double objectWidth = new Point2D.Double(width * camera.getZoomX(), height * camera.getZoomY());

        return getMouseHover(mousePosition, screenPosition, objectWidth);

    }

    // Returns true if the mouse is hovering over the object.
    private boolean getMouseHover(Point mousePosition, Point2D.Double screenPosition, Point2D.Double spriteCameraSize){
        Rectangle clickableArea = new Rectangle((int)screenPosition.x, (int)screenPosition.y, (int)spriteCameraSize.x, (int)spriteCameraSize.y);
        return clickableArea.contains(mousePosition);
    }

    private Image loadImage(String path){
        File f = new File(path); // Create a File object from the path.

        try{
            Image img = ImageIO.read(f); // Read the image in from the file.
            return img;

        } catch (IOException e) {
            e.printStackTrace(); // If there is an error reading the file, throw exception.
            return null;
        }
    }
}