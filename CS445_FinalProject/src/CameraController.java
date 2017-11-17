/*************************************************************** 
*   file: CameraController.java 
*   group: Multi Man Melee
*   class: CS 445 - Computer Graphics
* 
*   assignment: Final Project
*   date last modified: 11/15/17
* 
*   purpose: This class represents a camera that will be used to render a scene
*       and it also contains the main game loop for the program
* 
****************************************************************/ 

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

public class CameraController {

    private Vector3f position;
    private Vector3f IPosition;

    private float yaw;
    private float pitch;

    private Vector3f me;

    //private Chunk[][] chunks;
    private Chunk chunks;
    
    public CameraController(float x, float y, float z/*, int width, int length*/) {
        position = new Vector3f(x, y, z);
        IPosition = new Vector3f(0, 15f, 0);
        yaw = 0f;
        pitch = 0f;
        //chunks = new Chunk[width][length];
        //createChunks();
    }

    /*
    private void createChunks() {
        int length = Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH;
        for(int i = 0; i < chunks.length; i++) {
            for(int j = 0; j < chunks[0].length; j++) {
                chunks[j][i] = new Chunk(length * j,0,length * i);
            }
        }
    }
    */
    
    //method: yaw
    //purpose: adjusts the yaw
    public void yaw(float amount) {
        yaw += amount;
    }

    //method: pitch
    //purpose: adjusts the pitch
    public void pitch(float amount) {
        pitch -= amount;
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
        glRotatef(pitch, 1.0f, 0f, 0f);
        glRotatef(yaw, 0f, 1f, 0f);
        glTranslatef(position.x, position.y, position.z);
    }

    //method: gameLoop
    //purpose: main loop for the game, checks for input, and calls render function
    public void gameLoop() {
        
        //create an chunk object here
        chunks = new Chunk(0, 0, 0);
        //
        
        float dx = 0;
        float dy = 0;
        float dt = 0;
        float lastTime = 0;
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = 0.35f;
        Mouse.setGrabbed(true);

        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            time = Sys.getTime();
            lastTime = time;

            //get change in mouse position
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            
            //update yaw and pitch based on change in mouse position
            yaw(dx * mouseSensitivity);
            pitch(dy * mouseSensitivity);
            
            //reset camera orientation and position
            if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
                yaw(-yaw);
                pitch(pitch);
                position.set(0,0,0);
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

            //set the modelview matrix back to identity
            glLoadIdentity();
            //look through the camera before you draw anything
            lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);           
            
            //Render each chunk
            /*
            for (Chunk[] chunkArray : chunks) {
                for (Chunk chunk : chunkArray) {
                    chunk.render();
                }
            }
            */
            
            //call render through chunk object
            chunks.render();
            //draw the buffer to the screen
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }
    
    //method: renderBoundaries
    //purpose: renders an outline of each chunks boundaries
    /*
    private void renderBoundaries() {       
        glColor3f(1,0,0);
        float length = Chunk.CUBE_LENGTH * Chunk.CHUNK_SIZE;
        for(int i = 0; i < chunks.length; i++) {
            for(int j = 0; j < chunks[0].length; j++) {
                //top
                glBegin(GL_LINE_LOOP);
                    glVertex3f(length * j, length, length * i);
                    glVertex3f(length * j, length, length * i + length);
                    glVertex3f(length * j + length, length, length * i + length);
                    glVertex3f(length * j + length, length, length * i);
                glEnd();
                //bottom
                glBegin(GL_LINE_LOOP);
                    glVertex3f(length * j, 0, length * i);
                    glVertex3f(length * j, 0, length * i + length);
                    glVertex3f(length * j + 0, length, length * i + length);
                    glVertex3f(length * j + 0, length, length * i);
                glEnd();
                //verticals
                glBegin(GL_LINES);
                    glVertex3f(length * j, length, length * i);
                    glVertex3f(length * j, 0, length * i);
                    glVertex3f(length * j, length, length * i + length);
                    glVertex3f(length * j, 0, length * i + length);
                    glVertex3f(length * j + length, length, length * i + length);
                    glVertex3f(length * j + 0, length, length * i + length);
                    glVertex3f(length * j + length, length, length * i);
                    glVertex3f(length * j + 0, length, length * i);
                glEnd();
            }
        }
    }
    */
}
