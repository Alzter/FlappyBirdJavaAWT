import javax.swing.*;         // Window (JFrame) class
import java.awt.*;            // Drawing and graphics classes
import java.lang.Math;

import java.awt.event.*;      // Input handling classes

import java.io.File;          // Input/Output handling classes
import java.io.IOException;   // Input/Output handling classes
import javax.imageio.ImageIO; // Image loader class
import java.util.ArrayList;   // Flexible size arrays

public class FlappyJava extends Canvas {

    private static final Point windowSize = new Point(400,600);   // How big should the game window be?
    private static final int gameZoom = 2;                            // How zoomed in should the game be?
    private static final int fps = 60;                                // Target FPS of game
    private static final long targetFrameTime = (1000)/fps;           // How many milliseconds should it take for a frame to elapse?

    private static final int pipeDistance = 200;                      // How far apart should each pipe obstacle be in pixels?

    private long previousFrameTime;                                   // How many milliseconds did the previous frame take?
    private long delta;                                               // previousFrameTime / targetFrameTime
    private JFrame window;
    private Camera camera;
    private GameInput inputs;
    
    private ArrayList<GameObject> objects;
    private ArrayList<GameObject> groundObjects;
    private ArrayList<GameObject> pipeObjects;
    private PlayerBird bird;
    
    // CONSTRUCTOR
    public FlappyJava(String windowName, int width, int height){
        
        inputs = new GameInput();
        camera = new Camera(0,0,gameZoom,gameZoom,width,height);

        setSize(width,height);

        window = new JFrame(windowName);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Make execution stop when window is closed.
        window.add(this);
        window.pack();
        window.setVisible(true);

        objects = new ArrayList<GameObject>();
        groundObjects = new ArrayList<GameObject>();
        pipeObjects = new ArrayList<GameObject>();

        // Mouse click event handling code:
        addMouseListener(new MouseAdapter() { 
            public void mousePressed(MouseEvent me) {
                inputs.setMousePressed(true);
                //System.out.println("MousePress");
            }

            public void mouseReleased(MouseEvent me){
                inputs.setMousePressed(false);
                //System.out.println("MouseRelease");
            }
        });
    }

    // MAIN function
    public static void main(String[] args) {
        FlappyJava frame = new FlappyJava("Flappy Java", windowSize.x,windowSize.y);

        frame.initialiseGame();
        frame.gameLoop();
    }

    public void initialiseGame(){
        // Spawn the player at the center of the screen offset a bit upwards.
        bird = new PlayerBird(0,(windowSize.y * -0.2) / camera.getZoomY());
        addObject(bird);

        // The position of the ground objects is dependent on the camera so we must update the camera after spawning the player.
        updateCamera(camera);

        addGroundObjects();
    }

    // Add the ground objects to the bottom of the game window. Add enough ground to cover the whole window.
    private void addGroundObjects(){
        // How many ground objects do we need to fill the screen?
        int groundObjectsNeeded = (int)Math.ceil((windowSize.x / camera.getZoomX()) / Ground.size.x) + 1;

        // Where is the left-hand edge of the window in global co-ordinates?
        double groundXOrigin = camera.getX();
        // Where is the bottom-hand edge of the window in global co-ordinates?
        double groundYPosition = (camera.getY() + windowSize.y / camera.getZoomY()) - Ground.size.y;

        for(int i = 0; i < groundObjectsNeeded; i++){

            double groundXPosition = groundXOrigin + Ground.size.x * i;

            // Add a ground object at the bottom of the window.
            Ground ground = new Ground(groundXPosition, groundYPosition);

            groundObjects.add(ground);
            addObject(ground);
        }
    }

    private void updateCamera(Camera c){
        // Ensure the bird is at the horizontal center of the window
        camera.setX(bird.x + bird.size.x * 0.5);
    }

    // Update an array of objects so that they seamlessly loop across the screen using "objectTileWidth" as the repeating amount.
    private void updateArrayOfBackgroundObjectsToRepeatHorizontally(ArrayList<GameObject> objects, int objectTileWidth){

        // The left of the screen position rounded to the floor of the background tile's object width.
        double originX = Math.floor(camera.getX() / objectTileWidth) * objectTileWidth;
        for (int i=0; i<objects.size(); i++){

            GameObject object = objects.get(i);

            object.x = originX + i * objectTileWidth;
        }
    }

    // Spawn pipes in front of the player if there are none ahead.
    private void spawnPipesInFrontOfPlayer(GameObject player, ArrayList<GameObject> pipes, int pipeDistance){

        // Get the global position for the right-hand side of the screen.
        double rightEdgeOfScreen = camera.getX() + windowSize.x / camera.getZoomX();

        // Get the next pipe position by finding the next multiple of the pipeDistance from rightEdgeOfScreen
        double nextPipePosition = Math.ceil(rightEdgeOfScreen / pipeDistance) * pipeDistance;

        // Check if any pipe objects already exist at the next pipe position.
        for (GameObject pipe : pipes){
            if (pipe.x == nextPipePosition){
                return;
            }
        }

        // If no pipes exist at the desired pipe position, spawn a new one.
        Pipe pipe = new Pipe(nextPipePosition, 0);

        pipes.add(pipe);
        addObject(pipe);
    }

    // Delete pipes which are behind the player to free memory.
    private void deletePipesBehindPlayer(GameObject player, ArrayList<GameObject> pipes){

    }
    
    private void addObject(GameObject object){ objects.add(object); } 
    private void deleteObject(GameObject object){ objects.remove(object); }

    // Master game loop function. Continues indefinitely
    private void gameLoop(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        while(true){
            this.process(); // Run the game loop.
            this.repaint(); // Draw everything.
            toolkit.sync(); // Force display buffer to flush.

            // Wait a single frame. Source: https://stackoverflow.com/questions/46626715/how-do-i-properly-render-at-a-high-frame-rate-in-pure-java
            try {
                Thread.sleep(targetFrameTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    // Game process function. Called every frame.
    private void process(){
        // How long did it take to render the previous frame?
        previousFrameTime = targetFrameTime; // TODO: Placeholder behaviour sets previous frame time to target frame time, meaning delta will always be 1 regardless of execution speed.
        delta = (targetFrameTime / targetFrameTime); // TODO: Placeholder behaviour sets previous frame time to target frame time, meaning delta will always be 1 regardless of execution speed.

        inputs.process();

        // Make all game objects process.
        for (GameObject object : objects){
            object.process(delta, inputs);
        }

        updateCamera(camera);

        // Ensure the ground repeats endlessly horizontally across the screen.
        updateArrayOfBackgroundObjectsToRepeatHorizontally(groundObjects, Ground.size.x);

        spawnPipesInFrontOfPlayer(bird, pipeObjects, pipeDistance);
    }

    public void paint(Graphics g){
        super.paint(g);

        // Draw all game objects.
        for (GameObject object : objects){
            object.paint(g, this, camera);
        }
    }
}