/***************************************************************
 *   file: CameraController.java
 *   group: Multi Man Melee
 *   class: CS 445 - Computer Graphics
 *
 *   assignment: Final Project
 *   date last modified: 11/20/17
 *
 *   purpose: This class represents a camera that will be used to render a scene
 *       and it also contains the main game loop for the program
 *
 *************************************************************** */
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class CameraController {

    private Vector3f position;
    private Vector3f IPosition;
    private float yaw;
    private float pitch;
    private Chunk chunk;

    public CameraController(float x, float y, float z) {
//        position = new Vector3f(x, y, z);
        position = new Vector3f(-30f, -50f, 0f);
        chunk = new Chunk(0, -100, -50);        // starting location of chunk relative to camera's initial position
        IPosition = new Vector3f(0, 15f, 0);
        yaw = 0f;
        pitch = 0f;
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

        /**
         * These 3 lines of code move the lighting as you move the camera. Not
         * sure if that is what he wants. *
         */
//        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
//        lightPosition.put(IPosition.x-=xOffset).put(IPosition.y).put(IPosition.z+=zOffset).put(1.0f).flip();
//        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    //method: walkBackwards
    //purpose: moves the camera backwards a set distance
    public void walkBackwards(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
//        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
//        lightPosition.put(IPosition.x-=xOffset).put(IPosition.y).put(IPosition.z+=zOffset).put(1.0f).flip();
//        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    //method: strafeLeft
    //purpose: strafes the camera left a set distance
    public void strafeLeft(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset;
//        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
//        lightPosition.put(IPosition.x-=xOffset).put(IPosition.y).put(IPosition.z+=zOffset).put(1.0f).flip();
//        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    //method: strafeRight
    //purpose: strafes the camera right a set distance
    public void strafeRight(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;
//        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
//        lightPosition.put(IPosition.x-=xOffset).put(IPosition.y).put(IPosition.z+=zOffset).put(1.0f).flip();
//        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
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
        lightPosition.put(IPosition.x).put(
                IPosition.y).put(IPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    //method: gameLoop
    //purpose: main loop for the game, checks for input, and calls render function
    public void gameLoop() {
        CameraController camera = new CameraController(0, 0, 0);
        float dx = 0;
        float dy = 0;
        float dt = 0;
        float lastTime = 0;
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = 0.35f;
        Mouse.setGrabbed(true);

//        chunk = new Chunk(0, 0, 0);       // this kept resetting the lighting
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            time = Sys.getTime();
            lastTime = time;

            //get change in mouse position
            dx = Mouse.getDX();
            dy = Mouse.getDY();

            //update yaw and pitch based on change in mouse position
            camera.yaw(dx * mouseSensitivity);
            camera.pitch(dy * mouseSensitivity);

            //reset camera orientation and position
            if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
                yaw(-yaw);
                pitch(pitch);
                position.set(0, 0, 0);
            }

            //move forward
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                camera.walkForward(movementSpeed);
            }
            //move backwards
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                camera.walkBackwards(movementSpeed);
            }

            //strafe left
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                camera.strafeLeft(movementSpeed);
            }
            //strafe right
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                camera.strafeRight(movementSpeed);
            }
            //move up
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                camera.moveUp(movementSpeed);
            }
            //move down
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                camera.moveDown(movementSpeed);
            }

            //set the modelview matrix back to identity
            glLoadIdentity();
            //look through the camera before you draw anything
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            chunk.render();

            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }
}
