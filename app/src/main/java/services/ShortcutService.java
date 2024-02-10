package services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.security.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import float_widgets.Window;
import mappedvision.shortcuts.net.R;
import utils.SharedPrefUtils;

public class ShortcutService extends Service {
    private static final int CHECK_INTERVAL = 1000; // Check every second
    private static final String CHANNEL_ID = "intercept";
    public static boolean continueInstagram = false;
    Long lastEventTime;
    private WindowManager windowManager;
    private View touchView;
    private GestureDetector gestureDetector;
    String name;
    Handler hh = new Handler();
    Handler handler = new Handler();
    //    public static boolean isTRIGGERED = false;
    List<String> interveneApps = new ArrayList<>();
    private HashMap<String, Handler> resetHandlers = new HashMap<>();
    private UsageStatsManager usageStatsManager;
    private HashMap<String, Long> lastBackgroundEvent = new HashMap<>();

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private HashMap<String, Long> lastForegroundEvent = new HashMap<>();

    WindowManager.LayoutParams params;


    @Override
    public void onCreate() {
        super.onCreate();
        usageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, ShortcutService.class);
        alarmIntent = PendingIntent.getService(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );


        createNotificationChannel();
        Log.d("STARTED", "EXCELLENCE");


        registerTouch();
    }

    private void registerTouch() {
        // Initialize GestureDetector
        gestureDetector = new GestureDetector(this, new MyGestureListener());

        // Create a transparent overlay window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        touchView = new View(this);

        // Set width to match parent and height to a fixed value for the bottom part
        int width = 250;
        int height = 250;  // Adjust as needed

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
        params.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;

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


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Gesture Monitor";
            String description = "This helps monitor your gestures when interacting with apps";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Detecting gestures")
                .setContentText("Monitoring gesture movement...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = createNotification();
        startForeground(1, notification);

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hh.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
        // Remove the view when the service is destroyed
//        if (windowManager != null && touchOverlayView != null) {
//            windowManager.removeView(touchOverlayView);
//        }

        // Remove the overlay view
        if (windowManager != null && touchView != null) {
            windowManager.removeView(touchView);
        }
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
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            // Always return true to indicate that the down event was consumed
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // Handle fling gesture here
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            Log.d("TEST SCREEN", ""+e2.getY());
            if (e2.getY() >= 90){
                SharedPreferences pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
                boolean hasIllustrated = pref.getBoolean("Seen", false);

                if (!hasIllustrated){
                    showToast("Shortcut activated");
                    // Initialize SharedPreferences
                    // Initialize SharedPreferences
                    SharedPreferences.Editor sha = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
                    sha.putBoolean("Seen", true);
                    sha.apply();

                }else {
                    if (SharedPrefUtils.getStringArray(ShortcutService.this).size() == 0){
                        showToast("No favourites saved");
                    }else {
                        Window window = new Window(ShortcutService.this);
                        window.open();
                    }
                }
            }

//            if (e1.getY() > 0.8 * screenHeight && velocityY > 0) {
//                showToast();
//            }

            return true;
        }
    }


    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
