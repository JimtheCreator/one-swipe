package float_widgets;

import static android.content.Context.CROSS_PROFILE_APPS_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import java.util.Random;

import mappedvision.shortcuts.net.R;

public class SearchWindow {
    // declaring required variables
    private final Context context;
    private final View mView;
    LinearLayout search_bar;

    // Assuming that you have a reference to the main looper
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final WindowManager mWindowManager;
    private final LayoutInflater layoutInflater;
    TextView toptitle;
    RelativeLayout drop_down, close_window;
    TextView short_descr;
    ImageView savedpic;
    EditText EditText;
    RecyclerView suggestions_recyclerview, search_apps_recyclerview;
    Random r;
    Random random;
    ViewGroup root;

    Handler getHandler= new Handler();
    private WindowManager.LayoutParams mParams;


    public SearchWindow(Context context) {
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
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    // Make the underlying application window visible
                    // through any transparent parts
                    PixelFormat.TRANSLUCENT);
        }

        // getting a LayoutInflater
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflating the view with the custom layout we created
        mView = layoutInflater.inflate(R.layout.search_layout, null);

        mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        EditText = mView.findViewById(R.id.EditText);
        suggestions_recyclerview = mView.findViewById(R.id.suggestions_recyclerview);
        search_apps_recyclerview = mView.findViewById(R.id.search_apps_recyclerview);
        drop_down = mView.findViewById(R.id.drop_down);
        close_window = mView.findViewById(R.id.close_window);

        close_window.setOnClickListener(v -> openKeyboard());

        close_window.setOnClickListener(v -> close());

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

                openKeyboard();
            }
        } catch (Exception e) {
            Log.d("Error1", e.toString());
        }
    }

    public void close() {
//        TransitionManager.beginDelayedTransition(root);
//        recyclerView.setVisibility(View.GONE);
//        close.setVisibility(View.GONE);
//        search_bar.setVisibility(View.GONE);

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
            closeKeyboard();
        }

        catch (Exception e) {
            Log.e("Error2", e.toString());
        }
    }

    // Method to close keyboard
    public void closeKeyboard() {
        if (mView != null) {
            // Get the input method manager
            InputMethodManager imm = (InputMethodManager) mView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            // Close the keyboard
            imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
        }
    }

    private void openKeyboard(){
        if (mView!=null){
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
    }
}
