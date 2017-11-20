/*************************************************************** 
*   file: Chunk.java 
*   group: Multi Man Melee
*   class: CS 445 - Computer Graphics
* 
*   assignment: Final Project
*   date last modified: 11/20/17
* 
*   purpose: Holds data for all blocks in a chunk and provides the method 
*       for rendering all the blocks to the screen
* 
****************************************************************/
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    static final int CHUNK_SIZE = 32;
    static final int CUBE_LENGTH = 2;
    
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private Texture texture;
    private int StartX, StartY, StartZ;
    private Random r;
    
    //method: render
    //purpose: renders the chunk to the screen
    public void render(){
        glPushMatrix();       
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER,VBOColorHandle);
        glColorPointer(3,GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D,1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        glDrawArrays(GL_QUADS, 0,CHUNK_SIZE *CHUNK_SIZE*CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    //method: rebuildMesh
    //purpose: reconstructs the mesh for the chunk
    public void rebuildMesh(float startX, float startY, float startZ) {
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        for (float x = 0; x < CHUNK_SIZE; x++) {
            for (float z = 0; z < CHUNK_SIZE; z++) {
                for(float y = 0; y < CHUNK_SIZE; y++){
                    VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH) + CUBE_LENGTH / 2,(float)(y * CUBE_LENGTH) + CUBE_LENGTH / 2,(float) (startZ + z * CUBE_LENGTH) + CUBE_LENGTH));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                    VertexTextureData.put(createTexCube(0f,0f,Blocks[(int)x][(int)y][(int)z]));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexPositionData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexColorData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    //method: createCubeVertexCol
    //purpose: creates an array of colors for each vertex
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    
    //method: createCube
    //purpose: creates a cube at the given x,y,z coords
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
            //TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,            
            //Bottom quad
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,            
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z };
    }
    
    //method: getCubeColor
    //purpose: returns the color of the cube based on block type
    private float[] getCubeColor(Block block) {
        return new float[]{1f,1f,1f};
    }
    
    //method: createTexCube
    //purpose: sets the texture for the cube based on its type
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = 1/16f;
        switch(block.getID()) {          
            case 0: return new float[] {
                //TOP 
                x + offset*3, y + offset*10, 
                x + offset*2, y + offset*10, 
                x + offset*2, y + offset*9,
                x + offset*3, y + offset*9,
                // BOTTOM
                x + offset*3, y + offset*1, 
                x + offset*2, y + offset*1, 
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD 
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1, 
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*4, y + offset*1, 
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                // LEFT QUAD  
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1, 
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1, 
                x + offset*3, y + offset*1};
            case 1: return new float[] { //2,3 1,2
                //TOP 
                x + offset*3, y + offset*2, 
                x + offset*2, y + offset*2, 
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // BOTTOM
                x + offset*3, y + offset*2, 
                x + offset*2, y + offset*2, 
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // FRONT QUAD 
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2, 
                x + offset*2, y + offset*2,
                // BACK QUAD
                x + offset*3, y + offset*2, 
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // LEFT QUAD  
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2, 
                x + offset*2, y + offset*2,
                // RIGHT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2, 
                x + offset*2, y + offset*2};
            case 2: return new float[] {
                //TOP 
                x + offset*15, y + offset*1, 
                x + offset*14, y + offset*1, 
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // BOTTOM
                x + offset*15, y + offset*1, 
                x + offset*14, y + offset*1, 
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // FRONT QUAD 
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                x + offset*15, y + offset*1, 
                x + offset*14, y + offset*1,
                // BACK QUAD
                x + offset*15, y + offset*1, 
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                // LEFT QUAD  
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                x + offset*15, y + offset*1, 
                x + offset*14, y + offset*1,
                // RIGHT QUAD
                x + offset*14, y + offset*0,
                x + offset*15, y + offset*0,
                x + offset*15, y + offset*1, 
                x + offset*14, y + offset*1};
            case 3: return new float[] {
                //TOP 
                x + offset*3, y + offset*1, 
                x + offset*2, y + offset*1, 
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // BOTTOM
                x + offset*3, y + offset*1, 
                x + offset*2, y + offset*1, 
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD 
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1, 
                x + offset*2, y + offset*1,
                // BACK QUAD
                x + offset*3, y + offset*1, 
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // LEFT QUAD  
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1, 
                x + offset*2, y + offset*1,
                // RIGHT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1, 
                x + offset*2, y + offset*1};
            case 4: return new float[] {
                //TOP 
                x + offset*2, y + offset*1, 
                x + offset*1, y + offset*1, 
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // BOTTOM
                x + offset*2, y + offset*1, 
                x + offset*1, y + offset*1, 
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // FRONT QUAD 
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1, 
                x + offset*1, y + offset*1,
                // BACK QUAD
                x + offset*2, y + offset*1, 
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // LEFT QUAD  
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1, 
                x + offset*1, y + offset*1,
                // RIGHT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1, 
                x + offset*1, y + offset*1};
            case 5: return new float[] {
                //TOP 
                x + offset*2, y + offset*2, 
                x + offset*1, y + offset*2, 
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // BOTTOM
                x + offset*2, y + offset*2, 
                x + offset*1, y + offset*2, 
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD 
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2, 
                x + offset*1, y + offset*2,
                // BACK QUAD
                x + offset*2, y + offset*2, 
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // LEFT QUAD  
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2, 
                x + offset*1, y + offset*2,
                // RIGHT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2, 
                x + offset*1, y + offset*2};
            default: return new float[] {
                //TOP 
                x + offset*15, y + offset*2, 
                x + offset*14, y + offset*2, 
                x + offset*14, y + offset*1,
                x + offset*15, y + offset*1,
                // BOTTOM
                x + offset*15, y + offset*2, 
                x + offset*14, y + offset*2, 
                x + offset*14, y + offset*1,
                x + offset*15, y + offset*1,
                // FRONT QUAD 
                x + offset*14, y + offset*1,
                x + offset*15, y + offset*1,
                x + offset*15, y + offset*2, 
                x + offset*14, y + offset*2,
                // BACK QUAD
                x + offset*15, y + offset*2, 
                x + offset*14, y + offset*2,
                x + offset*14, y + offset*1,
                x + offset*15, y + offset*1,
                // LEFT QUAD  
                x + offset*14, y + offset*1,
                x + offset*15, y + offset*1,
                x + offset*15, y + offset*2, 
                x + offset*14, y + offset*2,
                // RIGHT QUAD
                x + offset*14, y + offset*1,
                x + offset*15, y + offset*1,
                x + offset*15, y + offset*2, 
                x + offset*14, y + offset*2};
        }
    } 
    
    public Chunk(int startX, int startY, int startZ) {
        r= new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    if(r.nextFloat()>0.84f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    } else if(r.nextFloat()>0.67f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    } else if(r.nextFloat()>0.51f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                    } else if (r.nextFloat()>0.34f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    } else if (r.nextFloat()>0.17f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                    } else {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    }
                }
            }
        }
        
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("textures.png"));
        } catch(Exception e) {
            System.err.println("Error loading texture file!");
        }
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        
        rebuildMesh(startX, startY, startZ);
    }
}
