package peacemaker.threedimentions;

import android.app.Activity;
import android.app.NativeActivity;
import android.app.Notification;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by 请叫我保尔 on 2016/1/17.
 */
public class DisplayActivity extends NativeActivity {
    private PopupWindow popupWindow;
    private DisplayActivity activity;
    private TextView label;
    static {
        System.loadLibrary("ThreeDrawer");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }
    public void showUI()
    {
        if( popupWindow != null )
            return;

        activity = this;

        this.runOnUiThread(new Runnable()  {
            @Override
            public void run()  {
                LayoutInflater layoutInflater
                        = (LayoutInflater)getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.widgets, null);
                popupWindow = new PopupWindow(
                        popupView,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);

                LinearLayout mainLayout = new LinearLayout(activity);
                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                activity.setContentView(mainLayout, params);

                // Show our UI over NativeActivity window
                popupWindow.showAtLocation(mainLayout, Gravity.TOP | Gravity.LEFT, 10, 10);
                popupWindow.update();

                label = (TextView)popupView.findViewById(R.id.fpsTextView);

            }});
    }
    public void updateFPS(final float fFPS)
    {
        if( label == null )
            return;

        activity = this;
        this.runOnUiThread(new Runnable()  {
            @Override
            public void run()  {
                label.setText(String.format("%2.2f FPS", fFPS));

            }});
    }
}
