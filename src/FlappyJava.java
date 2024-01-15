import javax.swing.*;         // Window (JFrame) class
import java.awt.*;            // Drawing and graphics classes
import java.lang.Math;

import java.awt.event.*;      // Input handling classes

import java.io.File;          // Input/Output handling classes
import java.io.IOException;   // Input/Output handling classes
import javax.imageio.ImageIO; // Image loader class
import java.util.ArrayList;   // Flexible size arrays
import java.util.Collections; // Array sorting methods
import java.util.Comparator;

public class FlappyJava extends Canvas {

    private static final Point windowSize = new Point(600,800);   // How big should the game window be?
    private static final int gameZoom = 3;                            // How zoomed in should the game be?
    private static final int fps = 60;                                // FPS that the game shall run at.
    private static final int targetFps = 60;                          // Target FPS of game (DO NOT CHANGE)
    private static final long frameTime = (1000)/fps;                 // How many milliseconds will it take for a frame to elapse?
    private static final long targetFrameTime = (1000)/targetFps;     // How many milliseconds should it take for a frame to elapse?

    private static final int pipeXDistance = 26 + 70;                 // How far apart should each pipe obstacle be in pixels?
    private static final int pipeYGap = 46;                           // How big should the gap in-between the pipes be in pixels?
    private static final float pipeYVariation = 0.6f;                 // How much should pipes vary on the Y axis? (1 = from top of screen to bottom, 0 = always in center of screen)

    private double roofYPosition;                                     // What is the global y position of the (invisible) ceiling?
    private double groundYPosition;                                   // What is the global y position of the ground?

    private long previousFrameTime;                                   // How many milliseconds did the previous frame take?
    private long delta;                                               // previousFrameTime / targetFrameTime
    private JFrame window;
    private Camera camera;
    private GameInput inputs;
    
    private ArrayList<GameObject> objects;
    private ArrayList<GameObject> groundObjects;
    private ArrayList<GameObject> pipeObjects;
    private PlayerBird bird;

    private ScoreDisplay scoreDisplay;

    public int score;
    
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

        scoreDisplay = new ScoreDisplay();

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
        score = 0;

        // Spawn the player at the center of the screen offset a bit upwards.
        bird = new PlayerBird(0,(windowSize.y * -0.1) / camera.getZoomY());
        addObject(bird);

        // The position of the ground objects is dependent on the camera so we must update the camera after spawning the player.
        updateCamera(camera);

