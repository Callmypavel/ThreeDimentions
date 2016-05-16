package peacemaker.threedimentions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private TextView currentCoordinates;
    private TextView relativeCoordinates;
    private TextView stickDirection;
//    private EditText editText;
//    private Button button;
    private Boolean isStop = true;
    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener gestureListener;
    private GeneralView generalView;
    private GamePadView gamePadView;
    private Button reset;
    private Button opengl;
    private SeekBar seekBar;
    private float x;
    private float y;
    private int counter = 0;
    private float degreeα = 0;
    private float degreeβ = 0;
    private float degree = 0;
    private float proportion = 1;
    private float lastProgress = 50;
    private final static int Reset = 0x000;
    private final static int RightUp = 0x001;
    private final static int RightDown = 0x002;
    private final static int LeftUp = 0x003;
    private final static int LeftDown = 0x004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void initialize(){
        currentCoordinates = (TextView)findViewById(R.id.currentCoordinates);
        relativeCoordinates = (TextView)findViewById(R.id.relativeCoordinates);
        stickDirection = (TextView)findViewById(R.id.stickDirection);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
//        editText = (EditText)findViewById(R.id.editText);
//        button = (Button)findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                float degree = Integer.parseInt(editText.getText().toString());
//                Log.v("MainActivity------->", "initialize获取转置角" + degree);
//                //generalView.setDegree((float) (degree * Math.PI / 180));
//                isStop = false;
//                Log.v("MainActivity------->", "initialize获取转置角" +(float) (degree * Math.PI / 180));
//            }
//        });
        seekBar.setProgress(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress!=0) {
                    proportion = progress / lastProgress;
                    lastProgress = progress;
                    Log.v("MainActivity------->", "onProgressChanged缩放比例" + proportion);
                    generalView.scaling(proportion);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        generalView = (GeneralView)findViewById(R.id.GenaralView);
        reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generalView.reset();
                lastProgress = 50;
                seekBar.setProgress(50);
            }
        });
        opengl = (Button)findViewById(R.id.opengl);
        opengl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TeapotNativeActivity.class);
                startActivity(intent);
                finish();
                generalView.stopThread();
            }
        });
        generalView.setOnTouchListener(this);
//        generalView.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                switch (event.getAction()) {
//                    case DragEvent.ACTION_DRAG_STARTED:
//                        event.getX();
//                        event.getY();
//                        Log.v("GeneralView------->", "开始拖动观察x" + event.getX());
//                        Log.v("GeneralView------->", "开始拖动观察y" + event.getY());
//                        //开始拖动
//                        break;
//                    case DragEvent.ACTION_DRAG_ENTERED:
//                        //正在拖动
//                        event.getX();
//                        event.getY();
//                        Log.v("GeneralView------->", "正在拖动观察x" + event.getX());
//                        Log.v("GeneralView------->", "正在拖动观察y" + event.getY());
//                        break;
//                }
//                return false;
//            }
//        });
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(isStop) {
//                    generalView.setDegree((float) (degree * Math.PI / 180));
//                    Log.v("MainActivity------->", "initialize获取转置角" + (float) (degree * Math.PI / 180));
//                    try{
//                        Thread.sleep(1000);
//                    }catch (Exception e){
//
//                    }
//                    degree+=10;
//                }
//            }
//        });
//        thread.start();
        gamePadView = (GamePadView)findViewById(R.id.GamePadView);
        gamePadView.setOperationListener(new OnOperationListener() {
            @Override
            public void onOperation(int oprationCode,float angle,float degree) {

                switch (oprationCode) {
                    case Reset:
                        stickDirection.setText("归位" + " 角度：" + angle);
                        generalView.setSpeed(0,0,degree);
                        break;
                    case RightUp:
//                        counter+=1;
//                        if(counter>10){
//                            counter=0;
                            setSpeed((float) Math.sin(angle * Math.PI / 180),(float) Math.cos(angle * Math.PI / 180),degree);
//                        }
                        stickDirection.setText("右上" + " 角度：" + angle);

                        break;
                    case RightDown:
//                        counter+=1;
//                        if(counter>10) {
//                            counter = 0;
                            setSpeed((float) Math.sin(angle * Math.PI / 180), (float) Math.cos(angle * Math.PI / 180), degree);

//                        }
                        stickDirection.setText("右下" + " 角度：" + angle);
                        break;
                    case LeftUp:
//                        counter+=1;
//                        if(counter>10) {
//                            counter = 0;
                            setSpeed((float) Math.sin(angle * Math.PI / 180), (float) Math.cos(angle * Math.PI / 180), degree);
//                        }
                        stickDirection.setText("左上" + " 角度：" + angle);
                        break;
                    case LeftDown:
//                        counter+=1;
//                        if(counter>10) {
//                            counter = 0;
                            setSpeed((float) Math.sin(angle * Math.PI / 180), (float) Math.cos(angle * Math.PI / 180), degree);
//                        }
                        stickDirection.setText("左下" + " 角度：" + angle);

                        break;
                }
            }

        });
//        gestureListener = new GestureDetector.OnGestureListener() {
//            @Override
//            public boolean onDown(MotionEvent e) {
//                Log.v("MainActivity------->", "按下手势" );
//                return false;
//            }
//
//            @Override
//            public void onShowPress(MotionEvent e) {
//                Log.v("MainActivity------->", "ShowPress手势" );
//            }
//
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                Log.v("MainActivity------->", "单击手势" );
//                return false;
//            }
//
//            @Override
//            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                Log.v("MainActivity------->", "滚动手势" );
//                generalView.scrollEvent(e1.getX(),e1.getY(),e2.getX(),e2.getY());
//                return false;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//                Log.v("MainActivity------->", "长按手势" );
//            }
//
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                Log.v("MainActivity------->", "抛掷手势" );
//                return false;
//            }
//        };
//        gestureDetector = new GestureDetector(this,gestureListener);
//        //gestureDetector.setIsLongpressEnabled(false);
//        gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Log.v("MainActivity------->", "确认单击" );
//                return false;
//            }
//
//            @Override
//            public boolean onDoubleTap(MotionEvent e) {
//                Log.v("MainActivity------->", "双击手势" );
//                return false;
//            }
//
//            @Override
//            public boolean onDoubleTapEvent(MotionEvent e) {
//                return false;
//            }
//        });




    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        x = event.getX();
        y = event.getY();
        generalView.setxAxis(x);
        generalView.setyAxis(y);

        //Log.v("MainActivity------->", "onTouch获取坐标" + x + " " + y);
        if(y<=generalView.getyMax()) {
            currentCoordinates.setText("绝对坐标:" + x + "," + y);
            relativeCoordinates.setText("相对坐标:" + generalView.getxAxis() + "," + generalView.getyAxis());
        }
        return false;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        return gestureDetector.onTouchEvent(event);
//    }

    public void setSpeed(float degree1,float degree2,float degree3){
        if(degree1!=degreeα ||degree2!=degreeβ ||degree3!=degree
                ){
            generalView.setSpeed(degree1,degree2,degree3);
            degreeα = degree1;
            degreeβ = degree2;
            degree = degree3;
        }
    }

}
