package peacemaker.threedimentions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 请叫我保尔 on 2015/12/5.
 */
public class GeneralView extends SurfaceView implements SurfaceHolder.Callback {
    float xmax = 0;
    float ymax = 0;
    float zmax = 0;
    float xmin = 0;
    float ymin = 0;
    float zmin = 0;
    float max = 0;
    private float xMax;
    private float yMax;
    private float xAxis;
    private float yAxis;
    private int unitLength = 40;
    private int width;
    private int divation;
    private int counter;
    int tempα = 0;
    int tempβ = 0;
    private float proportionality;
    private SurfaceHolder surfaceHolder;
    private static final int xoyMode = 0x100;
    private static final int yozMode = 0x200;
    private static final int xozMode = 0x300;
    private ArrayList<Edge> edges;
    private ArrayList<Point> points;
    private ArrayList<Face> faces;
    private ArrayList<Point> initialpoints;
    private Canvas canvas;
    private Boolean isDrawing = true;
    private float degreeα = 0;
    private float degreeβ = 0;
    private float maxSpeed = 5;
    private float αspeed;
    private float βspeed;
    private Thread rotateThread;
    private Runnable rotateRunnable;
    private Boolean isStop = false;
    private Timer timer;
    private TimerTask timerTask;
    private Context context;
    private int waitingpoints;
    private ExecutorService cachedThreadPool;
    private int threadnumber;
    private float a ;
    private float b ;
    private ArrayList<CalculateRunnable> calculateRunnables;
    private float waitingDegree = (float) Math.PI / 36;

    public GeneralView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GeneralView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        initializePoints();
    }

    public GeneralView(Context context) {
        super(context);
    }

    public float getDegree() {
        return this.degreeα;
    }

    public void scaling(float proportion) {
        for (Point point : points) {
            point.scaling(proportion);
        }
    }

    public void setDegree(float deltaα, float deltaβ) {
        //Log.v("GeneralView------->", "setDegree开始转制");
        Point point1 = new Point(0,0,0,xozMode);
        point1.coordianteTransfer(deltaα, deltaβ, 2 * max * proportionality, 2 * max * proportionality, max * proportionality);
        threadnumber = points.size() / 17;
        a = deltaα;
        b = deltaβ;
//        if(cachedThreadPool==null) {
//            cachedThreadPool = Executors.newCachedThreadPool();
//            calculateRunnables = new ArrayList<>();
//            for (int i = 0; i < 17; i++) {
//                CalculateRunnable calculateRunnable = new CalculateRunnable();
//                calculateRunnables.add(calculateRunnable);
//                calculateRunnable.setNumber(i);
//                cachedThreadPool.execute(calculateRunnable);
//            }
//        }else{
//            for (int i = 0; i < 17; i++) {
//                calculateRunnables.get(i).setWorking();
//            }
//        }
        long startTime = System.currentTimeMillis();
        for (Point point : points) {
            synchronized (point) {
                point.coordianteTransfer(deltaα, deltaβ, 2 * max * proportionality, 2 * max * proportionality, max * proportionality);
                point.setPriority(true);
            }
        }
        long endTime = System.currentTimeMillis();
        long period = endTime - startTime;
        //Log.v("GeneralView------->", "setDegree转制用时" + period);
    }

    public void setSpeed(final float sinα, final float cosα, final float degree) {
        Log.v("GeneralView------->", "setSpeed开始传入");
//        if(rotateThread==null) {
//            if(rotateRunnable==null){
//                rotateRunnable = new rotateRunnable();
//            }
//            rotateThread = new Thread(rotateRunnable);
//            rotateThread.start();
//        }
        //αspeed = maxSpeed;
        //βspeed = maxSpeed;
        if (timerTask != null) {
            timerTask.cancel();
            timer.cancel();
            timerTask = null;
            timer = null;
        }
        if (sinα == 0 && cosα == 0) {
            return;
        }

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                float deltaα = (float) (cosα * maxSpeed * degree / 10000 * 180 / Math.PI);
                float deltaβ = (float) (sinα * maxSpeed * degree / 10000 * 180 / Math.PI);
//                tempα+=deltaα;
//                tempβ+=deltaβ;
                //Log.v("GeneralView------->", "缓冲角度 " + tempα + "," + tempβ);

//                if((tempα>10||tempα==10)||(tempβ>10||tempβ==10)) {
//                    Log.v("GeneralView------->", "缓冲角度 " +tempα+","+tempβ);
//                    setDegree(tempα, tempβ);
//                }
//                if (counter > 30 || counter == 30) {
                counter = 0;
                if (((degreeβ* 180 / Math.PI == 0||degreeβ* 180 / Math.PI<0) && deltaβ < 0) || ((degreeβ* 180 / Math.PI == 180||degreeβ* 180 / Math.PI>180) && deltaβ > 0)) {
                    setDegree(deltaα, 0);
                }
                setDegree(deltaα, deltaβ);
                degreeα += deltaα;
                degreeβ += deltaβ;
//                }
//                    counter += 1;
//                degreeα+=tempα;
//                degreeβ+=tempβ;
                //Log.v("GeneralView------->", "现在角度 " + degreeβ);
            }

        };
        timer.schedule(timerTask, 0, 2);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        WindowManager wm = (WindowManager) getContext()
