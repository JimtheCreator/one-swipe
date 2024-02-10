package mappedvision.shortcuts.net;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.ItemDecorator;
import services.ShortcutService;
import utils.SharedPrefUtils;
import utils.UsageStatsHelper;

public class MainActivity extends AppCompatActivity {

    private static final int FRAME_RATE = 16; // Frame rate in milliseconds
    BottomSheetBehavior bottomSheetBehavior;
    MyAdapter adapter;
    int screenHeight;
    // Declare a variable to store the last slide offset
    float lastSlideOffset = 0f;
    boolean hasIllustrated  = false;
    boolean hasExecuted = false;

    TextView mostTxt, othertxt;
    OtherAdapter otherAdapter;
    ProgressBar progress;
    Handler handler = new Handler();
    RelativeLayout floatingBall, guide, close, pointer;
    LinearLayout issueslayout, battery, themainthing, displayoverapps, list, viewHolder, settings;
    ViewGroup viewGroup;
    TextView label_top, label_bottom;

    private final ActivityResultLauncher<Intent> requestOverlayPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (Settings.canDrawOverlays(MainActivity.this)) {
                                // The permission has been granted, proceed with your logic
                                TransitionManager.beginDelayedTransition(viewGroup);
                                displayoverapps.setVisibility(View.GONE);
                            } else {
                                // The user denied the permission. Handle accordingly.
                                TransitionManager.beginDelayedTransition(viewGroup);
                                displayoverapps.setVisibility(View.VISIBLE);
                            }
                        }
                    });
    RecyclerView listView, otherslist;
    NestedScrollView nest;

    @Override
    protected void onStart() {
        super.onStart();
        if (!UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), PermissionActivity.class));
        } else {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm != null && pm.isIgnoringBatteryOptimizations(getPackageName())
                    && Settings.canDrawOverlays(getApplicationContext())
                    && UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
                battery.setVisibility(View.GONE);
                displayoverapps.setVisibility(View.GONE);
                issueslayout.setVisibility(View.GONE);
                themainthing.setVisibility(View.VISIBLE);
                nest.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.offwhite_corners));

                // The permission is granted
                Intent intent = new Intent(getApplicationContext(), ShortcutService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);


        SharedPreferences pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        hasIllustrated = pref.getBoolean("Seen", false);

        screenHeight = getResources().getDisplayMetrics().heightPixels;
        guide = findViewById(R.id.guide);
        displayoverapps = findViewById(R.id.displayoverapps);
        pointer = findViewById(R.id.pointer);
        settings = findViewById(R.id.settings);
        close = findViewById(R.id.close);
        label_top = findViewById(R.id.label_top);
        label_bottom = findViewById(R.id.label_bottom);
        viewHolder = findViewById(R.id.viewHolder);
        progress = findViewById(R.id.progress);
        othertxt = findViewById(R.id.othertxt);
        otherslist = findViewById(R.id.otherslist);
        mostTxt = findViewById(R.id.mostTxt);
        viewGroup = findViewById(R.id.mainroot);
        themainthing = findViewById(R.id.themainthing);
        battery = findViewById(R.id.battery);
        list = findViewById(R.id.list);
        nest = findViewById(R.id.nest);
        issueslayout = findViewById(R.id.issueslayout);
        floatingBall = findViewById(R.id.bouncingLayout);
        final ViewGroup viewGroup = findViewById(R.id.mainroot);
        final LinearLayout list = findViewById(R.id.list);
        final View viewdis = findViewById(R.id.viewdis);
        NestedScrollView nest = findViewById(R.id.nest);

        // Get the list view
        listView = findViewById(R.id.listview);
        listView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listView.setLayoutManager(new GridLayoutManager(this, 3));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        listView.addItemDecoration(new ItemDecorator(3, spacingInPixels, true));

        // Get the list view
        otherslist = findViewById(R.id.otherslist);
        otherslist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        otherslist.setLayoutManager(new GridLayoutManager(this, 4));
        otherslist.addItemDecoration(new ItemDecorator(4, spacingInPixels, true));


        bottomSheetBehavior = BottomSheetBehavior.from(nest);
        final ImageView stretch = findViewById(R.id.stretch);
        final float scaleFactor = 1.1f; // Adjust the scaling factor as needed

        // Create ObjectAnimators for scaling
        ObjectAnimator inflate = ObjectAnimator.ofPropertyValuesHolder(
                floatingBall,
                PropertyValuesHolder.ofFloat(View.SCALE_X, scaleFactor),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleFactor)
        );
        inflate.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator deflate = ObjectAnimator.ofPropertyValuesHolder(
                floatingBall,
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
                stretch,
                PropertyValuesHolder.ofFloat(View.SCALE_X, scaleFactor),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleFactor)
        );
        inflateS.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator deflateS = ObjectAnimator.ofPropertyValuesHolder(
                stretch,
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

        list.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        float x = 19;
        float y = 22;

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    TransitionManager.beginDelayedTransition(viewGroup);
                    viewdis.setVisibility(View.VISIBLE);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    TransitionManager.beginDelayedTransition(viewGroup);
                    viewdis.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d("SCREEN", " FLOAT "+ slideOffset);
//                if (slideOffset > 0.5) {
//                    // The bottom sheet is being dragged upwards
//                    TransitionManager.beginDelayedTransition(viewGroup);
//                    label_top.setTextSize(y);
//                    label_bottom.setVisibility(View.VISIBLE);
//                    settings.setVisibility(View.VISIBLE);
//                } else if (slideOffset < 0.5) {
//                    // The bottom sheet is being dragged downwards
//                    TransitionManager.beginDelayedTransition(viewGroup);
//                    label_top.setTextSize(x);
//                    label_bottom.setVisibility(View.GONE);
//                    settings.setVisibility(View.GONE);
//                }
//
//                // Update the last slide offset
//                lastSlideOffset = slideOffset;
            }
        });

        floatingBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        battery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When you need to start the battery optimization settings activity
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
            }
        });

        displayoverapps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When you need to start the battery optimization settings activity
                checkAndRequestOverlayPermission();
            }
        });



        // This callback will only be called when MyActivity is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    nest.smoothScrollTo(0, 0);
                } else {
                    finishAffinity();
                }
            }
        };


        getOnBackPressedDispatcher().addCallback(this, callback);

        puthingstonull();

        settings.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SettingsActivity.class)));
    }


    private void checkAndRequestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));

        requestOverlayPermissionLauncher.launch(intent);
    }

    private void puthingstonull() {
        // The permission is already granted, proceed with your logic
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            // Call the method that requires API level 21 or higher
            List<UsageStats> usageStatsList = getUsageStatsList(MainActivity.this);
            List<AppInfo> appList = new ArrayList<>();
            Set<String> addedPackages = new HashSet<>();

            List<UsageStats> otherstats = getOverallStats(MainActivity.this);
            List<AppInfo> appListwo = new ArrayList<>();
            Set<String> addedPackagestwo = new HashSet<>();

            // Do something here on the main thread
            for (UsageStats usageStats : usageStatsList) {
                try {
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(usageStats.getPackageName(), 0);
                    boolean isUserApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
                    boolean isUpdatedSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
                    if ((isUserApp || isUpdatedSystemApp)
                            && !usageStats.getPackageName().equals(getPackageName())
                            && !addedPackages.contains(usageStats.getPackageName())) {
                        String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                        Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
                        appList.add(new AppInfo(appName, usageStats.getPackageName(), appIcon));
                        addedPackages.add(usageStats.getPackageName());
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }


            // Create a custom adapter
            adapter = new MyAdapter(appList, MainActivity.this);

            for (UsageStats usageStats : otherstats) {
                try {
                    Log.d("BIGG", "LISTS " + appList);
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(usageStats.getPackageName(), 0);
                    boolean isUserApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
                    boolean isUpdatedSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
                    if ((isUserApp || isUpdatedSystemApp)
                            && !usageStats.getPackageName().equals(getPackageName())
                            && !addedPackagestwo.contains(usageStats.getPackageName())
                            && !appList.contains(usageStats.getPackageName())) {
                        String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                        Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
                        appListwo.add(new AppInfo(appName, usageStats.getPackageName(), appIcon));
                        addedPackagestwo.add(usageStats.getPackageName());
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            otherAdapter = new OtherAdapter(appListwo, MainActivity.this);

            hasExecuted = true;

            runOnUiThread(() -> {
                viewHolder.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                mostTxt.setText(String.valueOf(appList.size()));
                TransitionManager.beginDelayedTransition(viewGroup);
                listView.setAdapter(adapter);
                otherslist.setAdapter(otherAdapter);
                othertxt.setText(String.valueOf(appListwo.size()));
                otherAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            });
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private List<UsageStats> getUsageStatsList(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        List<UsageStats> unusedAppsStats = new ArrayList<>();
        for (UsageStats usageStats : usageStatsList) {
            if (usageStats.getTotalTimeInForeground() >= 120000) {
                unusedAppsStats.add(usageStats);
            }
        }

        return unusedAppsStats;
    }

    private List<UsageStats> getOverallStats(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        // Return the complete list of usage stats (including used and unused apps)
        return usageStatsList;
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            // Vibrate for 500 milliseconds
            vibrator.vibrate(30);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

            if (pm != null && pm.isIgnoringBatteryOptimizations(getPackageName())) {
                TransitionManager.beginDelayedTransition(viewGroup);
                battery.setVisibility(View.GONE);
            }

            if (Settings.canDrawOverlays(getApplicationContext())) {
                TransitionManager.beginDelayedTransition(viewGroup);
                displayoverapps.setVisibility(View.GONE);
            }

            if (pm != null && pm.isIgnoringBatteryOptimizations(getPackageName()) && Settings.canDrawOverlays(getApplicationContext())) {
                if (!hasIllustrated){
                    guide.setVisibility(View.VISIBLE);
                    // Define the animation
                    TranslateAnimation bounceAnimation = new TranslateAnimation(0, 0, 0, 100);
                    bounceAnimation.setDuration(1000);
                    bounceAnimation.setInterpolator(new BounceInterpolator());
                    bounceAnimation.setRepeatCount(Animation.INFINITE);
                    bounceAnimation.setRepeatMode(Animation.REVERSE);

                    // Start the animation
                    pointer.startAnimation(bounceAnimation);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TransitionManager.beginDelayedTransition(viewGroup);
                            close.setVisibility(View.VISIBLE);
                        }
                    }, 2000);

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Initialize SharedPreferences
                            SharedPreferences.Editor pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
                            pref.putBoolean("Seen", true);
                            pref.apply();

                            guide.setVisibility(View.GONE);
                        }
                    });

                }

                else {
                    guide.setVisibility(View.GONE);
                }

                TransitionManager.beginDelayedTransition(viewGroup);
                battery.setVisibility(View.GONE);
                displayoverapps.setVisibility(View.GONE);
                issueslayout.setVisibility(View.GONE);
                themainthing.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static class AppInfo {
        String name, packagename;
        Drawable icon;

        AppInfo(String name, String packagename, Drawable icon) {
            this.name = name;
            this.packagename = packagename;
            this.icon = icon;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        List<AppInfo> appn;
        Context context;

        // Constructor to initialize the adapter with data


        public MyAdapter(List<AppInfo> appn, Context context) {
            this.appn = appn;
            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_layout, parent, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AppInfo appInfo = appn.get(position);

//            holder.maincontainer.setBackgroundColor(ContextCompat.getColor(context, R.color.invicibleColor));

            //holder.textView.setText(appInfo.name);
            holder.appIcon.setImageDrawable(appInfo.icon);
            String textToDisplay = appInfo.name;

            // Set the maximum length to 5 characters
            int maxLength = 4;

            // Check if the text exceeds the maximum length
            if (textToDisplay.length() > maxLength) {
                // Truncate the text and append ellipsis
                String truncatedText = textToDisplay.substring(0, maxLength) + "...";
                holder.appname.setText(truncatedText);
            } else {
                // Display the original text if it doesn't exceed the maximum length
                holder.appname.setText(textToDisplay);
            }

            checkstate(holder.checked, appInfo.packagename, context);


            Log.d("List of apps", ""+ SharedPrefUtils.getStringArray(context));
            holder.itemView.setOnClickListener(v -> {
                Log.d("List of apps", ""+ SharedPrefUtils.getStringArray(context));
                if (SharedPrefUtils.getStringArray(context).contains(appInfo.packagename)) {
                    removeapp(appInfo.packagename, holder.checked);
                }else {
                    uploadApptoServer(appInfo.packagename, holder.checked, context);
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return appn.size();
        }

        // Provide a reference to the views for each data item
        class ViewHolder extends RecyclerView.ViewHolder {
            //TextView textView;
            ImageView appIcon, checked;
            TextView appname, state;
//            RelativeLayout maincontainer;

            ViewHolder(View itemView) {
                super(itemView);
//                textView = itemView.findViewById(R.id.appName);
                appIcon = itemView.findViewById(R.id.appIcon);
//                maincontainer = itemView.findViewById(R.id.maincontainer);
                checked = itemView.findViewById(R.id.checked);
                appname = itemView.findViewById(R.id.appname);
//                state = itemView.findViewById(R.id.state);
            }
        }

        private void uploadApptoServer(String packagename, ImageView checked, Context context) {
            SharedPrefUtils.addItem(context, packagename);
            checked.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.blue_checkbox));
        }

        private void checkstate(ImageView checked, String packagename,Context context) {
            if (SharedPrefUtils.getStringArray(context) != null){
                if (SharedPrefUtils.getStringArray(context).contains(packagename)){
                    checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.blue_checkbox));
                }else {
                    checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_check_box_outline_blank_24));
                }
            }else {
                checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_check_box_outline_blank_24));
            }

        }

        private void removeapp(String packagename, ImageView checked) {
            checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_check_box_outline_blank_24));
            SharedPrefUtils.removeItem(getApplicationContext(), packagename);
        }

    }

    private class OtherAdapter extends RecyclerView.Adapter<OtherAdapter.ViewHolder> {

        List<AppInfo> appn;
        Context context;

        // Constructor to initialize the adapter with data

        public OtherAdapter(List<AppInfo> appn, Context context) {
            this.appn = appn;
            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_layout, parent, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AppInfo appInfo = appn.get(position);

//            holder.maincontainer.setBackgroundColor(ContextCompat.getColor(context, R.color.invicibleColor));

            //holder.textView.setText(appInfo.name);
            holder.appIcon.setImageDrawable(appInfo.icon);
            String textToDisplay = appInfo.name;

            // Set the maximum length to 5 characters
            int maxLength = 2;

            // Check if the text exceeds the maximum length
            if (textToDisplay.length() > maxLength) {
                // Truncate the text and append ellipsis
                String truncatedText = textToDisplay.substring(0, maxLength) + "..";
                holder.appname.setText(truncatedText);
            } else {
                // Display the original text if it doesn't exceed the maximum length
                holder.appname.setText(textToDisplay);
            }

            checkstate(holder.checked, appInfo.packagename, context);


            holder.itemView.setOnClickListener(v -> {
                Log.d("List of apps", ""+ SharedPrefUtils.getStringArray(context));
                if (SharedPrefUtils.getStringArray(context).contains(appInfo.packagename)) {
                    removeapp(appInfo.packagename, holder.checked);
                }else {
                    uploadApptoServer(appInfo.packagename, holder.checked, context);
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return appn.size();
        }

        // Provide a reference to the views for each data item
        class ViewHolder extends RecyclerView.ViewHolder {
            //TextView textView;
            ImageView appIcon, checked;
            TextView appname;
//            RelativeLayout maincontainer;

            ViewHolder(View itemView) {
                super(itemView);
//                textView = itemView.findViewById(R.id.appName);
                appIcon = itemView.findViewById(R.id.appIcon);
//                maincontainer = itemView.findViewById(R.id.maincontainer);
                checked = itemView.findViewById(R.id.checked);
                appname = itemView.findViewById(R.id.appname);
            }
        }

        private void uploadApptoServer(String packagename, ImageView checked, Context context) {
            SharedPrefUtils.addItem(context, packagename);
            checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.blue_checkbox));
        }

        private void checkstate(ImageView checked, String packagename,Context context) {
            if (SharedPrefUtils.getStringArray(context) != null){
                if (SharedPrefUtils.getStringArray(context).contains(packagename)){
                    checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.blue_checkbox));
                }else {
                    checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_check_box_outline_blank_24));
                }
            }else {
                checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_check_box_outline_blank_24));
            }
        }

        private void removeapp(String packagename, ImageView checked) {
            checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_check_box_outline_blank_24));
            SharedPrefUtils.removeItem(getApplicationContext(), packagename);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}