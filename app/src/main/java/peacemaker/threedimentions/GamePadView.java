package peacemaker.threedimentions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by 请叫我保尔 on 2015/12/5.
 */
public class GamePadView extends View implements View.OnTouchListener {
    private Context context;
    private Canvas canvas;
    private int backgroundWidth;
    private int stickWidth;
    private int radiSqu;
    private float centreX;
    private float centreY;
    private float currentX;
    private float currentY;
    private final static int Reset = 0x000;
    private final static int RightUp = 0x001;
    private final static int RightDown = 0x002;
    private final static int LeftUp = 0x003;
    private final static int LeftDown = 0x004;
    private OnOperationListener onOperationListener;
    public GamePadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        //Log.v("GamePadView------->", "三参数构造方法");
        //drawBackground();
        backgroundWidth = BitmapFactory.decodeResource(context.getResources(), R.drawable.console_back).getWidth();
        stickWidth = BitmapFactory.decodeResource(context.getResources(), R.drawable.console).getWidth();
        centreX = getLeft()+backgroundWidth/2+stickWidth/2;
        centreY = getTop()+backgroundWidth/2+stickWidth/2;
        currentX = centreX;
        currentY = centreY;
    }

    public GamePadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //Log.v("GamePadView------->", "二参数构造方法");
        //drawBackground();
        backgroundWidth = BitmapFactory.decodeResource(context.getResources(), R.drawable.console_back).getWidth();
        stickWidth = BitmapFactory.decodeResource(context.getResources(), R.drawable.console).getWidth();
        centreX = getLeft()+backgroundWidth/2+stickWidth/2;
        centreY = getTop()+backgroundWidth/2+stickWidth/2;
        currentX = centreX;
        currentY = centreY;
        //Log.v("GamePadView------->", "初始化中心点"+currentX+","+currentY);
    }

    public GamePadView(Context context) {
        super(context);
        this.context = context;
        //Log.v("GamePadView------->", "一参数构造方法");
        //drawBackground();
        backgroundWidth = BitmapFactory.decodeResource(context.getResources(), R.drawable.console_back).getWidth();
        stickWidth = BitmapFactory.decodeResource(context.getResources(), R.drawable.console).getWidth();
        centreX = getLeft()+backgroundWidth/2+stickWidth/2;
        centreY = getTop()+backgroundWidth/2+stickWidth/2;
        currentX = centreX;
        currentY = centreY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        super.onDraw(canvas);
        setOnTouchListener(this);
        drawBackground();
        drawStick(currentX,currentY);
    }

    public void setOperationListener(OnOperationListener onOperationListener){
        this.onOperationListener = onOperationListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        backgroundWidth = BitmapFactory.decodeResource(context.getResources(), R.drawable.console_back).getWidth();
//        stickWidth = BitmapFactory.decodeResource(context.getResources(), R.drawable.console).getWidth();
        setMeasuredDimension(backgroundWidth+stickWidth, backgroundWidth+stickWidth);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        //Log.v("GamePadView------->", "onTouchxy" +x+","+y);
        if(radiSqu==0) {
            radiSqu = (int) (Math.pow(backgroundWidth, 2) / 4);
        }
        //Log.v("GamePadView------->", "onTouch半径平方" +radiSqu);
        float angle = (float)(Math.atan((centreY-y)/(x-centreX))/Math.PI*180.0);
        float length = (float)(Math.pow((x - centreX),2)+Math.pow((y - centreY),2));
        //Log.v("GamePadView------->", "onTouch长度" +length);
        float degree = length/radiSqu;
        if(degree>1){
            degree =1;
        }
        //Log.v("GamePadView------->", "onTouch程度" +degree);
        if(x>centreX||x==centreX){
            if(y>centreY||y==centreY){
                angle+=360;
                onOperationListener.onOperation(RightDown,angle,degree);
            }else {
                onOperationListener.onOperation(RightUp,angle,degree);
            }
        }else{
            if(y<centreY||y==centreY){
                angle+=180;
                onOperationListener.onOperation(LeftUp,angle,degree);
            }else {
                angle+=180;
                onOperationListener.onOperation(LeftDown,angle,degree);
            }
        }
        if(length<radiSqu){
            //Log.v("GamePadView------->", "圆心距平方和" +Math.pow((x - centreX),2)+Math.pow((y - centreY),2));
            currentX = x;
            currentY = y;
        }else {
//            double denominator =Math.sqrt(Math.pow((double)(y-centreY),2)+Math.pow((double)(x-centreX),2));
//            double x1 = Math.abs(backgroundWidth*(x-centreX)/denominator)+centreX;
//            double y1 = Math.abs(backgroundWidth*(y-centreY)/denominator)+centreY-centreX*(y-currentY)/(x-centreX);
//            currentX = (int)x1-stickWidth;
//            currentY = (int)y1-stickWidth;
            currentX = (float)(centreX+Math.cos(angle*Math.PI/180)*backgroundWidth/2);
            currentY = (float)(centreY-Math.sin(angle*Math.PI/180)*backgroundWidth/2);
        }

        if(event.getAction()==MotionEvent.ACTION_DOWN){
        }else if(event.getAction()==MotionEvent.ACTION_UP){
            //摇杆复归原位
//            try {
//                drawStick(centreX, centreY);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            currentX = centreX;
            currentY = centreY;
            onOperationListener.onOperation(Reset,0,0);
        }

        return true;
    }
    protected void drawBackground(){
        Bitmap bitmap = null;
        if(context!=null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.console_back);
        }else{
            Log.v("GamePadView------->", "drawBackground获取上下文为空");
        }
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, stickWidth/2, stickWidth/2, paint);
        postInvalidate();
    }
    protected void drawStick(float left,float top){
        Bitmap bitmap = null;
        if(context!=null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.console);
        }else{
            Log.v("GamePadView------->", "drawBackground获取上下文为空");
        }
        Paint paint = new Paint();
        //Log.v("GamePadView------->", "drawStick画手柄坐标"+left+","+top+","+bitmap.getHeight());
        canvas.drawBitmap(bitmap, left - bitmap.getWidth() / 2, top - bitmap.getHeight() / 2, paint);
        canvas.drawLine(left,top,centreX,centreY,paint);
        postInvalidate();
    }


}
