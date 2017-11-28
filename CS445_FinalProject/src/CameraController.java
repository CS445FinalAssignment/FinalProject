/***************************************************************
 *   file: CameraController.java
 *   group: Multi Man Melee
 *   class: CS 445 - Computer Graphics
 *
 *   assignment: Final Project
 *   date last modified: 11/28/17
 *
 *   purpose: This class represents a camera that will be used to render a scene
 *       and it also contains the main game loop for the program
 *
 *************************************************************** */
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class CameraController {

    private final Vector3f position;
    private final Vector3f resetPosition;
    private final Vector3f IPosition;
    private float yaw;
    private float pitch;
    private boolean lighting;
    private final Chunk[][] chunks;
    private final int size = 4;
    
    public CameraController() {
        position = new Vector3f(0f, -30f, 0f);
        resetPosition = new Vector3f(0f, -30f, 0f);
        IPosition = new Vector3f(0f, 15f, 0f);
        yaw = 180f;
        pitch = 0f;    
        lighting = true;
        chunks = new Chunk[size][size];
        int half = size/2;
        int limit = size % 2 == 0 ? half : half + 1;
        for(int x = -half; x < limit; x++) {
            for (int z = -half; z < limit; z++) {
                chunks[x + half][z + half] = new Chunk(x * Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH, -100, z * Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH);
            }
        }
    }

    //method: yaw
    //purpose: adjusts the yaw
    public void yaw(float amount) {
        yaw += amount;
    }

    //method: pitch
    //purpose: decrement the pitch by the amount param and keep it from allowing the camera to turn upside down
    public void pitch(float amount) {
        pitch -= amount;
        if (pitch < -90) {
            pitch = -90;
        }
        if (pitch > 90) {
            pitch = 90;
        }
    }

    //method: walkForward
    //purpose: moves the camera forward a set distance
    public void walkForward(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
    }

    //method: walkBackwards
    //purpose: moves the camera backwards a set distance
    public void walkBackwards(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }

    //method: strafeLeft
    //purpose: strafes the camera left a set distance
    public void strafeLeft(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    //method: strafeRight
    //purpose: strafes the camera right a set distance
    public void strafeRight(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    //method: moveUp
    //purpose: moves the camera up a set distance
    public void moveUp(float distance) {
        position.y -= distance;
    }

    //method: moveDown
    //purpose: moves the camera down a set distance
    public void moveDown(float distance) {
        position.y += distance;
    }

    //method: lookThrough
    //purpose: translates and rotates the matrix so it looks through the camera
    public void lookThrough() {
        glRotatef(pitch, 1.0f, 0f, 0f);                     // rotate the pitch around the X axis
        glRotatef(yaw, 0f, 1f, 0f);                         // rotate the yaw around the Y axis
        glTranslatef(position.x, position.y, position.z);   // translate to the position vector's location

        // Lighting Position
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(IPosition.x).put(IPosition.y).put(IPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    //method: gameLoop
    //purpose: main loop for the game, checks for input, and calls render function
    public void gameLoop() {
        float dx;
        float dy;
        float mouseSensitivity = 0.09f;
        float movementSpeed = 0.35f;
        Mouse.setGrabbed(true);
        
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            //get change in mouse position
            dx = Mouse.getDX();
            dy = Mouse.getDY();

            //update yaw and pitch based on change in mouse position
            yaw(dx * mouseSensitivity);
            pitch(dy * mouseSensitivity);

            //reset camera orientation and position using the R key
            if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
                yaw(-yaw);
                pitch(pitch);
                position.set(resetPosition.x, resetPosition.y, resetPosition.z);
            }

            //move forward
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                walkForward(movementSpeed);
            }
            //move backwards
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                walkBackwards(movementSpeed);
            }

            //strafe left
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                strafeLeft(movementSpeed);
            }
            //strafe right
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                strafeRight(movementSpeed);
            }
            //move up
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                moveUp(movementSpeed);
            }
            //move down
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                moveDown(movementSpeed);
            }
            
            //toggle Lighting using the L key
            if(Keyboard.isKeyDown(Keyboard.KEY_L)) {
                lighting = !lighting;
                if(lighting) {
                    glEnable(GL_LIGHTING);
                } else {
                    glDisable(GL_LIGHTING);
                }
            }
            
            //set the modelview matrix back to identity
            glLoadIdentity();
            //look through the camera before you draw anything
            lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    chunks[i][j].render();
                }
            }        
            
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }
}
