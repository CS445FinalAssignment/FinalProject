import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

public class CameraController {

    private Vector3f position;
    private Vector3f IPosition;

    private float yaw;
    private float pitch;

    private Vector3f me;

    public CameraController(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        IPosition = new Vector3f(0, 15f, 0);
        yaw = 0f;
        pitch = 0f;
    }

    public void yaw(float amount) {
        yaw += amount;
    }

    public void pitch(float amount) {
        pitch -= amount;
    }

    public void walkForward(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
    }

    public void walkBackwards(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }

    public void strafeLeft(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    public void strafeRight(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    public void moveUp(float distance) {
        position.y -= distance;
    }

    public void moveDown(float distance) {
        position.y += distance;
    }

    public void lookThrough() {
        glRotatef(pitch, 1.0f, 0f, 0f);
        glRotatef(yaw, 0f, 1f, 0f);
        glTranslatef(position.x, position.y, position.z);
    }

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

        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            time = Sys.getTime();
            lastTime = time;

            dx = Mouse.getDX();
            dy = Mouse.getDY();
            
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
            glEnable(GL_DEPTH_TEST);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            //you would draw your scene here
            render();
            //draw the buffer to the screen
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }
    
    private void render() {
                glColor3f(1.0f, 0.5f, 0f);
                glTranslatef(0, 0, -10);
                glBegin(GL_QUADS);
                    //Top
                    glColor3f(0.863f, 0.078f, 0.235f);
                    glVertex3f( 1.0f, 1.0f,-1.0f);
                    glVertex3f(-1.0f, 1.0f,-1.0f);
                    glVertex3f(-1.0f, 1.0f, 1.0f);
                    glVertex3f( 1.0f, 1.0f, 1.0f);
                    //Bottom
                    glColor3f(1.0f, 0.078f, 0.576f);
                    glVertex3f( 1.0f,-1.0f, 1.0f);
                    glVertex3f(-1.0f,-1.0f, 1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f( 1.0f,-1.0f,-1.0f);
                    //Front
                    glColor3f(1.0f, 0.5f, 0.0f);
                    glVertex3f( 1.0f, 1.0f, 1.0f);
                    glVertex3f(-1.0f, 1.0f, 1.0f);
                    glVertex3f(-1.0f,-1.0f, 1.0f);
                    glVertex3f( 1.0f,-1.0f, 1.0f);
                    //Back
                    glColor3f(0.933f, 0.510f, 0.933f);
                    glVertex3f( 1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f, 1.0f,-1.0f);
                    glVertex3f( 1.0f, 1.0f,-1.0f);
                    //Left
                    glColor3f(0.0f, 1.0f, 0.0f);
                    glVertex3f(-1.0f, 1.0f,1.0f);
                    glVertex3f(-1.0f, 1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f, 1.0f);
                    //Right
                    glColor3f(0.0f, 0.749f, 1.0f);
                    glVertex3f( 1.0f, 1.0f,-1.0f);
                    glVertex3f( 1.0f, 1.0f, 1.0f);
                    glVertex3f( 1.0f,-1.0f, 1.0f);
                    glVertex3f( 1.0f,-1.0f,-1.0f);
                glEnd();
                glBegin(GL_LINE_LOOP);
                    //Top
                    glColor3f(0.0f,0.0f,0.0f);
                    glVertex3f( 1.0f, 1.0f,-1.0f);
                    glVertex3f(-1.0f, 1.0f,-1.0f);
                    glVertex3f(-1.0f, 1.0f, 1.0f);
                    glVertex3f( 1.0f, 1.0f, 1.0f);
                glEnd();
                glBegin(GL_LINE_LOOP);
                    //Bottom
                    glVertex3f( 1.0f,-1.0f, 1.0f);
                    glVertex3f(-1.0f,-1.0f, 1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f( 1.0f,-1.0f,-1.0f);
                glEnd();                   
                glBegin(GL_LINE_LOOP);
                    //Front
                    glVertex3f( 1.0f, 1.0f, 1.0f);
                    glVertex3f(-1.0f, 1.0f, 1.0f);
                    glVertex3f(-1.0f,-1.0f, 1.0f);
                    glVertex3f( 1.0f,-1.0f, 1.0f);
                glEnd();
                glBegin(GL_LINE_LOOP);
                    //Back
                    glVertex3f( 1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f, 1.0f,-1.0f);
                    glVertex3f( 1.0f, 1.0f,-1.0f);
                glEnd();
                glBegin(GL_LINE_LOOP);
                    //Left
                    glVertex3f(-1.0f, 1.0f, 1.0f);
                    glVertex3f(-1.0f, 1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f, 1.0f);
                glEnd();
                glBegin(GL_LINE_LOOP);
                    //Right
                    glVertex3f( 1.0f, 1.0f,-1.0f);
                    glVertex3f( 1.0f, 1.0f, 1.0f);
                    glVertex3f( 1.0f,-1.0f, 1.0f);
                    glVertex3f( 1.0f,-1.0f,-1.0f);
                glEnd();
    }
}
