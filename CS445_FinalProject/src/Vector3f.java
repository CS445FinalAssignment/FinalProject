/*************************************************************** 
*   file: Vector3f.java 
*   group: Multi Man Melee
*   class: CS 445 - Computer Graphics
* 
*   assignment: Final Project
*   date last modified: 11/9/17
* 
*   purpose: This class stores 3 float values representing the x, y, and z values
*       of a vector
* 
****************************************************************/ 

public class Vector3f {
    
    public float x;
    public float y;
    public float z;
    
    public Vector3f() {
        x = 0;
        y = 0;
        z = 0;
    }
    
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //method: set
    //purpose: updates value of the vector
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
}
