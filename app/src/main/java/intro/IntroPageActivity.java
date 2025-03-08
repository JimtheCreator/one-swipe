package intro;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
import android.view.animation.AccelerateDecelerateInterpolator;
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
//    private WindowManager windowManager;
    TextView cache;
    private View touchView;
    ViewGroup rootView;
    TextView startText_right;
    RelativeLayout congrats, proceeding, donewithintro;
//    private GestureDetector gestureDetector, leftGesture;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_page);

        donewithintro = findViewById(R.id.donewithintro);
        rootView = findViewById(R.id.rootView);
        leftView = findViewById(R.id.leftView);
        rightview = findViewById(R.id.rightview);
        startText_right = findViewById(R.id.startText_right);
        cache = findViewById(R.id.cache);
        congrats = findViewById(R.id.congrats);
//        done = findViewById(R.id.done);
        proceeding = findViewById(R.id.proceeding);

        startBouncing();

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

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        };

        proceeding.setOnClickListener(v -> {
            vibrate();
            TransitionManager.beginDelayedTransition(rootView);
            congrats.setVisibility(View.VISIBLE);

            proceeding.setEnabled(false);
            new Handler().postDelayed(() -> {
                proceeding.setVisibility(View.GONE);
                donewithintro.setVisibility(View.VISIBLE);
                TransitionManager.beginDelayedTransition(rootView);
                startText_right.setText("Swiping from the section shown below going rightwards, opens a drawer containing your recent and fav apps");
                congrats.setVisibility(View.INVISIBLE);
                rightview.setVisibility(View.GONE);
                leftView.setVisibility(View.VISIBLE);
            }, 1500);

        });

        donewithintro.setOnClickListener(v -> {
            vibrate();
            TransitionManager.beginDelayedTransition(rootView);
            cache.setText("You're all set :)");
            congrats.setVisibility(View.VISIBLE);
            donewithintro.setEnabled(false);
            //Initialize SharedPreferences
            SharedPreferences.Editor pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
            pref.putBoolean("Seen", true);
            pref.apply();

            new Handler().postDelayed(() -> {
                cache.setText(R.string.redirecting);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }, 2000);
        });
    }

    private void startBouncing() {
        final float scaleFactor = 1.1f; // Adjust the scaling factor as needed
        // Create ObjectAnimators for scaling
        ObjectAnimator inflate = ObjectAnimator.ofPropertyValuesHolder(
                proceeding,
                PropertyValuesHolder.ofFloat(View.SCALE_X, scaleFactor),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleFactor)
        );
        inflate.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator deflate = ObjectAnimator.ofPropertyValuesHolder(
                proceeding,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f)
        );
        deflate.setInterpolator(new AccelerateDecelerateInterpolator());

        // Create AnimatorSet
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(inflate, deflate);
        animatorSet.setDuration(2200); // Adjust the duration as needed

        // Set up listener to restart the animation when it ends
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorSet.start(); // Start the animation again when it ends
            }
        });

        // Start the animation
        animatorSet.start();


        // Create ObjectAnimators for scaling
        ObjectAnimator inflateS = ObjectAnimator.ofPropertyValuesHolder(
                donewithintro,
                PropertyValuesHolder.ofFloat(View.SCALE_X, scaleFactor),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleFactor)
        );

        inflateS.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator deflateS = ObjectAnimator.ofPropertyValuesHolder(
                donewithintro,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f)
        );
        deflateS.setInterpolator(new AccelerateDecelerateInterpolator());

        // Create AnimatorSet
        final AnimatorSet an = new AnimatorSet();
        an.playSequentially(inflateS, deflateS);
        an.setDuration(2200); // Adjust the duration as needed

        // Set up listener to restart the animation when it ends
        an.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                an.start(); // Start the animation again when it ends
            }
        });

        // Start the animation
        an.start();
    }

    private void startAnimation() {
        TransitionManager.beginDelayedTransition(rootView);
        rightview.setVisibility(View.VISIBLE);
        startText_right.setVisibility(View.VISIBLE);
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