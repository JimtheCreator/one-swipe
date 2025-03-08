package services;

import android.accessibilityservice.AccessibilityService;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import mappedvision.shortcuts.net.R;

public class TapScrollAccessibility extends AccessibilityService {

    private static final int NOTIFICATION_ID = 123;
    private static final String CHANNEL_ID = "tapScroll";
    private static final CharSequence CHANNEL_NAME = "Tap Scroll";
    private static final String CHANNEL_DESCRIPTION = "Monitoring screen";
    GestureDetector gestureDetector;
    ScrollToTop scrollToTop;
    private WindowManager windowManager;
    private View overlayView;

//    AccessibilityNodeInfo rootNode;
    private static void traverseViewHierarchy(AccessibilityNodeInfo node) {
        if (node == null) return;

        // Check if the current node is scrollable
        if (node.isScrollable()) {
            // Handle the scrollable view
            handleScrollableView(node);
        }

        // Recursively traverse the children of the current node
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                traverseViewHierarchy(child);
//                child.recycle();  // Recycle the child node here
            }
        }
    }

    private static void handleScrollableView(AccessibilityNodeInfo node) {
        // Check for common scrollable view types
        if (node.getClassName().toString().equals("android.widget.ScrollView") ||
                node.getClassName().toString().equals("androidx.viewpager.widget.ViewPager") ||
                node.getClassName().toString().equals("androidx.recyclerview.widget.RecyclerView") ||
                node.getClassName().toString().equals("android.widget.ListView") ||
                node.getClassName().toString().equals("android.webkit.WebView") ||
                node.getClassName().toString().equals("android.support.v7.widget.RecyclerView") ||
                node.getClassName().toString().equals("android.view.View")) {
            // Scroll to the top
            new ScrollToTop.ScrollThread(node).start();

            Log.d("GMAIL", "Actuality " + node.getClassName().toString());
        } else {
            Log.d("GMAIL", "Style " + node.getClassName().toString());
        }
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        showForegroundNotification();
        scrollToTop = new ScrollToTop(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        gestureDetector = new GestureDetector(this, new ScrollToTop(TapScrollAccessibility.this));

        // Initialize GestureDetector in onServiceConnected
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            addOverlayView();
        }

    }

    private void showForegroundNotification() {
        // Create Notification Channel on devices running Android 8.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Build the notification using NotificationCompat
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Tap Scroll")
                .setContentText("Tap to configure or disable.")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your notification icon
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)) // Replace with your app's launcher icon
                .setAutoCancel(false);

        // Use FLAG_MUTABLE if the PendingIntent needs to be mutable
        int flags = PendingIntent.FLAG_IMMUTABLE;

        // Intent for the accessibility settings
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flags);
        builder.setContentIntent(pendingIntent);

        // Build the notification
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;


        // Start the service in the foreground
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addOverlayView() {
        WindowManager.LayoutParams params = null;
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR, // Flags to position the layout relative to the screen
                    PixelFormat.TRANSLUCENT);
        }


        // Set the overlay to the bottom of the screen
        assert params != null;
        params.gravity = Gravity.TOP | Gravity.END;

        windowManager.addView(overlayView, params);

        overlayView.setOnTouchListener((v, event) -> {
            // Handle touch events on the overlay view, including touches on the status bar
            if (gestureDetector != null) {
                vibrate();
//                v.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
                return gestureDetector.onTouchEvent(event);
            }

            return false; // Pass touch event further if gestureDetector is null or doesn't handle the event
        });


    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());

        // Use FLAG_IMMUTABLE for immutable PendingIntent
        int flags = PendingIntent.FLAG_IMMUTABLE;

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1,
                restartService, flags);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), pendingIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove overlay view
        removeOverlayView();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            // Vibrate for 500 milliseconds
            vibrator.vibrate(20);
        }
    }

    private void removeOverlayView() {
        if (windowManager != null && overlayView != null) {
            windowManager.removeView(overlayView);
        }
    }

    private static class ScrollToTop implements GestureDetector.OnGestureListener {
        private final AccessibilityService service;

        ScrollToTop(AccessibilityService service) {
            this.service = service;
        }


        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {
            // Do something when the button is tapped once
            AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
            if (rootNode != null) {
                traverseViewHierarchy(rootNode);
                rootNode.recycle();
            }

            return true;
        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(@NonNull MotionEvent e) {

        }

        @Override
        public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {

        }

        @Override
        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }


        private static class ScrollThread extends Thread {
            private final AccessibilityNodeInfo node;

            ScrollThread(AccessibilityNodeInfo node) {
                this.node = node;
            }

            @Override
            public void run() {
                // Make sure node is still available
                if (node != null) {
                    // Keep scrolling as long as the view can still scroll further backward
                    while (node.refresh() && node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)) {
                        try {
                            // Sleep for a short amount of time between scroll actions
                            Thread.sleep(20);  // Adjust this value to control the speed
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    node.recycle();
                }
            }
        }

    }

}


