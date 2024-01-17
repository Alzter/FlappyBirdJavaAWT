import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;   // Flexible size arrays

public class PlayerBird extends GameObject{

    public PlayerState state;
    private Point2D.Double velocity;
    private final static float gravity = 0.15f;

    
    private final static float jumpVelocity = -2.5f; // How much velocity is granted when jumping.
    private final static float movementSpeed = 1.5f; // How much the bird moves to the right every frame.

    private static final String birdSprite = "images/bird.png";
    public static final Point size = new Point(17,12);
    
    private SoundPlayer sfx;
    private static final String jumpSound = "sounds/wing.wav";
    private static final String dieSound = "sounds/hit.wav";
    private static final String fallSound = "sounds/die.wav";

    private boolean wasCollidingWithPointLastFrame = false; // True if the bird was intersecting a ScorePoint last frame.
    private boolean isCollidingWithPoint = false; // True if the bird is intersecting a ScorePoint.
    private boolean isJustCollidingWithPoint = false; // Only becomes true on the first frame that the bird collides with the point.

    public PlayerBird(double x, double y){
        super(x,y,size.x,size.y,birdSprite,1);

        velocity = new Point2D.Double(movementSpeed,0f);
        state = PlayerState.IDLE;
        sfx = new SoundPlayer();
    }

    public void process(double delta, GameInput inputs, ArrayList<GameObject> objects, Camera camera){

        switch (state){
            case IDLE:
                handlePlayerInput(inputs);
            case ALIVE:
                handlePlayerInput(inputs);
                checkCollisions(objects);
                checkScorePoints(objects);
                break;
            default:
                break;
        }
        
        if (state != PlayerState.IDLE){
            applyGravity(delta);
        }
        
        applyVelocity(delta);

        isJustCollidingWithPoint = isCollidingWithPoint && !wasCollidingWithPointLastFrame;
        wasCollidingWithPointLastFrame = isCollidingWithPoint;

        super.process(delta, inputs, objects, camera);
    }

    public void checkCollisions(ArrayList<GameObject> objects){
        if (checkIsOnGround(objects) || checkIsOnWall(objects)){
            die();
        }
    }

    private boolean checkIsOnGround(ArrayList<GameObject> objects){
        y += 1;
        for (GameObject o : objects){
            if (o instanceof Ground){
                if (o.intersects(this)){
                    y -= 1;
                    return true;
                }
            }
        }
        y -= 1;
        return false;
    }

    private boolean checkIsOnWall(ArrayList<GameObject> objects){
        for (GameObject o : objects){
            if (o instanceof Pipe){
                if (o.intersects(this)){
                    return true;
                }
            }
        }
        return false;
    }

    public void checkScorePoints(ArrayList<GameObject> objects){
        isCollidingWithPoint = false;
        for (GameObject o : objects){
            if (o != this){
                if (o instanceof ScorePoint){
                    if (o.intersects(this)){
                        isCollidingWithPoint = true;
                    }
                }
            }
        }
    }

    // Returns true iff the bird has collided with a new ScorePoint object this frame.
    public boolean getIsCollidingWithPoint(){
        return isJustCollidingWithPoint;
    }

    private void die(){
        velocity.x = 0;
        velocity.y = 0;
        state = PlayerState.DEAD;

        sfx.playSound(dieSound);

        // Only play the fall sound after 0.5 seconds.
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    sfx.playSound(fallSound);
                }
            }, 
            500
        );
        
        // After 1 second, switch to the "Game Over" state
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    state = PlayerState.GAMEOVER;
                }
            }, 
            1000
        );
    }

    private void handlePlayerInput(GameInput inputs){
        if (inputs.getMouseJustPressed()){
            sfx.playSound(jumpSound);
            velocity.y = jumpVelocity;
            if (state == PlayerState.IDLE){ state = PlayerState.ALIVE; }
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