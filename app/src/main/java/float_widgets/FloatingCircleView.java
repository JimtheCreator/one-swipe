package float_widgets;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class FloatingCircleView extends View {

    public FloatingCircleView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Handle touch event (e.g., back button press)
            // Send a broadcast or perform any actions as needed
        }
        return true; // Consume the event
    }
}

