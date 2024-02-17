package intro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.TransitionManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import float_widgets.Window;
import mappedvision.shortcuts.net.MainActivity;
import mappedvision.shortcuts.net.R;
import mappedvision.shortcuts.net.SearchWindowActivity;
import services.ShortcutService;
import utils.SharedPrefUtils;

public class IntroPageActivity extends AppCompatActivity {

    View rightview, leftView;
    private WindowManager windowManager;
    TextView cache;
    private View touchView;
    ViewGroup rootView;
    TextView startText_right, startText_left;
    RelativeLayout congrats;
    private GestureDetector gestureDetector, leftGesture;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_page);

        rootView = findViewById(R.id.rootView);
        leftView = findViewById(R.id.leftView);
        rightview = findViewById(R.id.rightview);
        startText_right = findViewById(R.id.startText_right);
        cache = findViewById(R.id.cache);
        congrats = findViewById(R.id.congrats);
        startText_left = findViewById(R.id.startText_left);


        // Define the height you want programmatically
        int desiredHeightInPixels = getResources().getDisplayMetrics().heightPixels / 4; // Change this to whatever height you need
        // Get the current layout params of the view
        ViewGroup.LayoutParams params = rightview.getLayoutParams(); // Assuming 'rightview' is the view you want to modify
        ViewGroup.LayoutParams leftParams = leftView.getLayoutParams(); // Assuming 'leftView' is another view you want to modify
        // Update the height property of the layout params
        params.height = desiredHeightInPixels;
        params.width = 65;
        leftParams.height = desiredHeightInPixels;
        leftParams.width = 65;

        leftView.setLayoutParams(leftParams);
        rightview.setLayoutParams(params);


        startAnimation();

        rightSwipe();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        };
    }

    private void startAnimation() {
        TransitionManager.beginDelayedTransition(rootView);
        rightview.setVisibility(View.VISIBLE);
        startText_right.setVisibility(View.VISIBLE);
    }

    private void rightSwipe() {
        // Initialize GestureDetector
        gestureDetector = new GestureDetector(this, new RightGestureListener());

        // Create a transparent overlay window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        touchView = new View(this);

        int quarterScreenHeight = getResources().getDisplayMetrics().heightPixels / 4;

        //If whole =1 what about
        // Set width to match parent and height to a fixed value for the bottom part
        int width = 20;
        int height = quarterScreenHeight;  // Adjust as needed

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width,
                height,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // Set the overlay to the bottom of the screen
        params.gravity = Gravity.BOTTOM | Gravity.END;

        windowManager.addView(touchView, params);

        // Set touch listener on the overlay view
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true; // consume the touch event
            }
        });

    }

    private void leftSwipe() {
        // Initialize GestureDetector
        leftGesture = new GestureDetector(this, new LeftGesture());

        // Create a transparent overlay window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        touchView = new View(this);

        int quarterScreenHeight = getResources().getDisplayMetrics().heightPixels / 4;

        //If whole =1 what about
        // Set width to match parent and height to a fixed value for the bottom part
        int width = 20;
        int height = quarterScreenHeight;  // Adjust as needed

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width,
                height,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // Set the overlay to the bottom of the screen
        params.gravity = Gravity.BOTTOM | Gravity.START;

        windowManager.addView(touchView, params);

        // Set touch listener on the overlay view
        touchView.setOnTouchListener((v, event) -> {
            leftGesture.onTouchEvent(event);
            return true; // consume the touch event
        });

    }

    private class RightGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            // Always return true to indicate that the down event was consumed
            Log.d("TEST SCREEN", "DOWN " + e.getY());
            return true;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            vibrate();
            TransitionManager.beginDelayedTransition(rootView);
            congrats.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> {
                TransitionManager.beginDelayedTransition(rootView);
                congrats.setVisibility(View.INVISIBLE);
                startText_right.setVisibility(View.GONE);
                rightview.setVisibility(View.GONE);
                startText_left.setVisibility(View.VISIBLE);
                leftView.setVisibility(View.VISIBLE);
                leftSwipe();
            }, 1500);

            return true;
        }
    }

    private class LeftGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            // Always return true to indicate that the down event was consumed
            Log.d("TEST SCREEN", "DOWN " + e.getY());
            return true;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            vibrate();
            TransitionManager.beginDelayedTransition(rootView);
            congrats.setVisibility(View.VISIBLE);

            //Initialize SharedPreferences
            SharedPreferences.Editor pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
            pref.putBoolean("Seen", true);
            pref.apply();

            new Handler().postDelayed(() -> {
                cache.setText(R.string.redirecting);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }, 2000);


            return true;
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            // Vibrate for 500 milliseconds
            vibrator.vibrate(30);
        }
    }


    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}