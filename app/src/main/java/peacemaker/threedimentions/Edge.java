package peacemaker.threedimentions;

import android.util.Log;

/**
 * Created by 请叫我保尔 on 2015/12/5.
 */
public class Edge {
    private String startPointName;
    private String endPointName;
    public Edge(String startPointName,String endPointName){
        this.startPointName = startPointName;
        this.endPointName = endPointName;
    }
    public void setStartPoint(String startPointName){
        this.startPointName = startPointName;
    }
    public void setEndPointName(String endPointName){
        this.endPointName = endPointName;
    }
    public String getStartPointName(){
        return this.startPointName;
    }
    public String getEndPointName(){
        return this.endPointName;
    }
    public Boolean symmetricEquals(Edge edge){
        if(edge.getStartPointName().equals(endPointName)
                &&edge.getEndPointName().equals(startPointName)){
            return true;
        }else return false;
    }
    public Boolean equals(Edge edge){
        if(edge.getStartPointName().equals(startPointName)
                &&edge.getEndPointName().equals(endPointName)){
            return true;
        }
        return false;
    }
//    public Boolean relativeTheSame(){
//        if(getStartPoint().relativeEquals(getEndPoint())){
//            return true;
//        }
//        return false;
//    }


}
