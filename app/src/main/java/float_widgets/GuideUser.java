package float_widgets;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.transition.TransitionManager;

import mappedvision.shortcuts.net.R;

public class GuideUser {
    // declaring required variables
    private final Context context;
    private final View mView;
    // Assuming that you have a reference to the main looper
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final WindowManager mWindowManager;
    private final LayoutInflater layoutInflater;
    TextView instructions;
    LinearLayout base_root;
    private WindowManager.LayoutParams mParams;

    public GuideUser(Context context) {
        this.context = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = new WindowManager.LayoutParams(
                    // Shrink the window to wrap the content rather
                    // than filling the screen
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                    // Display it on top of other application windows
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    // Don't let it grab the input focus
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,// Make the underlying application window visible // through any transparent parts
                    PixelFormat.TRANSLUCENT);
        }

        // getting a LayoutInflater
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflating the view with the custom layout we created
        mView = layoutInflater.inflate(R.layout.guide_user_layout, null);


        mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        CardView dismisspage = mView.findViewById(R.id.dismisspage);


        dismisspage.setOnClickListener(v->{
            close();
        });

        // Define the position of the
        // window within the screen
        mParams.gravity = Gravity.FILL_VERTICAL;
        mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
    }

    public void open() {
        try {
            // check if the view is already
            // inflated or present in the window
            if (mView.getWindowToken() == null) {
                if (mView.getParent() == null) {
                    mWindowManager.addView(mView, mParams);
                }
            }
        } catch (Exception e) {
            Log.d("Error1", e.toString());
        }
    }

    public void close() {
        vibrate();
        try {
            Log.d("DEBUG", "close method called");
            ViewParent parent = mView.getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup parentViewGroup = (ViewGroup) parent;
                parentViewGroup.removeAllViews();
            }

            // remove the view from the window
            ((WindowManager) context.getSystemService(WINDOW_SERVICE)).removeView(mView);
            // invalidate the view
            mView.invalidate();
        } catch (Exception e) {
            Log.e("Error2", e.toString());
        }

    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            // Vibrate for 500 milliseconds
            vibrator.vibrate(30);
        }
    }


}
