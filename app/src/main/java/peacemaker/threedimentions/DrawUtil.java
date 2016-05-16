package peacemaker.threedimentions;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by 请叫我保尔 on 2016/1/3.
 */
public class DrawUtil {
//    native String drawTri(short[] points);
//    native void scroll(float x1,float y1,float x2,float y2);
//    native String drawTri(short[] indicies,short[] verticles);
//    native void init();
    static
    {
        System.loadLibrary("ThreeDrawer");
    }

}
