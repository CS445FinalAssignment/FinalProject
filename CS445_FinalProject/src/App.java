/*************************************************************** 
*   file: App.java 
*   group: Multi Man Melee
*   class: CS 445 - Computer Graphics
* 
*   assignment: Final Project
*   date last modified: 11/20/17
* 
*   purpose: The main class for this program. Initializes the window 
*       and starts the game loop
* 
****************************************************************/ 

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class App {
    private CameraController cc;
    private DisplayMode displayMode;
    
    //method: start
    //purpose: calls the initialization functions for reading data/setting up the window, 
    //  then inits the render process
    public void start() throws Exception {
        initWindow();
        initGL();
        cc = new CameraController(0,0,0);
        cc.gameLoop();
    }

    //method: initWindow
    //purpose: Initializes the window with the correct size and title
    private void initWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if(d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("CS445 - Final Assignment");
        Display.create();
    }

    // method: initGL
    // purpose: initilize the gl settings for the program
    private void initGL() {
        glClearColor(0f, 0f, 0f, 0f);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300f);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);  
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }

    //method: main
    //purpose: entry point for the program, launches the app
    public static void main(String[] args) throws Exception {
        App app = new App();
        app.start();
    }
}
