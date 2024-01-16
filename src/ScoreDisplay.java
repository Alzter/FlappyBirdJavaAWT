import javax.swing.JFrame;    // Window class
import java.awt.*;
import java.io.File;          // Input/Output handling classes
import java.io.IOException;   // Input/Output handling classes
import javax.imageio.ImageIO; // Image loader class
import java.util.ArrayList;   // Flexible size arrays

import java.awt.geom.*;

public class ScoreDisplay {

    private final static int scoreYPosition = 32; // How low should the score appear on the screen?
    private ArrayList<Image> digitSprites;
    private final static String digitImagePath = "images/digits/";
    private final static int digitWidth = 12;

    public ScoreDisplay(){
        digitSprites = loadDigitSprites();
    }

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

    public void paint(int score, Graphics g, Canvas c, Camera camera, Point windowSize){

        ArrayList<Integer> scoreDigits = splitIntegerIntoDigits(score);

        for (int digitOrder=0;digitOrder<scoreDigits.size();digitOrder++){

            int digit = (int)scoreDigits.get(digitOrder);

            // Get the Image for the digit.
            Image digitSprite = digitSprites.get(digit);

            // Get the digit's size it should be drawn at.
            Point2D.Double digitSpriteSize = new Point2D.Double(digitSprite.getWidth(null), digitSprite.getHeight(null));
            digitSpriteSize.x *= camera.getZoomX();
            digitSpriteSize.y *= camera.getZoomY();

            double windowCenterX = windowSize.x * 0.5 - digitSpriteSize.x * 0.5;
            double digitWidthZoomed = digitWidth * camera.getZoomX();

            windowCenterX -= Math.max(scoreDigits.size() - 1, 0) * digitWidthZoomed * 0.5;

            double digitPosition = windowCenterX + (digitOrder * digitWidthZoomed);

            g.drawImage(digitSprite, (int)digitPosition, scoreYPosition, (int)digitSpriteSize.x, (int)digitSpriteSize.y, c);

        }
    }

    // Takes an integer number and splits it into an array of its digits. 123 -> [1,2,3]
    private ArrayList<Integer> splitIntegerIntoDigits(int number){

        ArrayList<Integer> intList = new ArrayList<Integer>();
        
        // Convert the number into a string.
        String numberString = String.valueOf(number);

        // Split number string into an array of characters.
        char[] digitChars = numberString.toCharArray();

        for (char digitChar : digitChars){

            // Convert each character into an int.
            Integer digit = Integer.parseInt(String.valueOf(digitChar));

            intList.add(digit);
        }

        return intList;
    }
}
