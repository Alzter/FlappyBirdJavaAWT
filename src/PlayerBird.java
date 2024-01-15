import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;   // Flexible size arrays

public class PlayerBird extends GameObject{

    public enum PlayerState {
        ALIVE,
        DEAD
    }

    private PlayerState state;
    private Point2D.Double velocity;
    private final static float gravity = 0.15f;

    
    private final static float jumpVelocity = -2.5f; // How much velocity is granted when jumping.
    private final static float movementSpeed = 1.5f; // How much the bird moves to the right every frame.

    private static final String birdSprite = "images/bird.png";
    public static final Point size = new Point(17,12);

    public PlayerBird(double x, double y){
        super(x,y,size.x,size.y,birdSprite);

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
        
        applyGravity(delta);
        applyVelocity(delta);

    }

    public void checkCollisions(ArrayList<GameObject> objects){

        for (GameObject o : objects){
            if (o.intersects(this)){
                die();
                return;
            }
        }
    }

    private void die(){
        velocity.x = 0;
        state = PlayerState.DEAD;
    }

    private void handlePlayerInput(GameInput inputs){
        if (inputs.getMouseJustPressed()){
            velocity.y = jumpVelocity;
        }
    }

    private void applyGravity(double delta){
        velocity.y += gravity * delta;
    }

    private void applyVelocity(double delta){
        x += velocity.x * delta;
        y += velocity.y * delta;
    }
}