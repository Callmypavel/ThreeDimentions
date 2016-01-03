package peacemaker.threedimentions;

import java.util.ArrayList;

/**
 * Created by 请叫我保尔 on 2015/12/5.
 */
public class Polygon {
    private ArrayList<Point> vertexs;
    private ArrayList<Edge> edges;
    public void setVertexs(ArrayList<Point> vertexs){
        this.vertexs = vertexs;
    }
    public void setEdges(ArrayList<Edge> edges){
        this.edges = edges;
    }
    public ArrayList<Edge> getEdges(){
        return this.edges;
    }
    public ArrayList<Point> getVertexs(){
        return this.vertexs;
    }
}
