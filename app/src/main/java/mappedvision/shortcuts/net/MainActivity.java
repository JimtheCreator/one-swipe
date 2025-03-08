package mappedvision.shortcuts.net;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import intro.IntroPageActivity;
import model.ItemDecorator;
import model.JustInfoApps;
import model.SearchedApp;
import new_ui.HomeActivity;
import services.ShortcutService;
import utils.PreforSearch;
import utils.SharedPrefUtils;
import utils.UsageStatsHelper;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String THEME_KEY = "theme";
    private static final String SIZE_NAME = "SeekBar";
    private static final String SIZE_KEY = "size";
    private static final String UI_THEME = "Theme";
    private static final String UI_KEY = "code";
    BottomSheetBehavior bottomSheetBehavior;

    FirebaseAnalytics firebaseAnalytics;
    MyAdapter adapter;
    ImageView stretch;
    int screenHeight;
    boolean hasIllustrated;
    boolean hasExecuted = false;
    boolean isChecked;
    TextView mostTxt, othertxt, most;
    OtherAdapter otherAdapter;
    ProgressBar progress;
    Handler handler = new Handler();
    RelativeLayout floatingBall;
    LinearLayout issueslayout, battery, themainthing, displayoverapps, list, viewHolder, settings, holder, oko;
    ViewGroup viewGroup;

    private final ActivityResultLauncher<Intent> requestOverlayPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
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

    CoordinatorLayout mainroot;
    TextView label_top, label_bottom;
    RecyclerView listView, otherslist;
    NestedScrollView nest;
    String seekBarprogress;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        if (!UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), PermissionActivity.class));
        } else {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm != null && pm.isIgnoringBatteryOptimizations(getPackageName())
                    && Settings.canDrawOverlays(getApplicationContext())
                    && UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
                if (state()){
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                }else {
                    onResume();
                    applyTheme();
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


        SharedPreferences prefs = getSharedPreferences(SIZE_NAME, MODE_PRIVATE);
        seekBarprogress = prefs.getString(SIZE_KEY, "none");


        SharedPreferences pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        hasIllustrated = pref.getBoolean("Seen", false);

        SharedPreferences preference = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        isChecked = preference.getBoolean("Checked", false);

        most = findViewById(R.id.most);
        oko = findViewById(R.id.oko);
        holder = findViewById(R.id.holder);
        mainroot = findViewById(R.id.mainroot);
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        displayoverapps = findViewById(R.id.displayoverapps);
        settings = findViewById(R.id.settings);
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
        stretch = findViewById(R.id.stretch);
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
                Log.d("SCREEN", " FLOAT " + slideOffset);
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

        displayoverapps.setOnClickListener(v -> {
            // When you need to start the battery optimization settings activity
            checkAndRequestOverlayPermission();
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

        settings.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SettingsActivity.class)));


    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkThemeEnabled = prefs.getBoolean(THEME_KEY, false);

        if (darkThemeEnabled) {
            changeTHEME();
        } else {
            nest.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.offwhite_corners));
        }
    }

    private void changeTHEME() {
        // Change status bar color
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.niceBlack)); // Change to your desired color
        window.setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        View view = getWindow().getDecorView();

        if (view != null) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        TextView settingstxt = findViewById(R.id.settingstxt);
        TextView other_apps = findViewById(R.id.other_apps);
        TextView hold = findViewById(R.id.hold);
        LinearLayout theme = findViewById(R.id.theme);
        ImageView settingtype = findViewById(R.id.settingtype);
        RelativeLayout boxtwo = findViewById(R.id.boxtwo);
        RelativeLayout boxone = findViewById(R.id.boxone);
        LinearLayout oko = findViewById(R.id.oko);
        TextView llaal = findViewById(R.id.llaal);

        boxone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_shape));
        boxtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_shape));
        settingtype.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_settings_24));
        hold.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        theme.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        other_apps.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        llaal.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        label_bottom.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.thickGrey));
        settingstxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        most.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        oko.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        holder.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.coolDeepBlack));
        mainroot.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.niceBlack));
        nest.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home));
        floatingBall.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.cool_circle));
        stretch.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.faded_add_24));
        label_top.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
    }

    private void checkAndRequestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));

        requestOverlayPermissionLauncher.launch(intent);
    }

    private void puthingstonull() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.newprogressbar, (ViewGroup) findViewById(R.id.progressID));
        TextView text = (TextView) layout.findViewById(R.id.analyze);
        text.setText("Setting up home...");

        AlertDialog alertDialoG = new AlertDialog.Builder(this).setView(layout)
                .setCancelable(false).create();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null && pm.isIgnoringBatteryOptimizations(getPackageName())
                && Settings.canDrawOverlays(getApplicationContext())
                && UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
            setUp(alertDialoG);
        }

        // The permission is already granted, proceed with your logic
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            // Call the method that requires API level 21 or higher
            List<UsageStats> usageStatsList = getUsageStatsList(MainActivity.this);

            List<JustInfoApps> appList = new ArrayList<>();
            reverseAppInfoListAlphabetically(appList);
            Set<String> addedPackages = new HashSet<>();
            List<SearchedApp> appSearch = get();
            List<JustInfoApps> appListwo = getAllInstalledApps();

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
                        appList.add(new JustInfoApps(usageStats.getPackageName(), packageInfo.applicationInfo.icon));
                        addedPackages.add(usageStats.getPackageName());
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            // Update UI components with retrieved data
            List<SearchedApp> existingAppList = PreforSearch.getAppInfoList(getApplicationContext());

            // Create a set to store package names of existing apps for faster lookup
            Set<String> existingPackageNames = new HashSet<>();
            for (SearchedApp existingApp : existingAppList) {
                existingPackageNames.add(existingApp.getPackageName());
            }

            // Iterate over newly retrieved appSearch list
            for (SearchedApp app : appSearch) {
                // Check if the package name of the current app already exists in SharedPreferences
                if (!existingPackageNames.contains(app.getPackageName())) {
                    PreforSearch.addItem(getApplicationContext(), app);
                    // If not, add it to SharedPreferences
                    // Also, add it to the existingAppList to avoid adding duplicates again
                    existingAppList.add(app);
                    // Update the existingPackageNames set for faster lookup
                    existingPackageNames.add(app.getPackageName());
                }
            }

            hasExecuted = true;

            // Main thread updates
            runOnUiThread(() -> {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (alertDialoG != null){
                            alertDialoG.dismiss();
                        }
                    }
                }, 2500);

                // Update UI components with retrieved data
                updateUI(appList, appListwo);
            });
        });
    }

    // Method to update UI components
    private void updateUI(List<JustInfoApps> usageStatsList, List<JustInfoApps> appList) {
        // Update the first adapter
        // Create a custom adapter

        if (usageStatsList.size() == 0){
            oko.setVisibility(View.GONE);
            otherAdapter = new OtherAdapter(appList, MainActivity.this);
            viewHolder.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            mostTxt.setText("0");
            TransitionManager.beginDelayedTransition(viewGroup);
            otherslist.setAdapter(otherAdapter);
            othertxt.setText(String.valueOf(appList.size()));
            otherAdapter.notifyItemChanged(0);
        }else {
            adapter = new MyAdapter(usageStatsList, MainActivity.this);
            otherAdapter = new OtherAdapter(appList, MainActivity.this);
            viewHolder.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            mostTxt.setText(String.valueOf(usageStatsList.size()));
            TransitionManager.beginDelayedTransition(viewGroup);
            listView.setAdapter(adapter);
            otherslist.setAdapter(otherAdapter);
            othertxt.setText(String.valueOf(appList.size()));
            otherAdapter.notifyItemChanged(0);
            adapter.notifyItemInserted(0);
        }
    }

    private List<JustInfoApps> getAllInstalledApps() {
        List<JustInfoApps> appList = new ArrayList<>();
        List<SearchedApp> search = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

        // Get the package name of your own app
        String myPackageName = getPackageName();

        for (ResolveInfo resolveInfo : activities) {
            String packageName = resolveInfo.activityInfo.packageName;

            // Exclude your own app by comparing package names
            if (!packageName.equals(myPackageName)) {
                String appName = resolveInfo.loadLabel(packageManager).toString();
                int iconResource = resolveInfo.activityInfo.icon;
                appList.add(new JustInfoApps(packageName, iconResource));
            }
        }

        return appList;
    }

    private List<SearchedApp> get() {
        List<SearchedApp> search = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

        // Get the package name of your own app
        String myPackageName = getPackageName();

        for (ResolveInfo resolveInfo : activities) {
            String packageName = resolveInfo.activityInfo.packageName;

            // Exclude your own app by comparing package names
            String appName = resolveInfo.loadLabel(packageManager).toString();
            int iconResource = resolveInfo.activityInfo.icon;
            search.add(new SearchedApp(appName, packageName, resolveInfo.activityInfo.icon));
        }

        return search;
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
                TransitionManager.beginDelayedTransition(viewGroup);
                issueslayout.setVisibility(View.GONE);

                if (!hasIllustrated) {
                    openDialog();
                    return;
                } else {
                    puthingstonull();
                    if (!seekBarprogress.equals("0")) {
                        Intent intent = new Intent(getApplicationContext(), ShortcutService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                        } else {
                            startService(intent);
                        }
                    } else {
                        // From an Activity
                        Intent stopIntent = new Intent(this, ShortcutService.class);
                        stopService(stopIntent);
                    }
                }


                TransitionManager.beginDelayedTransition(viewGroup);
                themainthing.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.newprogressbar, (ViewGroup) findViewById(R.id.progressID));

