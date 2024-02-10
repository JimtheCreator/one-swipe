package float_widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.core.content.ContextCompat;

import mappedvision.shortcuts.net.R;

public class FloatingCircleView extends View {
    private int mX, mY;
    private int mDirectionX = 10;
    private int mDirectionY = 10;
    private Drawable mDrawable;
    private ObjectAnimator animator = null;

    public FloatingCircleView(Context context) {
        super(context);
        mDrawable = ContextCompat.getDrawable(context, R.drawable.circle_home_screen);
        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.setBounds(mX, mY, mX + mDrawable.getIntrinsicWidth(), mY + mDrawable.getIntrinsicHeight());
        mDrawable.draw(canvas);
        if (mX < 0 || mX > getWidth() - mDrawable.getIntrinsicWidth()) {
            mDirectionX = -mDirectionX;
        }
        if (mY < 0 || mY > getHeight() - mDrawable.getIntrinsicHeight()) {
            mDirectionY = -mDirectionY;
        }
        mX = mX + mDirectionX;
        mY = mY + mDirectionY;
    }

    private void startAnimation() {
        animator = ObjectAnimator.ofFloat(this, "alpha", 1.0f);
        animator.setDuration(30); // duration of the animation in milliseconds
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                invalidate();
            }
        });
        animator.start();
    }
}
