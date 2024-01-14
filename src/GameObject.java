
import javax.swing.JFrame;    // Window class
import java.awt.*;
import java.io.File;          // Input/Output handling classes
import java.io.IOException;   // Input/Output handling classes
import javax.imageio.ImageIO; // Image loader class

import java.awt.geom.*;

public class GameObject extends Rectangle2D.Double{

    private Image sprite;

    // Overwritable physics process function.
    public void process(double delta, GameInput inputs){
        
    }

    // Create a GameObject without a sprite.
    public GameObject(double x, double y, double width, double height){
        super(x,y,width,height); // Set the Rectangle parameters to the ones specified.
    }

    // Create a GameObject with a sprite.
    public GameObject(double x, double y, double width, double height, String spriteFilePath){

        this(x,y,width,height); // Call previous constructor function to set Rectangle parameters.
        sprite = loadImage(spriteFilePath);
        
    }

    // Sprite draw method.
    public void paint(Graphics g, Canvas c, Camera camera){

        // If a sprite Image resource was assigned to the object:
        if (sprite != null){

            // Add the Camera's position to the position of the object to get the object's global position.
            Point2D.Double globalPosition = new Point2D.Double(x - camera.x, y - camera.y);

            // Cast the object's global position to an integer.
            Point intGlobalPosition = new Point((int)globalPosition.x, (int)globalPosition.y);
            
            // Get the Graphics object to draw the sprite in the Frame.
            g.drawImage(sprite, intGlobalPosition.x, intGlobalPosition.y, c);
        }
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