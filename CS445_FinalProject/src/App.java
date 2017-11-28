/*************************************************************** 
*   file: App.java 
*   group: Multi Man Melee
*   class: CS 445 - Computer Graphics
* 
*   assignment: Final Project
*   date last modified: 11/27/17
* 
*   purpose: The main class for this program. Initializes the window 
*       and starts the game loop
* 
****************************************************************/ 

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class App {
    private CameraController cc;
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    
    //method: start
    //purpose: calls the initialization functions for reading data/setting up the window, 
    //  then inits the render process
    public void start() throws Exception {
        initWindow();
        initGL();
        cc = new CameraController();
        cc.yaw(180);
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
        glClearColor(0.529f, 0.808f, 0.922f, 1f);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300f);

        // Lighting
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);     // sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);        // sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);         // sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);         // sets our ambient light
        glEnable(GL_LIGHTING);                              // enables our lighting
        glEnable(GL_LIGHT0);                                // enables light
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);  
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }

    //method: initLightArrays
    //purpose: initializes the light arrays
    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }
    
    
    //method: main
    //purpose: entry point for the program, launches the app
    public static void main(String[] args) throws Exception {
        App app = new App();
        app.start();
    }
}
