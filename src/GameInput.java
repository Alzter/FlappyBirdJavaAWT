/* Class which encapsulates all inputs which can be detected within the game. */

public class GameInput{

    private boolean mousePressed = false;
    private boolean mouseJustPressed = false;
    private boolean mousePressedPreviousFrame = false;

    public void process(){
        // Set "mouse just pressed" to false the frame after the mouse has been pressed.
        if (mousePressed && mousePressedPreviousFrame){
            mouseJustPressed = false;
        }

        mousePressedPreviousFrame = mousePressed;
    }

    public void setMousePressed(boolean mousePressed){
        this.mousePressed = mousePressed;
        this.mouseJustPressed = mousePressed;
    }

    public boolean getMousePressed(){ return mousePressed; }
    public boolean getMouseJustPressed(){ return mouseJustPressed; }
}