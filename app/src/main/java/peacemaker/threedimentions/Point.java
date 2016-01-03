package peacemaker.threedimentions;

import android.util.Log;

/**
 * Created by 请叫我保尔 on 2015/12/5.
 */
public class Point {
    private float x;
    private float y;
    private float z;
    private float relativeX;
    private float relativeY;
    private float relativeZ;
    private float obserHeight = 26;
    private float obserWidth = 26;
    //private float obserDistance = (float)1;
    private float obserx = 0;
    private float obsery = 0;
    private float obserz = 13;
    private float α;
    private float β;
    private String name;
    private boolean priority = true;
    private boolean isCaculating = false;
    private static final int xoyMode = 0x100;
    private static final int yozMode = 0x200;
    private static final int xozMode = 0x300;
    public Point(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    public Point(float x,float y,float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Point(String name){
        this.name = name;
    }
    public Point(float x,float y,float z,String name,int visionMode){
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
        setvisionMode(visionMode);
    }
    public Point(float x,float y,float z,int visionMode){
        this.x = x;
        this.y = y;
        this.z = z;
        setvisionMode(visionMode);
    }
//    public Point(float x,float y,float z,String name,float degreeα,float degreeβ){
//        this.x = x;
//        this.y = y;
//        this.z = z;
//        this.name = name;
//        setRelativeX(x);
//        setRelativeY(z);
//        setRelativeZ(-y + 0 + obserDistance);
//        coordianteTransfer(degreeα, degreeβ);
//    }
    public Point(float x,float y){
        this.x = x;
        this.y = y;
        this.z = 0;
    }
    public void setX(float x){
        this.x = x;
    }
    public float getX(){
        return this.x;
    }
    public void setY(float y){
        this.y = y;
    }
    public float getY(){
        return this.y;
    }
    public void setZ(float z){
        this.z = z;
    }
    public float getZ(){
        return this.z;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setPriority(Boolean priority){
        this.priority = priority;
    }
    public Boolean getPriority(){
        return this.priority;
    }
    public float getRelativeX(){
        return this.relativeX;
    }
    public void setRelativeX(float relativeX){
        this.relativeX = relativeX;
    }
    public float getRelativeY(){
        return this.relativeY;
    }
    public void setRelativeY(float relativeY){
        this.relativeY = relativeY;
    }
    public float getRelativeZ(){
        return this.relativeZ;
    }
    public void setRelativeZ(float relativeZ){
        this.relativeZ = relativeZ;
    }
    public void setvisionMode(int visionMode){
        switch (visionMode){
            case xoyMode:setRelativeX(x);setRelativeY(y);setRelativeZ(z);break;
            case yozMode:setRelativeX(y);setRelativeY(z);setRelativeZ(x);break;
            case xozMode:setRelativeX(x);setRelativeY(z);setRelativeZ(-y+0+13);break;
        }
    }
    public Boolean getCaculateState(){
        return this.isCaculating;
    }
    public  void coordianteTransfer(float α,float β,float obserHeight,float obserWidth,float obserDistance){
//        Log.v("Point------->", "coordinateTransfer开始转制");
            float sinα = (float)Math.sin(α);
            float cosα = (float)Math.cos(α);
            float sinβ = (float)Math.sin(β);
            float cosβ = (float)Math.cos(β);
//            Log.v("Point------->", "coordinateTransfer检查x平移前:(" + relativeX + "," + relativeY + "," + relativeZ + ")");
//        double translatedX = relativeX-(obserx+sinα*obserDistance-obserWidth/2*cosα);
            float translatedX = relativeX - (obserx + sinα * obserDistance);
            float translatedY = relativeY;
//        double translatedZ = relativeZ-(obserz-cosα*obserDistance-obserWidth/2*sinα);
            float translatedZ = relativeZ - (obserz - cosα * obserDistance);
//            Log.v("Point------->", "coordinateTransfer检查x旋转前:(" + translatedX + "," + translatedY + "," + translatedZ + ")");
            relativeX = cosα * translatedX + sinα * translatedZ;
            relativeY = translatedY;
            relativeZ = cosα * translatedZ - sinα * translatedX;
//            Log.v("Point------->", "coordinateTransfer检查z平移前:(" + relativeX + "," + relativeY + "," + relativeZ + ")");
            translatedX = relativeX;
            translatedY = relativeY - (obsery + sinβ * obserDistance);
            translatedZ = relativeZ - (obserz - cosβ * obserDistance);
//        translatedY = relativeY-(obsery+sinβ*obserDistance-obserHeight/2*cosβ);
//        translatedZ = relativeZ-(obserz-cosβ*obserDistance-obserHeight/2*sinβ);
//        translatedY = relativeY-(obsery-cosβ*obserDistance+obserHeight/2*sinβ);
//        translatedZ = relativeZ-(obserz-sinβ*obserDistance-obserHeight/2*cosβ);
//            Log.v("Point------->", "coordinateTransfer检查z旋转后:(" + translatedX + "," + translatedY + "," + translatedZ + ")");
            relativeX = (float) (translatedX);
            relativeY = (float) (cosβ * translatedY + sinβ * translatedZ);
            relativeZ = (float) (cosβ * translatedZ - sinβ * translatedY);
            isCaculating = false;
//            Log.v("Point------->", "coordinateTransfer检查翻转后:(" + relativeX + "," + relativeY + "," + relativeZ + ")");
    }

    public Boolean equals(Point point){
        if(point.getRelativeX()==relativeX
                &&point.getRelativeY()==relativeY
                &&point.getRelativeZ()==relativeZ){
            return true;
        }
        return false;
    }
    public Boolean relativeEquals(Point point){
        if(point.getRelativeX()==relativeX
                &&point.getRelativeY()==relativeY){
            return true;
        }
        return false;
    }
    public void scaling(float proportion){
        relativeX *= proportion;
        relativeY *= proportion;
        relativeZ *= proportion;
    }
    public void comparePriority(Point point) {
        if (relativeEquals(point)) {
            if (point.getRelativeZ() > relativeZ) {
                priority = false;
                point.setPriority(true);
            } else if (point.getRelativeZ() < relativeZ) {
                priority = true;
                point.setPriority(false);
            }
        }
    }
}
