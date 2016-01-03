package peacemaker.threedimentions;

/**
 * Created by 请叫我保尔 on 2015/12/13.
 */
public class ModelElement {
    private float[] ele_floats;
    private int[] ele_ints;
    public ModelElement(float[] ele_floats){
        this.ele_floats = ele_floats;

    }
    public ModelElement(int[] ele_ints){
        this.ele_ints = ele_ints;
    }

    public float[] getEle_floats() {
        return ele_floats;
    }

    public int[] getEle_ints() {
        return ele_ints;
    }
}
