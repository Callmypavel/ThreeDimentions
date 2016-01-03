package peacemaker.threedimentions;

import android.util.Log;
import android.widget.EdgeEffect;

import java.security.cert.PolicyNode;
import java.util.ArrayList;

/**
 * Created by 请叫我保尔 on 2015/12/7.
 */
public class EdgeUtil {
    public static ArrayList<Edge> EdgesRemoval(ArrayList<Edge> edges,ArrayList<Point> points) {
        Log.v("EdgeUtil------->", "EdgesRemoval处理前边数"+edges.size());
        for (int i = 0; i < edges.size() ; i++) {
            Edge edge1 = edges.get(i);
            for (int j = 0; j < edges.size() ; j++) {
                Log.v("EdgeUtil------->", "EdgesRemoval进来处理的边"+edge1.getStartPointName()+","+edge1.getEndPointName());
                getPoint(edge1.getStartPointName(), points).comparePriority(getPoint(edge1.getEndPointName(), points));
                Edge edge2 = edges.get(j);
                if (edge1.symmetricEquals(edge2)) {
                    edges.remove(edges.get(j));
                }
            }
        }
        Log.v("EdgeUtil------->", "EdgesRemoval处理后的边" + edges.size());
        return edges;
    }
    public static Point getPoint(String name,ArrayList<Point> points){
        for(Point point : points){
            if (point.getName().equals(name)){
                return point;
            }
        }
        return new Point("error");
    }
}