/** *************************************************************
 *   file: Chunk.java
 *   group: Multi Man Melee
 *   class: CS 445 - Computer Graphics
 *
 *   assignment: Final Project
 *   date last modified: 11/25/17
 *
 *   purpose: Holds data for all blocks in a chunk and provides the method
 *       for rendering all the blocks to the screen
 *
 *************************************************************** */
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
    static final double PERSISTANCE = 0.2;

    static final int SEED = new Random().nextInt();

    private Block[][][] Blocks;
    private double[][] heightMap;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private Texture texture;
    private int StartX, StartY, StartZ;
    private Random r;

    //method: render
    //purpose: renders the chunk to the screen
    public void render() {
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }

    //method: rebuildMesh
    //purpose: reconstructs the mesh for the chunk
    public void rebuildMesh(float startX, float startY, float startZ) {
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);

        for (float x = 0; x < CHUNK_SIZE; x++) {
            for (float z = 0; z < CHUNK_SIZE; z++) {
                for (float y = 0; y < CHUNK_SIZE; y++) {
//                for (float y = 0; y < heightMap[(int) x][(int) z]; y++) {     // use chunk size instead
                    if (Blocks[(int) x][(int) y][(int) z] == null) {
                        continue;
                    }
                    VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH) + CUBE_LENGTH / 2, (float) (y * CUBE_LENGTH) + CUBE_LENGTH / 2, (float) (startZ + z * CUBE_LENGTH) + CUBE_LENGTH));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                    VertexTextureData.put(createTexCube(0f, 0f, Blocks[(int) x][(int) y][(int) z]));

                }
            }
        }

        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
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
        return new float[]{
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
            x + offset, y - offset, z
        };
    }

    //method: getCubeColor
    //purpose: returns the color of the cube based on block type
    private float[] getCubeColor(Block block) {
        return new float[]{1f, 1f, 1f};
    }

    //method: createTexCube
    //purpose: sets the texture for the cube based on its type
    public static float[] createTexCube(float x, float y, Block block) {
//        float offset = 1 / 16f;
        float offset = (1024f / 16) / 1024f;
        switch (block.getID()) {
            case 0: // Grass
                return new float[]{
                    //TOP 
                    x + offset * 3, y + offset * 10,
                    x + offset * 2, y + offset * 10,
                    x + offset * 2, y + offset * 9,
                    x + offset * 3, y + offset * 9,
                    // BOTTOM
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // FRONT QUAD 
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // BACK QUAD
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    // LEFT QUAD  
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1};
            case 1: // Sand
                return new float[]{ //2,3 1,2
                    //TOP 
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // BOTTOM
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // FRONT QUAD 
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // BACK QUAD
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // LEFT QUAD  
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // RIGHT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2};
            case 2: // Water
                return new float[]{
                    //TOP 
                    x + offset * 15, y + offset * 1,
                    x + offset * 14, y + offset * 1,
                    x + offset * 14, y + offset * 0,
                    x + offset * 15, y + offset * 0,
                    // BOTTOM
                    x + offset * 15, y + offset * 1,
                    x + offset * 14, y + offset * 1,
                    x + offset * 14, y + offset * 0,
                    x + offset * 15, y + offset * 0,
                    // FRONT QUAD 
                    x + offset * 14, y + offset * 0,
                    x + offset * 15, y + offset * 0,
                    x + offset * 15, y + offset * 1,
                    x + offset * 14, y + offset * 1,
                    // BACK QUAD
                    x + offset * 15, y + offset * 1,
                    x + offset * 14, y + offset * 1,
                    x + offset * 14, y + offset * 0,
                    x + offset * 15, y + offset * 0,
                    // LEFT QUAD  
                    x + offset * 14, y + offset * 0,
                    x + offset * 15, y + offset * 0,
                    x + offset * 15, y + offset * 1,
                    x + offset * 14, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 14, y + offset * 0,
                    x + offset * 15, y + offset * 0,
                    x + offset * 15, y + offset * 1,
                    x + offset * 14, y + offset * 1};
            case 3: // Dirt
                return new float[]{
                    //TOP 
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // BOTTOM
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // FRONT QUAD 
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // BACK QUAD
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // LEFT QUAD  
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1};
            case 4: // Stone
                return new float[]{
                    //TOP 
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // BOTTOM
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // FRONT QUAD 
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // BACK QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // LEFT QUAD  
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1};
            case 5: // Bedrock
                return new float[]{
                    //TOP 
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // BOTTOM
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // FRONT QUAD 
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // BACK QUAD
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // LEFT QUAD  
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // RIGHT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2};
            default:
                return new float[]{
                    //TOP 
                    x + offset * 15, y + offset * 2,
                    x + offset * 14, y + offset * 2,
                    x + offset * 14, y + offset * 1,
                    x + offset * 15, y + offset * 1,
                    // BOTTOM
                    x + offset * 15, y + offset * 2,
                    x + offset * 14, y + offset * 2,
                    x + offset * 14, y + offset * 1,
                    x + offset * 15, y + offset * 1,
                    // FRONT QUAD 
                    x + offset * 14, y + offset * 1,
                    x + offset * 15, y + offset * 1,
                    x + offset * 15, y + offset * 2,
                    x + offset * 14, y + offset * 2,
                    // BACK QUAD
                    x + offset * 15, y + offset * 2,
                    x + offset * 14, y + offset * 2,
                    x + offset * 14, y + offset * 1,
                    x + offset * 15, y + offset * 1,
                    // LEFT QUAD  
                    x + offset * 14, y + offset * 1,
                    x + offset * 15, y + offset * 1,
                    x + offset * 15, y + offset * 2,
                    x + offset * 14, y + offset * 2,
                    // RIGHT QUAD
                    x + offset * 14, y + offset * 1,
                    x + offset * 15, y + offset * 1,
                    x + offset * 15, y + offset * 2,
                    x + offset * 14, y + offset * 2};
        }
    }

    //method: generateHeightmap
    //purpose: generates a 2d array representing a height map using simplex noise
    private void generateHeightmap() {
        SimplexNoise noise = new SimplexNoise(CHUNK_SIZE, PERSISTANCE, SEED);
        heightMap = new double[CHUNK_SIZE][CHUNK_SIZE];

        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_SIZE; j++) {
                for (int k = 0; k < CHUNK_SIZE; k++) {
                    heightMap[i][j] = (noise.getNoise(i, j, k) + 1) * 8;
                }
            }
        }
    }

    // Constructor
    public Chunk(int startX, int startY, int startZ) {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("textures.png"));
        } catch (Exception e) {
            System.err.println("Error loading texture file!");
        }

        r = new Random();
        generateHeightmap();

        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        int fillLevel = CHUNK_SIZE;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int height = (int) heightMap[x][z];
                if (height < fillLevel) {
                    fillLevel = height;
                }
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    if (y > heightMap[x][z]) {
                        Blocks[x][y][z] = null;
                    } else if (y == 0) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    } else if (y < CHUNK_SIZE / 6) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    } else if (y == height) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    } else {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }
                }
            }
        }

        //Turn edges of where water is going to be into sand
        fillLevel += 1;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                if (Blocks[x][fillLevel][z] == null) {
                    continue;
                }
                if ((x + 1 < CHUNK_SIZE && Blocks[x + 1][fillLevel][z] == null) || (x - 1 >= 0 && Blocks[x - 1][fillLevel][z] == null)
                        || (z + 1 < CHUNK_SIZE && Blocks[x][fillLevel][z + 1] == null) || (z - 1 >= 0 && Blocks[x][fillLevel][z - 1] == null)) {
                    Blocks[x][fillLevel][z] = new Block(Block.BlockType.BlockType_Sand);
                }
            }
        }

        //fill water in, set sand below the water if below the water isnt the bottom level
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                if (Blocks[x][fillLevel][z] == null) {
                    Blocks[x][fillLevel][z] = new Block(Block.BlockType.BlockType_Water);
                    if (fillLevel - 1 >= 1) {
                        Blocks[x][fillLevel - 1][z] = new Block(Block.BlockType.BlockType_Sand);
                    }
                }
            }
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
