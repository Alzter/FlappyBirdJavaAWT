import java.awt.*;
import java.awt.geom.*;

public class PlayerBird extends GameObject{

    public enum PlayerState {
        ALIVE,
        DEAD
    }

    private PlayerState state;
    private Point2D.Double velocity;
    private final static float gravity = 0.3f;

    
    private final static float jumpVelocity = -5f; // How much velocity is granted when jumping.
    private final static float movementSpeed = 2f; // How much the bird moves to the right every frame.

    private static final String birdSprite = "images/bird.png";
    private static final Point birdSize = new Point(17,12);

    public PlayerBird(double x, double y){
        super(x,y,birdSize.x,birdSize.y,birdSprite);

        velocity = new Point2D.Double(movementSpeed,0f);
        state = PlayerState.ALIVE;
    }

    public void process(double delta, GameInput inputs){

        switch (state){
            case ALIVE:
                handlePlayerInput(inputs);
                break;
            case DEAD:
                break;
        }
        
        applyGravity();
        applyVelocity();

    }

    private void handlePlayerInput(GameInput inputs){
        if (inputs.getMouseJustPressed()){
            velocity.y = jumpVelocity;
        }
    }

    private void applyGravity(){
        velocity.y += gravity;
    }

    private void applyVelocity(){
        x += velocity.x;
        y += velocity.y;
    }
}