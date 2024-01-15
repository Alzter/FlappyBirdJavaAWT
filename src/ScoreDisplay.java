import javax.swing.JFrame;    // Window class
import java.awt.*;
import java.io.File;          // Input/Output handling classes
import java.io.IOException;   // Input/Output handling classes
import javax.imageio.ImageIO; // Image loader class
import java.util.ArrayList;   // Flexible size arrays

import java.awt.geom.*;

public class ScoreDisplay {
    private ArrayList<Image> digitSprites;
    private final static String digitImagePath = "images/digits/";
    private final static int digitWidth = 12;
    private int score;

    public ScoreDisplay(){
        digitSprites = loadDigitSprites();
        score = 0;
    }

    public void setScore(int score){ this.score = score; }

    // Read in all of the number textures from the game and load them each as images.
    private ArrayList<Image> loadDigitSprites(){

        ArrayList<Image> digitSprites = new ArrayList<Image>();
        for (int i=0; i<=9; i++){
            String digitSpritePath = (digitImagePath + String.valueOf(i) + ".png");
            Image digitSprite = loadImage(digitSpritePath);
            digitSprites.add(digitSprite);
        }
        return digitSprites;

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

    public void paint(Graphics g, Canvas c, Camera camera, Point windowSize){

    }
}
