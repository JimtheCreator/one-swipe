package services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import float_widgets.Window;
import mappedvision.shortcuts.net.R;
import mappedvision.shortcuts.net.SearchWindowActivity;
import utils.PreforSearch;
import utils.SharedPrefUtils;

public class ShortcutService extends Service {
    private static final String SIZE_NAME = "SeekBar";

    private static final String SIZE_KEY = "size";
    private static final String CHANNEL_ID = "intercept";
    String seekBarprogress;
    int width;
    Long lastEventTime;
    String name;
    Handler hh = new Handler();
    Handler handler = new Handler();
    //    public static boolean isTRIGGERED = false;
    List<String> interveneApps = new ArrayList<>();
    SharedPreferences prefs;
    private WindowManager leftwindowManager, rightwindowManager;
    private View righttouchView, leftTouchView;
    private GestureDetector gestureDetector, leftGesture;
    private HashMap<String, Handler> resetHandlers = new HashMap<>();
    private UsageStatsManager usageStatsManager;
    private HashMap<String, Long> lastBackgroundEvent = new HashMap<>();
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private HashMap<String, Long> lastForegroundEvent = new HashMap<>();

    View view;
    @Override
    public void onCreate() {
        super.onCreate();
        // Your task to run every second
        prefs = getSharedPreferences(SIZE_NAME, MODE_PRIVATE);

        // Start the periodic task
        seekBarprogress = prefs.getString(SIZE_KEY, "none");


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

        rightSwipe();
        leftSwipe();
    }


    private void rightSwipe() {
        int quarterScreenHeight = getResources().getDisplayMetrics().heightPixels / 4;

        if (seekBarprogress == null) {
            // Handle the case when seekBarprogress is null
            // You may initialize it with a default value or perform other appropriate actions
            seekBarprogress = "none";
        }

        if (seekBarprogress.equals("none")) {
            width = 50;
        } else {
            width = Integer.parseInt(seekBarprogress);
        }

        // Initialize GestureDetector
        gestureDetector = new GestureDetector(this, new MyGestureListener());

        // Create a transparent overlay window
        rightwindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        righttouchView = new View(this);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width,
                quarterScreenHeight,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // Set the overlay to the bottom of the screen
        params.gravity = Gravity.BOTTOM | Gravity.END;

        rightwindowManager.addView(righttouchView, params);

        // Set touch listener on the overlay view
        righttouchView.setOnTouchListener((v, event) -> {
            if (gestureDetector != null) {
                gestureDetector.onTouchEvent(event);
                Log.d("LOG", "RIGHT " + width);
            }

            return false; // consume the touch event
        });
    }

    private void leftSwipe() {
        int quarterScreenHeight = getResources().getDisplayMetrics().heightPixels / 4;

        if (seekBarprogress == null) {
            // Handle the case when seekBarprogress is null
            // You may initialize it with a default value or perform other appropriate actions
            seekBarprogress = "none";
        }

        if (seekBarprogress.equals("none")) {
            width = 50;
        } else {
            width = Integer.parseInt(seekBarprogress);
        }


        // Initialize GestureDetector
        leftGesture = new GestureDetector(this, new AnotherGesture());

        // Create a transparent overlay window
        leftwindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        leftTouchView = new View(this);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width,
                quarterScreenHeight,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // Set the overlay to the bottom of the screen
        params.gravity = Gravity.BOTTOM | Gravity.START;

        leftwindowManager.addView(leftTouchView, params);

        // Set touch listener on the overlay view
        leftTouchView.setOnTouchListener((v, event) -> {
            if (leftGesture != null) {
                leftGesture.onTouchEvent(event);
                Log.d("LOG", "LEFT " + width);
            }
            return false; // consume the touch event
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        }else {
            startForeground(1, notification);
        }


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

        // Remove the overlay view
        if (leftwindowManager != null && rightwindowManager != null && leftTouchView != null && righttouchView != null) {
            // Unregister gesture listeners before removing the view
            leftTouchView.setOnTouchListener(null); // Remove the touch listener
            righttouchView.setOnTouchListener(null); // Remove the touch listener
            gestureDetector = null; // Release reference to the gesture detector
            leftGesture = null; // Release reference to the left gesture detector
            leftwindowManager.removeView(leftTouchView);
            rightwindowManager.removeView(righttouchView);
        }

        if (seekBarprogress != null) {
            if (seekBarprogress.equals("0")) {
                PreforSearch.removeAll(getApplicationContext());
                SharedPrefUtils.removeAll(getApplicationContext());
            }
        }

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (!seekBarprogress.equals("0")) {
            Intent restartService = new Intent(getApplicationContext(), this.getClass());
            restartService.setPackage(getPackageName());

            // Use FLAG_IMMUTABLE for immutable PendingIntent
            int flags = PendingIntent.FLAG_IMMUTABLE;

            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1,
                    restartService, flags);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), pendingIntent);

        }

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
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


    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
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

            Log.d("TEST SCREEN", "FLING " + e2.getY() + "" + e1.getY());
            SharedPreferences pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
            boolean hasIllustrated = pref.getBoolean("Seen", false);

            if (!hasIllustrated) {
                showToast("Shortcut activated");
                // Initialize SharedPreferences
                // Initialize SharedPreferences
                SharedPreferences.Editor sha = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
                sha.putBoolean("Seen", true);
                sha.apply();

            } else {
                if (SharedPrefUtils.getAppInfoList(ShortcutService.this).size() == 0) {
                    showToast("No favourites saved");
                } else {
                    try {
                        Intent intent = new Intent(ShortcutService.this, SearchWindowActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle the exception appropriately, perhaps by logging or displaying an error message.
                    }

                    //Close your window here
//                    SearchWindow searchWindow = new SearchWindow(ShortcutService.this);
//                    searchWindow.open();
                }
            }

//            if (e1.getY() > 0.8 * screenHeight && velocityY > 0) {
//                showToast();
//            }

            return true;
        }
    }

    private class AnotherGesture extends GestureDetector.SimpleOnGestureListener {
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
        public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            vibrate();
            SharedPreferences pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
            boolean hasIllustrated = pref.getBoolean("Seen", false);

            if (!hasIllustrated) {
                showToast("Shortcut activated");
                // Initialize SharedPreferences
                // Initialize SharedPreferences
                SharedPreferences.Editor sha = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
                sha.putBoolean("Seen", true);
                sha.apply();

            } else {
                if (SharedPrefUtils.getAppInfoList(ShortcutService.this).size() == 0) {
                    showToast("No favourites saved");
                } else {
                    Window window = new Window(ShortcutService.this);
                    window.open();
                }
            }

//            if (e1.getY() > 0.8 * screenHeight && velocityY > 0) {
//                showToast();
//            }

            return true;
        }
    }
}
