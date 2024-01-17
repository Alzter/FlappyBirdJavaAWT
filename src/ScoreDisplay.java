import javax.swing.JFrame;    // Window class
import java.awt.*;
import java.io.File;          // Input/Output handling classes
import java.io.IOException;   // Input/Output handling classes
import javax.imageio.ImageIO; // Image loader class
import java.util.ArrayList;   // Flexible size arrays
import java.util.HashMap;
import java.util.Map;         // Dictionaries.
import java.awt.geom.*;

public class ScoreDisplay {
    private final static int scoreYPosition = 32; // How low should the score appear on the screen?
    private Map<FontSize, ArrayList<Image>> digitSpriteDictionary;

    private final static Map<FontSize, String> fontFiles = Map.of(
        FontSize.LARGE, "images/digits/",
        FontSize.MEDIUM, "images/digits_medium/",
        FontSize.SMALL, "images/digits_small/"

    );

    //private final static String digitImagePath = "images/digits/";

    private final static Map<FontSize, Integer> digitWidths = Map.of(
        FontSize.LARGE, 12,
        FontSize.MEDIUM, 7,
        FontSize.SMALL, 6
    );

    public ScoreDisplay(){
        digitSpriteDictionary = loadDigitSprites();
    }

    // Read in all of the number textures from the game and load them each as images.
    private Map<FontSize, ArrayList<Image>> loadDigitSprites(){

        Map<FontSize, ArrayList<Image>> digitSpriteDictionary = new HashMap<FontSize, ArrayList<Image>>();

        for (FontSize size : fontFiles.keySet()){
            String digitImagePath = fontFiles.get(size);

            ArrayList<Image> digitSprites = new ArrayList<Image>();
            for (int i=0; i<=9; i++){
                String digitSpritePath = (digitImagePath + String.valueOf(i) + ".png");
                Image digitSprite = loadImage(digitSpritePath);
                digitSprites.add(digitSprite);
            }

            digitSpriteDictionary.put(size, digitSprites);
        }

        return digitSpriteDictionary;
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

    public void paint(FontSize size, FontAlign alignment, int number, Graphics g, Canvas c, Camera camera, Point windowSize){

        ArrayList<Image> digitSprites = digitSpriteDictionary.get(size);
        int digitWidth = (int)digitWidths.get(size);
        
        ArrayList<Integer> digits = splitIntegerIntoDigits(number);

        for (int digitOrder=0;digitOrder<digits.size();digitOrder++){

            int digit = (int)digits.get(digitOrder);

            // Get the Image for the digit.
            Image digitSprite = digitSprites.get(digit);

            // Get the digit's size it should be drawn at.
            Point2D.Double digitSpriteSize = new Point2D.Double(digitSprite.getWidth(null), digitSprite.getHeight(null));
            digitSpriteSize.x *= camera.getZoomX();
            digitSpriteSize.y *= camera.getZoomY();

            double windowCenterX = windowSize.x * 0.5 - digitSpriteSize.x * 0.5;
            double digitWidthZoomed = digitWidth * camera.getZoomX();

            // How many pixels would align the printed number to the left? 
            double leftAlignmentOffset = Math.max(digits.size() - 1, 0) * digitWidthZoomed;
            
            switch (alignment){
                case CENTER:
                    windowCenterX -= leftAlignmentOffset * 0.5;
                    break;
                case LEFT:
                    windowCenterX -= leftAlignmentOffset * 1;
                    break;
                case RIGHT:
                    break;
            }

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