//        ImageView image = (ImageView) layout.findViewById(R.id.image);
//        image.setImageResource(R.drawable.android); // replace with your own image resource
//
//        TextView text = (TextView) layout.findViewById(R.id.text);
//        text.setText("Hello! This is a custom AlertDialog!");

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setCancelable(false) // This makes the AlertDialog non-dismissable
                .create();

        // Perform null check on the window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        alertDialog.show();

        handler.postDelayed(() -> {
            alertDialog.dismiss();
            handler.removeCallbacksAndMessages(null);
            startActivity(new Intent(getApplicationContext(), IntroPageActivity.class));
        }, 2200);

    }

    private void setUp(AlertDialog alertDialoG) {
        // Perform null check on the window
        if (alertDialoG.getWindow() != null) {
            alertDialoG.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        alertDialoG.show();
    }

    public void reverseAppInfoListAlphabetically(List<JustInfoApps> appInfoList) {
        // Sort the list based on package name in ascending order
        appInfoList.sort((app1, app2) -> app1.getPackageName().compareToIgnoreCase(app2.getPackageName()));

        // Reverse the sorted list
        Collections.reverse(appInfoList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private boolean state() {
        SharedPreferences prefs = getSharedPreferences(UI_THEME, MODE_PRIVATE);
        boolean isNewUi = prefs.getBoolean(UI_KEY, false);

        return isNewUi;
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private static final String SIZE_NAME = "SeekBar";
        private static final String SIZE_KEY = "size";
        List<JustInfoApps> appn;
        Context context;
        String seekBarprogress;
        // Constructor to initialize the adapter with data


        public MyAdapter(List<JustInfoApps> appn, Context context) {
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
            JustInfoApps appInfo = appn.get(position);

            SharedPreferences prefs = context.getSharedPreferences(SIZE_NAME, MODE_PRIVATE);
            seekBarprogress = prefs.getString(SIZE_KEY, "none");


            holder.appIcon.setImageDrawable(appInfo.getAppIcon(context));


            checkstate(holder.checked, appInfo.getPackageName(), context);


            if (seekBarprogress.equals("0")) {
                if (state()){
                    holder.shadow.setVisibility(View.VISIBLE);
                    holder.shadow.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDark));
                }else {
                    holder.shadow.setVisibility(View.VISIBLE);
                    holder.shadow.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
                }
            }

            holder.itemView.setOnClickListener(v -> {
                if (seekBarprogress.equals("0")) {
                    showToast("Sensitivity is OFF");
                    return;
                }

                List<JustInfoApps> appInfoList = SharedPrefUtils.getAppInfoList(context);

                if (appInfoList != null && appInfoList.stream().anyMatch(storedAppInfo ->
                        storedAppInfo.getPackageName().equals(appInfo.getPackageName()))) {
                    // Package name exists in the list
                    removeapp(appInfo, holder.checked);
                } else {
                    // Package name does not exist in the list
                    uploadApptoServer(appInfo, holder.checked, context);
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return appn.size();
        }

        private void uploadApptoServer(JustInfoApps packagename, ImageView checked, Context context) {
            SharedPrefUtils.addItem(context, packagename);
            checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.blue_checkbox));
        }

        private void checkstate(ImageView checked, String packageName, Context context) {
            List<JustInfoApps> appInfoList = SharedPrefUtils.getAppInfoList(context);
            Drawable drawable;

            if (appInfoList != null && appInfoList.stream().anyMatch(appInfo -> appInfo.getPackageName().equals(packageName))) {
                // Package name exists in the list
                drawable = ContextCompat.getDrawable(context, R.drawable.blue_checkbox);
            } else {
                // Package name does not exist in the list
                if (state()) {
                    drawable = ContextCompat.getDrawable(context, R.drawable.darktheme_check_box_outline_blank_24);

                } else {
                    drawable = ContextCompat.getDrawable(context, R.drawable.baseline_check_box_outline_blank_24);
                }
            }

            checked.setImageDrawable(drawable);

        }


        private void removeapp(JustInfoApps packagename, ImageView checked) {
            if (state()){
                checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.darktheme_check_box_outline_blank_24));
            }else {
                checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_check_box_outline_blank_24));
            }
            SharedPrefUtils.removeAppInfo(context, packagename);
        }

        private boolean state() {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean darkThemeEnabled = prefs.getBoolean(THEME_KEY, false);

            return darkThemeEnabled;
        }

        private void showToast(String message){
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

        // Provide a reference to the views for each data item
        static class ViewHolder extends RecyclerView.ViewHolder {
            //TextView textView;
            ImageView appIcon, checked;
            RelativeLayout shadow;

            ViewHolder(View itemView) {
                super(itemView);
//                textView = itemView.findViewById(R.id.appName);
                appIcon = itemView.findViewById(R.id.appIcon);
                shadow = itemView.findViewById(R.id.shadow);
                checked = itemView.findViewById(R.id.checked);
//                state = itemView.findViewById(R.id.state);
            }
        }

    }

    private static class OtherAdapter extends RecyclerView.Adapter<OtherAdapter.ViewHolder> {

        private static final String SIZE_NAME = "SeekBar";
        private static final String SIZE_KEY = "size";
        List<JustInfoApps> appn;
        Context context;
        String seekBarprogress;


        // Constructor to initialize the adapter with data

        public OtherAdapter(List<JustInfoApps> appn, Context context) {
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
            JustInfoApps appInfo = appn.get(position);

            SharedPreferences prefs = context.getSharedPreferences(SIZE_NAME, MODE_PRIVATE);
            seekBarprogress = prefs.getString(SIZE_KEY, "none");

            holder.appIcon.setImageDrawable(appInfo.getAppIcon(context));

            checkstate(holder.checked, appInfo.getPackageName(), context);


            if (seekBarprogress.equals("0")) {
                if (state()){
                    holder.shadow.setVisibility(View.VISIBLE);
                    holder.shadow.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDark));
                }else {
                    holder.shadow.setVisibility(View.VISIBLE);
                    holder.shadow.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
                }
            }

            holder.itemView.setOnClickListener(v -> {
                if (seekBarprogress.equals("0")) {
                    showToast("Sensitivity is OFF");
                    return;
                }

                List<JustInfoApps> appInfoList = SharedPrefUtils.getAppInfoList(context);

                if (appInfoList != null && appInfoList.stream().anyMatch(storedAppInfo ->
                        storedAppInfo.getPackageName().equals(appInfo.getPackageName()))) {
                    // Package name exists in the list
                    removeapp(appInfo, holder.checked);
                } else {
                    // Package name does not exist in the list
                    uploadApptoServer(appInfo, holder.checked, context);
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return appn.size();
        }

        private void uploadApptoServer(JustInfoApps packagename, ImageView checked, Context context) {
            SharedPrefUtils.addItem(context, packagename);
            checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.blue_checkbox));
        }

        private void checkstate(ImageView checked, String packageName, Context context) {
            List<JustInfoApps> appInfoList = SharedPrefUtils.getAppInfoList(context);
            Drawable drawable;

            if (appInfoList != null && appInfoList.stream().anyMatch(appInfo -> appInfo.getPackageName().equals(packageName))) {
                // Package name exists in the list
                drawable = ContextCompat.getDrawable(context, R.drawable.blue_checkbox);
            } else {
                // Package name does not exist in the list

                if (state()) {
                    drawable = ContextCompat.getDrawable(context, R.drawable.darktheme_check_box_outline_blank_24);
                } else {
                    drawable = ContextCompat.getDrawable(context, R.drawable.baseline_check_box_outline_blank_24);
                }
            }

            checked.setImageDrawable(drawable);

        }

        private void removeapp(JustInfoApps packagename, ImageView checked) {
            if (state()){
                checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.darktheme_check_box_outline_blank_24));
            }else {
                checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_check_box_outline_blank_24));
            }
            SharedPrefUtils.removeAppInfo(context, packagename);
        }

        private boolean state() {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean darkThemeEnabled = prefs.getBoolean(THEME_KEY, false);

            return darkThemeEnabled;
        }

        private void showToast(String message){
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

        // Provide a reference to the views for each data item
        static class ViewHolder extends RecyclerView.ViewHolder {
            //TextView textView;
            ImageView appIcon, checked;
            RelativeLayout shadow;

            ViewHolder(View itemView) {
                super(itemView);
//                textView = itemView.findViewById(R.id.appName);
                appIcon = itemView.findViewById(R.id.appIcon);
                shadow = itemView.findViewById(R.id.shadow);
                checked = itemView.findViewById(R.id.checked);
            }
        }


    }


    void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}