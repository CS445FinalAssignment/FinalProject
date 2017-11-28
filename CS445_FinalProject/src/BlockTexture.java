
/** *************************************************************
 *   file: BlockTexture.java
 *   group: Multi Man Melee
 *   class: CS 445 - Computer Graphics
 *
 *   assignment: Final Project
 *   date last modified: 11/27/17
 *
 *   purpose: Holds the data for each blocks texture(texture coords)
 *
 *************************************************************** */

public class BlockTexture {

    private static final float OFFSET = 1/8f;
    private static final float PIXEL = (1/512f) / 2;
    
    public static final float[] GRASS = generateTexture(2,0,0,0,1,0,1,0,1,0,1,0);
    public static final float[] SAND = generateTexture(3,0,3,0,3,0,3,0,3,0,3,0);
    public static final float[] WATER = generateTexture(4,0,4,0,4,0,4,0,4,0,4,0);
    public static final float[] DIRT = generateTexture(0,0,0,0,0,0,0,0,0,0,0,0);
    public static final float[] STONE = generateTexture(0,1,0,1,0,1,0,1,0,1,0,1);
    public static final float[] BEDROCK = generateTexture(1,1,1,1,1,1,1,1,1,1,1,1);
    //Below are extra blocks added
    public static final float[] LOG = generateTexture(6,0,6,0,5,0,5,0,5,0,5,0);
    public static final float[] LEAVES = generateTexture(7,0,7,0,7,0,7,0,7,0,7,0);
    public static final float[] IRONORE = generateTexture(2,1,2,1,2,1,2,1,2,1,2,1);
    public static final float[] EMERALDORE = generateTexture(3,1,3,1,3,1,3,1,3,1,3,1);
    public static final float[] GOLDORE = generateTexture(4,1,4,1,4,1,4,1,4,1,4,1);
    public static final float[] PUMPKIN = generateTexture(6,1,6,1,7,1,5,1,5,1,5,1);
    public static final float[] DEFAULT = generateTexture(4,4,4,4,4,4,4,4,4,4,4,4);
    
    //method: generateTexture
    //purpose: returns a float[] of texture coords for a block, parameteres are the x,y coords of each sides texture in the texture map, 
    //          the pixel offset is to try and remove textures not being read exactly causing overlap on edges
    private static float[] generateTexture(int topX, int topY, int bottomX, int bottomY, int frontX, int frontY, int backX, int backY, int leftX, int leftY, int rightX, int rightY) {      
        return new float[] {
        //TOP 
        OFFSET * (topX + 1) - PIXEL, OFFSET * (topY + 1) - PIXEL,
        OFFSET * topX + PIXEL, OFFSET * (topY + 1) - PIXEL,
        OFFSET * topX + PIXEL, OFFSET * topY + PIXEL,
        OFFSET * (topX + 1) - PIXEL, OFFSET * topY + PIXEL,
        // BOTTOM
        OFFSET * (bottomX + 1) - PIXEL, OFFSET * (bottomY + 1) - PIXEL,
        OFFSET * bottomX + PIXEL, OFFSET * (bottomY + 1) - PIXEL,
        OFFSET * bottomX + PIXEL, OFFSET * bottomY + PIXEL,
        OFFSET * (bottomX + 1) - PIXEL, OFFSET * bottomY + PIXEL,
        // FRONT QUAD 
        OFFSET * frontX + PIXEL, OFFSET * frontY + PIXEL,
        OFFSET * (frontX + 1) - PIXEL, OFFSET * frontY + PIXEL,
        OFFSET * (frontX + 1) - PIXEL, OFFSET * (frontY + 1) - PIXEL,
        OFFSET * frontX + PIXEL, OFFSET * (frontY + 1) - PIXEL,
        // BACK QUAD
        OFFSET * (backX + 1) - PIXEL, OFFSET * (backY + 1) - PIXEL,
        OFFSET * backX + PIXEL, OFFSET * (backY + 1) - PIXEL,
        OFFSET * backX + PIXEL, OFFSET * backY + PIXEL,
        OFFSET * (backX + 1) - PIXEL, OFFSET * backY + PIXEL,
        // LEFT QUAD  
        OFFSET * leftX + PIXEL, OFFSET * leftY + PIXEL,
        OFFSET * (leftX + 1) - PIXEL, OFFSET * leftY + PIXEL,
        OFFSET * (leftX + 1) - PIXEL, OFFSET * (leftY + 1) - PIXEL,
        OFFSET * leftX + PIXEL, OFFSET * (leftY + 1) - PIXEL,
        // RIGHT QUAD
        OFFSET * rightX + PIXEL, OFFSET * rightY + PIXEL,
        OFFSET * (rightX + 1) - PIXEL, OFFSET * rightY + PIXEL,
        OFFSET * (rightX + 1) - PIXEL, OFFSET * (rightY + 1) - PIXEL,
        OFFSET * rightX + PIXEL, OFFSET * (rightY + 1) - PIXEL};
    }
}