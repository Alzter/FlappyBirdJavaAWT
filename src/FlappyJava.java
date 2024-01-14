import javax.swing.*;         // Window (JFrame) class
import java.awt.*;            // Drawing and graphics classes

import java.awt.event.*;      // Input handling classes

import java.io.File;          // Input/Output handling classes
import java.io.IOException;   // Input/Output handling classes
import javax.imageio.ImageIO; // Image loader class
import java.util.ArrayList;   // Flexible size arrays

public class FlappyJava extends Canvas {

    private static final Point windowSize = new Point(400,400);
    private static final int fps = 60;
    private static final long frameTime = (1000)/fps; // How many milliseconds should it take for a frame to elapse?
    private long previousFrameTime; // How many ms did the previous frame take to render?
    private long delta;
    private JFrame window;
    private Camera camera;
    private GameInput inputs;

    private static final float cameraPlayerHorizontalPosition = 0.25f;
    // How much percent of the game window should be dedicated to showing what is ahead of the player?
    // 0   = Player is on the right-hand side of the screen
    // 0.5 = Player is in the middle of the screen
    // 1   = Player is on the left-hand side of the screen
    
    private ArrayList<GameObject> objects;
    private PlayerBird bird;
    
    // CONSTRUCTOR
    public FlappyJava(String windowName, int width, int height){
        
        inputs = new GameInput();
        camera = new Camera(0,0,2,2);

        setSize(width,height);

        window = new JFrame(windowName);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Make execution stop when window is closed.
        window.add(this);
        window.pack();
        window.setVisible(true);

        objects = new ArrayList<GameObject>();

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

        // Sppawn the bird at the vertical center of the screen.
        frame.bird = new PlayerBird(0d,windowSize.y * 0.5 / frame.camera.zoom.y);
        frame.addObject(frame.bird);
        
        frame.gameLoop();
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
        delta = (frameTime / frameTime); // TODO: Placeholder behaviour sets previous frame time to target frame time, meaning delta will always be 1 regardless of execution speed.

        inputs.process();

        // Make all game objects process.
        for (GameObject object : objects){
            object.process(delta, inputs);
        }

        // Ensure the bird is at the horizontal center of the window
        camera.x = (bird.x * camera.zoom.x - windowSize.x * cameraPlayerHorizontalPosition) / camera.zoom.x;
    }

    public void paint(Graphics g){
        super.paint(g);

        // Draw all game objects.
        for (GameObject object : objects){
            object.paint(g, this, camera);
        }
    }
}