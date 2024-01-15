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

    private long previousFrameTime;                                   // How many milliseconds did the previous frame take?
    private long delta;                                               // previousFrameTime / targetFrameTime
    private JFrame window;
    private Camera camera;
    private GameInput inputs;
    
    private ArrayList<GameObject> objects;
    private ArrayList<Ground> groundObjects;
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
        groundObjects = new ArrayList<Ground>();

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
        // Spawn the bird at the top of the screen.
        bird = new PlayerBird(0,(windowSize.y * -0.2) / camera.getZoomY());

        addObject(bird);

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

    // Move the ground objects so that they always are in the camera.
    private void updateGroundObjects(){

        for (Ground ground : groundObjects){

        }
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
    }

    public void paint(Graphics g){
        super.paint(g);

        // Draw all game objects.
        for (GameObject object : objects){
            object.paint(g, this, camera);
        }
    }
}