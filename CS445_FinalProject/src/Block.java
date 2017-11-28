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
    private final BlockType type;
    private float x, y, z;

    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5),
        BlockType_Log(6),
        BlockType_Leaves(7),
        BlockType_IronOre(8),
        BlockType_EmeraldOre(9),
        BlockType_GoldOre(10),
        BlockType_Pumpkin(11);
        
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

    // method: setCoords
    // purpose: Set coordinate position
    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // method: IsActive
    // purpose: Return true if active, false otherwise
    public boolean isActive() {
        return isActive;
    }

    // method: SetActive
    // purpose: Set block status to active
    public void setActive(Boolean active) {
        isActive = active;
    }

    // method: getID
    // purpose: return the id of the block
    public int getID() {
        return type.getID();
    }
    
    //method: toString
    //purpose: returns string representation of this object
    @Override
    public String toString() {
        return "Block(" + x + ", + " + y + "," + z + "): " + type;
    }
}