//                .getSystemService(Context.WINDOW_SERVICE);
//        if(width==0){
//            width = wm.getDefaultDisplay().getWidth();
//            xMax = width;
//            yMax = width;
//            divation = width/unitLength;
//            divation /= 2;
//        }
        //获取屏幕宽度并设置为尺寸大小
        //Log.v("GeneralView------->", "onMeasure获取宽度" + width);
        setMeasuredDimension(width, width);
        //setPadding(unitLength, unitLength, unitLength, unitLength);
    }

    protected void drawGrid() {
        //Log.v("GeneralView------->", "createGrid被调用");
        ArrayList<Point> points = new ArrayList<>();
        Paint paint = new Paint();
        paint.setColor(Color.rgb(255, 255, 240));
        paint.setStrokeWidth(2);
        //画横向线
        for (float y = 0; y <= yMax; y += unitLength) {
            points.add(new Point(unitLength, y));
        }
        for (Point point : points) {
            canvas.drawLine(0, point.getY(), xMax, point.getY(), paint);
        }
        points.clear();
        //画纵向线
        for (float x = 0; x <= xMax; x += unitLength) {
            points.add(new Point(x, unitLength));
        }
        for (Point point : points) {
            canvas.drawLine(point.getX(), 0, point.getX(), yMax, paint);
        }


    }

    protected void initializePoints() {

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        xMax = width;
        yMax = width;
        divation = width / unitLength;
        divation /= 2;
        ModelLoader modelLoader = new ModelLoader();
        ModelObject modelObject = modelLoader.LoadModel("arcticcondor.obj", context);
        //Log.v("GeneralView------->", "initializePoints()modelObject检查:(" + modelObject + ")");
        if (modelObject != null) {
            points = new ArrayList<>();
            faces = new ArrayList<>();
            ArrayList<ModelElement> elements = modelObject.getV();
            //Log.v("GeneralView------->", "initializePoints()顶点数检查:(" + elements.size() + ")");
            xmax = 0;
            ymax = 0;
            zmax = 0;
            xmin = 0;
            ymin = 0;
            zmin = 0;
            max = 0;
            for (ModelElement element : elements) {
                float[] vs = element.getEle_floats();
                if (vs[0] > xmax) {
                    xmax = vs[0];
                } else if (vs[0] < xmin) {
                    xmin = vs[0];
                }
                if (vs[1] > ymax) {
                    ymax = vs[1];
                } else if (vs[1] < ymin) {
                    ymin = vs[1];
                }
                if (vs[2] > zmax) {
                    zmax = vs[2];
                } else if (vs[2] < zmin) {
                    zmin = vs[2];
                }
                max = xmax > ymax ? xmax : ymax;
                max = max > zmax ? max : zmax;
                max = max > -xmin ? max : -xmin;
                max = max > -ymin ? max : -ymin;
                max = max > -zmin ? max : -zmin;
            }
            proportionality = divation / max;
            for (ModelElement element : elements) {
                float[] vs = element.getEle_floats();
                //Log.v("GeneralView------->", "initializePoints()观察元素前:(" + vs[0] + "," + vs[1] + "),(" +
                //        vs[2] + ")");
                float x = vs[0] * proportionality;
                float y = vs[1] * proportionality;
                float z = vs[2] * proportionality;
                //Log.v("GeneralView------->", "initializePoints()观察元素:(" + x + "," + y + "),(" +
                 //       z + ")");
                points.add(new Point(x, y, z, xozMode));
            }
            Paint paint = new Paint();


            ArrayList<ModelElement> elements1 = modelObject.getF();
            for (ModelElement element : elements1) {
                int[] fs = element.getEle_ints();
                faces.add(new Face(fs));
            }

        }
//        points = new ArrayList<>();
//        points.add(new Point(4, 12, 4,"A",xozMode));
//        points.add(new Point(12, 12, 4,"B",xozMode));
//        points.add(new Point(12, 4, 4,"C",xozMode));
//        points.add(new Point(4, 4, 4,"D",xozMode));
//        points.add(new Point(4, 12, 12,"A'",xozMode));
//        points.add(new Point(12, 12, 12, "B'",xozMode));
//        points.add(new Point(12, 4, 12, "C'",xozMode));
//        points.add(new Point(4, 4, 12, "D'",xozMode));
//        reset();
//        Polygon polygon = new Polygon();
//        polygon.setVertexs(points);
//        edges = new ArrayList<>();
//        for (Point point : points) {
//            for (Point point1 : points) {
//                if ((point1.getX() == point.getX() && point1.getY() == point.getY())
//                        || (point1.getY() == point.getY() && point1.getZ() == point.getZ())
//                        || (point1.getX() == point.getX() && point1.getZ() == point.getZ())
//                        ) {
//                    if (!point.equals(point1)) {
//                        edges.add(new Edge(point1.getName(), point.getName()));
//                    }
//                }
//
//            }
//        }
//        EdgeUtil.EdgesRemoval(edges,points);
    }

    public void reset() {
        for (Point point : points) {
            point.setvisionMode(xozMode);
            //point.coordianteTransfer((float)(45*Math.PI/180),(float)(-45*Math.PI/180));
        }
    }

    public void drawPoints() {
        Paint paint = new Paint();
        paint.setColor(Color.rgb(0, 255, 255));
        paint.setStrokeWidth(1);
        Paint paint1 = new Paint();
        paint.setStrokeWidth(1);
//            for (Face face : faces) {
//                int[] pointindex = face.getIndexs();
//                for (int index = 0; index < 2; index += 1) {
//                    Point point1 = points.get(pointindex[index]);
//                    Point point2 = points.get(pointindex[index + 3]);
//                    synchronized (point1) {
//                        synchronized (point2) {
//                            canvas.drawLine((point1.getRelativeX() + divation) * unitLength, (point1.getRelativeY() + divation) * unitLength,
//                                    (point2.getRelativeX() + divation) * unitLength, (point2.getRelativeY() + divation) * unitLength, paint);
//                        }
//                    }
//                }
//            }
        long start = System.currentTimeMillis();
        for (Face face : faces) {
            int[] pointindex = face.getIndexs();
            Point point1 = points.get(pointindex[0] - 1);
            Point point2 = points.get(pointindex[3] - 1);
            Point point3 = points.get(pointindex[6] - 1);
            synchronized (point1) {
                synchronized (point2) {
                    synchronized (point3) {
                        drawTriangles(point1, point2, point3, paint, canvas);
                        canvas.drawLine((point1.getRelativeX() + divation) * unitLength, (point1.getRelativeY() + divation) * unitLength,
                                (point2.getRelativeX() + divation) * unitLength, (point2.getRelativeY() + divation) * unitLength, paint1);
                        canvas.drawLine((point2.getRelativeX() + divation) * unitLength, (point2.getRelativeY() + divation) * unitLength,
                                (point3.getRelativeX() + divation) * unitLength, (point3.getRelativeY() + divation) * unitLength, paint1);
                        canvas.drawLine((point3.getRelativeX() + divation) * unitLength, (point3.getRelativeY() + divation) * unitLength,
                                (point1.getRelativeX() + divation) * unitLength, (point1.getRelativeY() + divation) * unitLength, paint1);
//                    }
                    }
                }
            }
            long end = System.currentTimeMillis();
            long period = end - start;
            Log.v("GeneralView------->", "测试三角形渲染用时:" + period+"ms");


//        for (Edge edge : edges) {
//            for(Point point : points){
//                canvas.drawCircle(point.getRelativeX() * unitLength, (width/unitLength-point.getRelativeY()) * unitLength, 4, paint);
//                Log.v("GeneralView------->", "drawPoints点坐标变化:(" + point.getRelativeX()+","+point.getRelativeY()+ ")");
//            }
//            paint.setTextSize(50);
//            Point StartPoint = EdgeUtil.getPoint(edge.getStartPointName(), points);
//            float sx = StartPoint.getRelativeX() * unitLength ;
//            float sy = (width/unitLength-StartPoint.getRelativeY()) * unitLength ;
//            Point EndPoint = EdgeUtil.getPoint(edge.getEndPointName(), points);
//            float ex = EndPoint.getRelativeX() * unitLength ;
//            float ey = (width/unitLength-EndPoint.getRelativeY()) * unitLength ;
//            drawMark(StartPoint, paint);
//            drawMark(EndPoint, paint);
//            Log.v("GeneralView------->", "drawPoints线段:(" +sx+","+sy+")"+"("+ex+","+ey+")");
//            canvas.drawLine(sx,sy,ex,ey,paint);
//        }

        }
    }

    public void drawMark(Point point, Paint paint) {
        if (point.getPriority()) {
            canvas.drawText(point.getName(), point.getRelativeX() * unitLength, (width / unitLength - point.getRelativeY()) * unitLength, paint);
        } else {
            canvas.drawText("(" + point.getName() + ")", point.getRelativeX() * unitLength + unitLength, (width / unitLength - point.getRelativeY()) * unitLength, paint);
        }
    }

    protected void drawLine(float x1, float y1, float x2, float y2) {
        canvas = surfaceHolder.lockCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.rgb(255, 255, 240));
        paint.setStrokeWidth(2);
        long startTime = System.currentTimeMillis();
        int drawx1 = (int) x1;
        int drawx2 = (int) x2;
        int drawy1 = (int) y1;
        int drawy2 = (int) y2;
        Boolean flag = false;
        int stepx = 1;
        int stepy = 1;
        if (drawx2 - drawx1 < 0) {
            stepx = -1;
        }
        if (drawy2 - drawy1 < 0) {
            stepy = -1;
        }
        int deltax = Math.abs(drawx2 - drawx1);
        int deltay = Math.abs(drawy2 - drawy1);
        if (deltax == 0) {
            for (int i = 0; i < Math.abs(deltax); i++) {
                drawy1 += stepy;
                canvas.drawPoint(drawx1, drawy1, paint);

            }
            long endTime = System.currentTimeMillis();
            long period = endTime - startTime;
            Log.v("GeneralView------->", "自制租用时" + period);
        } else if (deltay == 0) {
            for (int i = 0; i < Math.abs(deltay); i++) {
                drawx1 += stepx;
                canvas.drawPoint(drawx1, drawy1, paint);
            }
            long endTime = System.currentTimeMillis();
            long period = endTime - startTime;
            Log.v("GeneralView------->", "自制租用时" + period);
        } else {
            if (deltay > deltax) {
                int i = deltax;
                deltax = deltay;
                deltay = i;
                flag = true;
            }
            int ne = 2 * deltay - deltax;
            for (int i = 0; i < deltax; i++) {
                if (flag) {
                    drawy1 += stepy;
                } else {
                    drawx1 += stepx;
                }
                if (ne > 0) {
                    if (flag) {
                        drawx1 += stepx;
                    } else {
                        drawy1 += stepy;
                    }
                    ne -= 2 * deltax;
                }
                ne += 2 * deltay;
                canvas.drawPoint(drawx1, drawy1, paint);

            }
            long endTime = System.currentTimeMillis();
            long period = endTime - startTime;
            Log.v("GeneralView------->", "自制租用时" + period);
        }
        surfaceHolder.unlockCanvasAndPost(canvas);
    }
    protected void drawTriangles(Point point1,Point point2,Point point3,Paint paint,Canvas canvas){
        int x1 = Math.round((point1.getRelativeX() + divation) * unitLength);
        int y1 = Math.round((point1.getRelativeY() + divation) * unitLength);
        int x2 = Math.round((point2.getRelativeX() + divation) * unitLength);
        int y2 = Math.round((point2.getRelativeY() + divation) * unitLength);
        int x3 = Math.round((point3.getRelativeX() + divation) * unitLength);
        int y3 = Math.round((point3.getRelativeY() + divation) * unitLength);
        int deltax12 = x1-x2;
        int deltax23 = x2-x3;
        int deltax31 = x3-x1;
        int deltay12 = y1-y2;
        int deltay23 = y2-y3;
        int deltay31 = y3-y1;
        int minx = Math.min(Math.min(x1, x2), x3);
        int miny = Math.min(Math.min(y1, y2), y3);
        int maxx = Math.max(Math.max(x1, x2), x3);
        int maxy = Math.max(Math.max(y1,y2),y3);
        final int q = 8;
        //以8为单位长度的起始值
        minx &= ~(q-1);
        miny &= ~(q-1);
        int c1 = deltay12*x1-deltax12*y1;
        int c2 = deltay23*x2-deltax23*y2;
        int c3 = deltay31*x3-deltax31*y3;
        if(deltay12<0||deltay12==0&&deltax12>0){
            c1++;
        }
        if(deltay23<0||deltay23==0&&deltax23>0){
            c2++;
        }
        if(deltay31<0||deltay31==0&&deltax31>0){
            c3++;
        }

        for(int y = miny;y<maxy;y+=q){
            //Log.v("GeneralView------->", "drawTriangles()y循环");
            for(int x = minx;x<maxx;x+=q){
                //Log.v("GeneralView------->", "drawTriangles()x循环");
                int x0 = x;
                int xf = x+q;
                int y0 = y;
                int yf = y+q;

                int a = 0;
                if(deltax12*y0-deltay12*x0>0){
                    a+=1;
                }
                if(deltax12*y0-deltay12*xf>0){
                    a+=10;
                }
                if(deltax12*yf-deltay12*x0>0){
                    a+=100;
                }
                if(deltax12*yf-deltay12*xf>0){
                    a+=1000;
                }

                int b = 0;
                if(deltax23*y0-deltay23*x0>0){
                    b+=1;
                }
                if(deltax23*y0-deltay23*xf>0){
                    b+=10;
                }
                if(deltax23*yf-deltay23*x0>0){
                    b+=100;
                }
                if(deltax23*yf-deltay23*xf>0){
                    b+=1000;
                }

                int c = 0;
                if(deltax31*y0-deltay31*x0>0){
                    c+=1;
                }
                if(deltax31*y0-deltay31*xf>0){
                    c+=10;
                }
                if(deltax31*yf-deltay31*x0>0){
                    c+=100;
                }
                if(deltax31*yf-deltay31*xf>0){
                    c+=1000;
                }
                if(a==0&&b==0&&c==0){
                    continue;
                }else if(a==1111&&b==1111&&c==1111){
                    for(int iy=0;iy<q;iy++){
                        for(int ix = x;ix<x+q;ix++){
                            canvas.drawPoint(ix, iy, paint);
                        }
                    }
                }else{
                    int cy1 = c1 + deltax12*y0-deltay12*x0;
                    int cy2 = c2 + deltax23*y0-deltay23*x0;
                    int cy3 = c3 + deltax31*y0-deltay31*x0;
                    for(int iy = y;iy<y+q;iy++){
                        int cx1 = cy1;
                        int cx2 = cy2;
                        int cx3 = cy3;
                        for (int ix = x;ix<x+q;ix++){
                            if(cx1>0&&cx2>0&&cx3>0){
//                                if(cx1==1||cx2==1||cx3==1){
//                                    canvas.drawPoint(ix,iy,paint1);
//                                }else {
                                    canvas.drawPoint(ix,iy,paint);
//                                }
                            }
                            cx1-=deltay12;
                            cx2-=deltay23;
                            cx3-=deltay31;
                        }
                        cy1+=deltax12;
                        cy2+=deltax23;
                        cy3+=deltax31;
                    }
                }
            }
        }
    }

    protected void drawImages() {
        canvas = surfaceHolder.lockCanvas();
        clearDraw();
        drawPoints();
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    protected void testCanvas() {
        canvas = surfaceHolder.lockCanvas();
        Log.v("GeneralView------->", "testCanvas残忍测试:(" + canvas + ")");
    }

    protected void clearDraw() {
        canvas.drawColor(Color.BLACK);// 清除画布
        //drawGrid();
    }

    protected void stopRotate() {
        isStop = true;
    }

    protected void admitRotate() {
        isStop = false;
        rotateRunnable = new rotateRunnable();
        Log.v("GeneralView------->", "setSpeed解锁线程");
    }

    protected void setxAxis(float x) {
        this.xAxis = x / unitLength - 14;
    }

    protected float getxAxis() {
        return this.xAxis;
    }

    protected void setyAxis(float y) {
        this.yAxis = y / unitLength - 14;
    }

    protected float getyAxis() {
        return this.yAxis;
    }

    protected float getyMax() {
        return this.yMax;
    }

    protected float getxMax() {
        return this.xMax;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //canvas = surfaceHolder.lockCanvas(new Rect(0,0,width,width));
        //drawGrid();
        //surfaceHolder.unlockCanvasAndPost(canvas);
        Thread thread = new Thread(new drawRunnable());
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    class drawRunnable implements Runnable {
        @Override
        public void run() {
            while (isDrawing) {

                synchronized (surfaceHolder) {
                    drawImages();
//                    drawLine(500, 600, 1000, 1000);
//                    canvas = surfaceHolder.lockCanvas();
//                    Paint paint = new Paint();
//                    paint.setColor(Color.rgb(255, 255, 240));
//                    paint.setStrokeWidth(2);
//                    long starttime = System.currentTimeMillis();
//                    canv as.drawLine(500, 600, 1000, 1000, paint);
//                    long end = System.currentTimeMillis();
//                    long period = end - starttime;
//                    Log.v("GeneralView------->", "官方租用时" + period);
//                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
                try {
                       Thread.sleep(1);
                } catch (Exception e) {
                }
            }
        }
    }

    class rotateRunnable implements Runnable {
        @Override
        public void run() {
            while (!isStop) {
                //Thread.sleep(10);
                degreeα += αspeed / 1000000;
                degreeβ += βspeed / 1000000;
                setDegree(degreeα, degreeβ);
                Log.v("GeneralView------->", "setSpeed线程运行中惹" + αspeed);
            }
        }
    }

    class CalculateRunnable implements Runnable {
        private int number;
        public void setNumber(int number){
            this.number = number;
        }
        public void setWorking(){
            isCalculating = false;
        }
        private Boolean isCalculating = false;
        @Override
        public void run() {
            while (!isCalculating) {
                isCalculating = true;
                for (int i1 = 0; i1 < threadnumber; i1++) {
                    if (!(number * threadnumber + i1 == points.size())) {
                        Point point = points.get(number * threadnumber + i1);
                        Log.v("GeneralView------->", "setDegree处理点" + number * threadnumber + i1);
                        synchronized (point) {
                            point.coordianteTransfer(a, b, 2 * max * proportionality, 2 * max * proportionality, max * proportionality);
                        }

                    }
                }

            }
        }
    }
}


