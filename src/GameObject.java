import javax.swing.JFrame;    // Window class
import java.awt.*;
import java.io.File;          // Input/Output handling classes
import java.io.IOException;   // Input/Output handling classes
import javax.imageio.ImageIO; // Image loader class
import java.util.ArrayList;   // Flexible size arrays

import java.awt.geom.*;
import java.awt.image.*;

public class GameObject extends Rectangle2D.Double{

    private BufferedImage sprite;
    private Point spriteSize;
    public int zIndex = 0; // Sprites are sorted by Z indexes when rendering. Sprites with higher Z indexes render above ones with lower ones.
    public Point2D.Double scrollSpeed = new Point2D.Double(1,1); // Controls how quickly the sprite should move when the camera moves.
    public boolean mouseOverObject = false; // True if the mouse is hovering over the game object.

    // Physics process function.
    public void process(double delta, GameInput inputs, ArrayList<GameObject> objects, Camera camera){
        mouseOverObject = getMouseHover(inputs.getMousePosition(), camera);

        if (mouseOverObject && inputs.getMouseJustPressed()){
            clicked();
        }
    }

    // Function which runs when the object is clicked.
    public void clicked(){

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

    // Sprite draw method.
    public void paint(Graphics g, Canvas c, Camera camera){

        // If a sprite BufferedImage resource was assigned to the object:
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

    private BufferedImage loadImage(String path){
        File f = new File(path); // Create a File object from the path.

        try{
            BufferedImage img = ImageIO.read(f); // Read the image in from the file.
            return img;

        } catch (IOException e) {
            e.printStackTrace(); // If there is an error reading the file, throw exception.
            return null;
        }
    }
}