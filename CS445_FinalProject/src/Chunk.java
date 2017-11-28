
/** *************************************************************
 *   file: Chunk.java
 *   group: Multi Man Melee
 *   class: CS 445 - Computer Graphics
 *
 *   assignment: Final Project
 *   date last modified: 11/27/17
 *
 *   purpose: Holds data for all blocks in a chunk and provides the method
 *      for rendering all the blocks to the screen
 *      Added functionality to terrain gen: trees/pumpkins are place randomly on surface, ores(iron,gold,emerald) are placed randomly in stone area
 *      A custom texture is used called textures.png, rather then the given texture file
 *************************************************************** */
import java.io.IOException;
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
    static final double PERSISTANCE = 0.3;

    static final int SEED = new Random().nextInt();

    private Block[][][] blocks;
    private double[][] heightMap;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private Texture texture;
    private int StartX, StartY, StartZ;

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
    private void rebuildMesh(float startX, float startY, float startZ) {
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 8);

        for (float x = 0; x < CHUNK_SIZE; x++) {
            for (float z = 0; z < CHUNK_SIZE; z++) {
                for (float y = 0; y < CHUNK_SIZE; y++) {
                    if (blocks[(int) x][(int) y][(int) z] == null) {
                        continue;
                    }
                    VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH) + CUBE_LENGTH / 2, (float) (y * CUBE_LENGTH) + CUBE_LENGTH / 2, (float) (startZ + z * CUBE_LENGTH) + CUBE_LENGTH));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(blocks[(int) x][(int) y][(int) z])));
                    VertexTextureData.put(createTexCube(blocks[(int) x][(int) y][(int) z]));
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
    public static float[] createTexCube(Block block) {
        switch (block.getID()) {
            case 0:
                return BlockTexture.GRASS;
            case 1:
                return BlockTexture.SAND;
            case 2:
                return BlockTexture.WATER;
            case 3:
                return BlockTexture.DIRT;
            case 4:
                return BlockTexture.STONE;
            case 5:
                return BlockTexture.BEDROCK;
            case 6:
                return BlockTexture.LOG;
            case 7:
                return BlockTexture.LEAVES;
            case 8:
                return BlockTexture.IRONORE;
            case 9:
                return BlockTexture.EMERALDORE;
            case 10:
                return BlockTexture.GOLDORE;
            case 11:
                return BlockTexture.PUMPKIN;
            default:
                return BlockTexture.DEFAULT;
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
                    heightMap[i][j] = (noise.getNoise(StartX + i, StartY + j, StartZ + k) + 1) * 8;
                }
            }
        }
    }

    //method: toString
    //purpose: returns string representation of this object
    @Override
    public String toString() {
        return "Chunk(" + StartX + "," + StartY + "," + StartZ + ")";
    }

    // Constructor, block generation occurs here
    public Chunk(int startX, int startY, int startZ) {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("textures.png"));
        } catch (IOException ioe) {
            System.err.println("Error loading texture file: " + ioe.getMessage());
        }

        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        
        StartX = startX;
        StartY = startY;
        StartZ = startZ;

        Random r = new Random();

        generateHeightmap();

        //First pass through of generation
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        int fillLevel = CHUNK_SIZE;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int height = (int) heightMap[x][z];
                if (height < fillLevel) { //update min height(fill level)
                    fillLevel = height;
                }
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    if (y > heightMap[x][z]) {
                        blocks[x][y][z] = null;
                    } else if (y == 0) { //bottom level
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    } else if (y < CHUNK_SIZE / 6) { //bottom 1/6 of chunk, stone with chance for ore
                        int value = r.nextInt(101);
                        if (value > 97) {
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_GoldOre);
                        } else if (value > 92) {
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_IronOre);
                        } else if (value > 90) {
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_EmeraldOre);
                        } else {
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                        }
                    } else if (y == height) { //top level
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    } else { //lower than top, higher than stone level
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }
                }
            }
        }
        
        fillLevel += 1;
        
        //ensure all edges of the chunk are blocks so that water placement doesnt look weird between chunks
        for (int x = 0; x < CHUNK_SIZE; x++) {
            blocks[x][fillLevel][0] = blocks[x][fillLevel + 1][0] == null ? new Block(Block.BlockType.BlockType_Grass) : new Block(Block.BlockType.BlockType_Dirt);
            blocks[x][fillLevel][CHUNK_SIZE - 1] = blocks[x][fillLevel + 1][CHUNK_SIZE - 1] == null ? new Block(Block.BlockType.BlockType_Grass) : new Block(Block.BlockType.BlockType_Dirt);
        }
        for (int z = 0; z < CHUNK_SIZE; z++) {
            blocks[0][fillLevel][z] = blocks[0][fillLevel + 1][z] == null ? new Block(Block.BlockType.BlockType_Grass) : new Block(Block.BlockType.BlockType_Dirt);
            blocks[CHUNK_SIZE - 1][fillLevel][z] = blocks[CHUNK_SIZE - 1][fillLevel + 1][z] == null ? new Block(Block.BlockType.BlockType_Grass) : new Block(Block.BlockType.BlockType_Dirt);
        }

        //Turn edges of where water is going to be into sand      
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                if (blocks[x][fillLevel][z] == null) {
                    continue;
                }
                if ((x + 1 < CHUNK_SIZE && blocks[x + 1][fillLevel][z] == null) || (x - 1 >= 0 && blocks[x - 1][fillLevel][z] == null)
                        || (z + 1 < CHUNK_SIZE && blocks[x][fillLevel][z + 1] == null) || (z - 1 >= 0 && blocks[x][fillLevel][z - 1] == null)) {
                    blocks[x][fillLevel][z] = new Block(Block.BlockType.BlockType_Sand);
                }
            }
        }

        //fill water in, set sand below the water if below the water isnt the bottom level
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                if (blocks[x][fillLevel][z] == null) {
                    blocks[x][fillLevel][z] = new Block(Block.BlockType.BlockType_Water);
                    if (fillLevel - 1 >= 1) {
                        blocks[x][fillLevel - 1][z] = new Block(Block.BlockType.BlockType_Sand);
                    }
                }
            }
        }

        //place trees/pumpkins at random
        for (int x = 2; x < CHUNK_SIZE - 2; x++) {
            for (int z = 2; z < CHUNK_SIZE - 2; z++) {
                int value = r.nextInt(101);
                int height = (int) heightMap[x][z];
                if (value > 99) { //place tree if value is > 99                 
                    if (blocks[x][height][z].getID() == Block.BlockType.BlockType_Grass.getID()) {
                        for (int i = 0; i < 5; i++) {
                            blocks[x][height + 1 + i][z] = new Block(Block.BlockType.BlockType_Log);
                        }
                        for (int i = -2; i < 3; i++) {
                            for (int j = -2; j < 3; j++) {
                                if (i == 0 && j == 0) {
                                    continue;
                                }
                                blocks[x + i][height + 4][z + j] = new Block(Block.BlockType.BlockType_Leaves);
                                blocks[x + i][height + 5][z + j] = new Block(Block.BlockType.BlockType_Leaves);
                            }
                        }

                        blocks[x + 1][height + 6][z] = new Block(Block.BlockType.BlockType_Leaves);
                        blocks[x][height + 6][z + 1] = new Block(Block.BlockType.BlockType_Leaves);
                        blocks[x][height + 6][z] = new Block(Block.BlockType.BlockType_Leaves);
                        blocks[x - 1][height + 6][z] = new Block(Block.BlockType.BlockType_Leaves);
                        blocks[x][height + 6][z - 1] = new Block(Block.BlockType.BlockType_Leaves);

                    }
                }
                if (value < 1) { //place pumpkin if value < 1
                    if (blocks[x][height][z].getID() == Block.BlockType.BlockType_Grass.getID()) {
                        blocks[x][height + 1][z] = new Block(Block.BlockType.BlockType_Pumpkin);
                    }
                }
            }
        }

        rebuildMesh(startX, startY, startZ);
    }
}
