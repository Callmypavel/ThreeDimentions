package peacemaker.threedimentions;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by 请叫我保尔 on 2015/12/13.
 */
public class ModelObject {
    //几何体顶点
    private ArrayList<ModelElement> v;
    //贴图坐标点
    private ArrayList<ModelElement> vt;
    //顶点法线
    private ArrayList<ModelElement> vn;
    //面
    private ArrayList<ModelElement> f;
    public ModelObject(){
        v = new ArrayList<>();
        vt = new ArrayList<>();
        vn = new ArrayList<>();
        f = new ArrayList<>();
    }

    public void addv(float[] vs){
        //Log.v("ModelObject---------->", "addv()增加顶点检查" + v.size());
        v.add(new ModelElement(vs));
    }
    public void addvt(float[] vts){
        vt.add(new ModelElement(vts));
    }
    public void addvn(float[] vns){
        vn.add(new ModelElement(vns));
    }
    public void addf(int[] fs){
        f.add(new ModelElement(fs));
    }
    public ArrayList<ModelElement> getV(){
        //Log.v("ModelObject---------->", "getv()返回顶点检查" + v.size());
        return v;
    }
    public ArrayList<ModelElement> getVt() {
        return vt;
    }

    public ArrayList<ModelElement> getVn() {
        return vn;
    }

    public ArrayList<ModelElement> getF() {
        return f;
    }
}