        roofYPosition = camera.getY();
        addGroundObjects();
    }

    public void restartGame(){
        objects = new ArrayList<GameObject>();
        groundObjects = new ArrayList<GameObject>();
        pipeObjects = new ArrayList<GameObject>();

        initialiseGame();
    }

    // Add the ground objects to the bottom of the game window. Add enough ground to cover the whole window.
    private void addGroundObjects(){
        // How many ground objects do we need to fill the screen?
        int groundObjectsNeeded = (int)Math.ceil((windowSize.x / camera.getZoomX()) / Ground.size.x) + 1;

        // Where is the left-hand edge of the window in global co-ordinates?
        double groundXOrigin = camera.getX();
        // Where is the bottom-hand edge of the window in global co-ordinates?
        groundYPosition = (camera.getY() + windowSize.y / camera.getZoomY()) - Ground.size.y;

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
    private void spawnPipesInFrontOfPlayer(ArrayList<GameObject> pipes, int pipeXDistance){

        // Get the global position for the right-hand side of the screen.
        double rightEdgeOfScreen = camera.getX() + windowSize.x / camera.getZoomX();

        // Get the next pipe position by finding the next multiple of the pipeXDistance from rightEdgeOfScreen
        double nextPipePosition = Math.ceil(rightEdgeOfScreen / pipeXDistance) * pipeXDistance;

        // Check if any pipe objects already exist at the next pipe position.
        for (GameObject pipe : pipes){
            if (pipe.x == nextPipePosition){
                return;
            }
        }

        // Get the y position for the vertical center of the game (between the ceiling and the roof)
        double YCenter = (roofYPosition + groundYPosition) * 0.5;

        // Get the y position for the distance from the center of the screen to the roof/floor
        double YOffset = Math.abs(groundYPosition - roofYPosition) - pipeYGap;

        // Reduce it
        YOffset *= pipeYVariation;

        // Set the pipe Y position to the center y plus the offset y multiplied by a random float from -0.5 to 0.5
        double pipeYPosition = YCenter + YOffset * (Math.random() - 0.5);

        // Spawn the top and bottom pipes.
        Pipe pipeTop = new Pipe(nextPipePosition, pipeYPosition - pipeYGap * 0.5, true);
        Pipe pipeBottom = new Pipe(nextPipePosition, pipeYPosition + pipeYGap * 0.5, false);

        pipes.add(pipeBottom);
        addObject(pipeBottom);
        pipes.add(pipeTop);
        addObject(pipeTop);

        // Add a "point" object which gives the player a point for clearing the pipe.
        ScorePoint point = new ScorePoint(nextPipePosition + Pipe.size.x, roofYPosition);
        pipes.add(point);
        addObject(point);
    }

    // Delete pipes which are behind the player to free memory.
    private void deletePipesBehindPlayer(ArrayList<GameObject> pipes){

        ArrayList<GameObject> pipesToRemove = new ArrayList<GameObject>();

        for (GameObject pipe : pipes){
            double cullPosition = camera.getX() - pipe.width * camera.getZoomX();
            if (pipe.x <= cullPosition){
                pipesToRemove.add(pipe);
            }
        }
        for (GameObject pipe : pipesToRemove){
            pipes.remove(pipe);
            removeObject(pipe);
        }
    }
    
    private void addObject(GameObject object){ objects.add(object); } 
    private void removeObject(GameObject object){ objects.remove(object); }

    // Master game loop function. Continues indefinitely
    private void gameLoop(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        while(true){
            this.process(); // Run the game loop.
            this.repaint(); // Draw everything.
            toolkit.sync(); // Force display buffer to flush.

            // Wait a single frame. Source: https://stackoverflow.com/questions/46626715/how-do-i-properly-render-at-a-high-frame-rate-in-pure-java
            try {
                Thread.sleep(frameTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    // Game process function. Called every frame.
    private void process(){
        // How long did it take to render the previous frame?
        previousFrameTime = frameTime; // TODO: Placeholder behaviour sets previous frame time to target frame time, meaning delta will always be 1 regardless of execution speed.
        delta = (previousFrameTime / targetFrameTime); // TODO: Placeholder behaviour sets previous frame time to target frame time, meaning delta will always be 1 regardless of execution speed.

        inputs.process();

        // Make all game objects process.
        for (GameObject object : objects){
            object.process(delta, inputs, objects);
        }

        // If the bird has just collided with a ScorePoint object, increment the score value.
        if (bird.getIsCollidingWithPoint()){ score++; }

        // Constrain the bird's y position within the ceiling and floor
        bird.y = Math.max(bird.y, roofYPosition);
        bird.y = Math.min(bird.y, groundYPosition - bird.height);

        updateCamera(camera);

        // Ensure the ground repeats endlessly horizontally across the screen.
        updateArrayOfBackgroundObjectsToRepeatHorizontally(groundObjects, Ground.size.x);

        
        deletePipesBehindPlayer(pipeObjects);

        switch (bird.state){
            case ALIVE:

                // If player is alive, spawn pipes in front of them.
                spawnPipesInFrontOfPlayer(pipeObjects, pipeXDistance);
                break;

            case DEAD:

                // If the player dies and we click, restart the game.
                if (inputs.getMouseJustPressed()){
                    restartGame();
                }
                break;
            
            case IDLE:
                break;
        }
    }

    public void paint(Graphics g){
        super.paint(g);

        // Sort all game objects by Z index before rendering them.
        Collections.sort(objects, new SortByZIndex());

        // Draw all game objects.
        for (GameObject object : objects){
            object.paint(g, this, camera);
        }

        // Draw the score display.
        if (bird.state != PlayerState.IDLE){
            scoreDisplay.setScore(score);
            scoreDisplay.paint(g, this, camera, windowSize);
        }
    }

    // Class which exists to compare two game objects by their Z indexes.
    private class SortByZIndex implements Comparator<GameObject>{

        // Methods which compares two game objects by their Z indexes.
        public int compare(GameObject a, GameObject b){
            return (int)Math.signum(a.zIndex - b.zIndex);
        }
    }
}