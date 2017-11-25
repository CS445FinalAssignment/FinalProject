/*************************************************************** 
*   file: Block.java 
*   group: Multi Man Melee
*   class: CS 445 - Computer Graphics
* 
*   assignment: Final Project
*   date last modified: 11/25/17
* 
*   purpose: This class represents a block including its type, 
*           id, and whether its active
* 
****************************************************************/
public class Block {

    private boolean isActive;
    private BlockType type;
    private float x, y, z;

    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);

        private int blockID;

        BlockType(int i) {
            blockID = i;
        }

        public int getID() {
            return blockID;
        }

        public void setID(int i) {
            blockID = i;
        }
    }

    public Block(BlockType type) {
        this.type = type;
    }

    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean isAcive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public int getID() {
        return type.getID();
    }
    
    @Override
    public String toString() {
        return "Block(" + x + ", + " + y + "," + z + "): " + type;
    }
}
