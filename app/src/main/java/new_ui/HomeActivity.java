package new_ui;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import adapters.FavouriteListAdapter;
import decor.ImproveParallax;
import float_widgets.GuideUser;
import mappedvision.shortcuts.net.R;
import model.JustInfoApps;
import model.SearchedApp;
import services.ShortcutService;
import services.TapScrollAccessibility;
import utils.PreforSearch;
import utils.SharedPrefUtils;

public class HomeActivity extends AppCompatActivity {

    OtherAdapter spareAdapter;
    LinearLayout refrshList;
    List<SearchedApp> justInfoAppsList;
    ImageView howtouse;
    SwitchCompat stateofTapScroll;
    RecyclerView scroll_app_list;
    TextView size, favourites;
    Handler handler, firstHandle, secondHandle, thirdHandle;
    RelativeLayout callintent, fav_apps_layout;
    GuideUser guideUser;
    private boolean isScrolling = false;
    private static final String SIZE_NAME = "SeekBar";
    private static final String SIZE_KEY = "size";
    String seekBarprogress;

    @Override
    protected void onStart() {
        super.onStart();

        // Restore scroll position after the layout has been inflated
        if (!seekBarprogress.equals("0")) {
            Intent intent = new Intent(getApplicationContext(), ShortcutService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        } else {
            if (PreforSearch.getAppInfoList(getApplicationContext()).size() == 0){
                refreshAppList();
            }
            // From an Activity
            Intent stopIntent = new Intent(this, ShortcutService.class);
            stopService(stopIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        stateofTapScroll.setChecked(isAccessibilityEnabled());
        if (SharedPrefUtils.getAppInfoList(getApplicationContext()) != null) {
            int x = SharedPrefUtils.getAppInfoList(getApplicationContext()).size();
            if (SharedPrefUtils.getAppInfoList(getApplicationContext()).size() >= 1) {
                favourites.setText(String.valueOf(x));
            } else {
                favourites.setText("0");
            }
        } else {
            favourites.setText("--");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme); // Replace R.style.AppTheme with your actual theme name
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getSharedPreferences(SIZE_NAME, MODE_PRIVATE);
        seekBarprogress = prefs.getString(SIZE_KEY, "none");

        firstHandle = new Handler();
        secondHandle = new Handler();
        thirdHandle = new Handler();

        guideUser = new GuideUser(getApplicationContext());

        refrshList = findViewById(R.id.refrshList);
        fav_apps_layout = findViewById(R.id.fav_apps_layout);
        howtouse = findViewById(R.id.howtouse);
        stateofTapScroll = findViewById(R.id.stateofTapScroll);
        callintent = findViewById(R.id.callintent);
        favourites = findViewById(R.id.favourites);
        size = findViewById(R.id.size);
        justInfoAppsList = PreforSearch.getAppInfoList(getApplicationContext());
        spareAdapter = new OtherAdapter(justInfoAppsList, getApplicationContext());
        scroll_app_list = findViewById(R.id.scroll_app_list);
        // Add ParallaxItemDecoration with a parallax factor (adjust as needed)
        int parallaxFactor = getResources().getDimensionPixelOffset(R.dimen.parallax_factor);
        scroll_app_list.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        scroll_app_list.addItemDecoration(new ImproveParallax(parallaxFactor, 17));
        // Now, you can control the scroll behavior

        scroll_app_list.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return true; // Consume the touch event
            }
        });

        size.setText(String.format("%s Apps", justInfoAppsList.size()));
        Log.d("APP LISTS", "" + justInfoAppsList);

        if (SharedPrefUtils.getAppInfoList(getApplicationContext()) != null) {
            int x = SharedPrefUtils.getAppInfoList(getApplicationContext()).size();
            if (SharedPrefUtils.getAppInfoList(getApplicationContext()).size() >= 1) {
                favourites.setText(String.valueOf(x));
            } else {
                favourites.setText("0");
            }
        } else {
            favourites.setText("--");
        }

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();

        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(decor.findViewById(R.id.homescreen), true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);


        scroll_app_list.setAdapter(spareAdapter);
        spareAdapter.notifyItemChanged(0);
        startAutoScroll();

        callintent.setOnClickListener(v -> {
            if (scroll_app_list != null) {
                int firstVisibleItemPosition = ((GridLayoutManager) Objects.requireNonNull(scroll_app_list.getLayoutManager())).findFirstVisibleItemPosition();
                Intent intent = new Intent(getApplicationContext(), AppDrawerListActivity.class);
                intent.putExtra("scrollPosition", firstVisibleItemPosition);
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this, scroll_app_list, ViewCompat.getTransitionName(scroll_app_list));
                startActivity(intent, activityOptionsCompat.toBundle());
            } else {
                Log.d("Null Error Exception", "HOME-ACTIVITY SCROLL-APP-LIST == null");
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        };

        stateofTapScroll.setChecked(isAccessibilityEnabled());

        stateofTapScroll.setOnClickListener(v -> {
            if (isAccessibilityEnabled()) {
                stateofTapScroll.setChecked(true);
                openDialog();
            } else {
                openBottomSheet();
                stateofTapScroll.setChecked(false);
            }
        });

        howtouse.setOnClickListener(v -> guideBottomSheet());

        refrshList.setOnClickListener(v -> refreshAppList());

        fav_apps_layout.setOnClickListener(v -> {
            if (SharedPrefUtils.getAppInfoList(getApplicationContext()).size() > 0){
                openSheetForApps();
            }else {
                showToast("List is empty");
            }
        });

        findViewById(R.id.settings).setOnClickListener(v-> startActivity(new Intent(getApplicationContext(), NewSettingsActivity.class)));
    }

    private void openSheetForApps() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.bottomsheetTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.favourite_applist,
                (ViewGroup) findViewById(R.id.source));

        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        List<JustInfoApps> infoAppsList = SharedPrefUtils.getAppInfoList(this);
        RecyclerView favourites_recyclerview = view.findViewById(R.id.favourites_recyclerview);

        // Add ParallaxItemDecoration with a parallax factor (adjust as needed)
        int parallaxFactor = getResources().getDimensionPixelOffset(R.dimen.parallax_factor);
        favourites_recyclerview.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        favourites_recyclerview.addItemDecoration(new ImproveParallax(parallaxFactor, 17));

        FavouriteListAdapter favouriteListAdapter = new FavouriteListAdapter(getApplicationContext(), infoAppsList);
        favouriteListAdapter.notifyItemChanged(0);
        favourites_recyclerview.setAdapter(favouriteListAdapter);

        if (SharedPrefUtils.getAppInfoList(getApplicationContext()) != null) {
            if (SharedPrefUtils.getAppInfoList(getApplicationContext()).size() == 0) {
                bottomSheetDialog.dismiss();
            }
        }

        bottomSheetDialog.setOnDismissListener(dialog -> onResume());
    }

    private void openDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.close_accessibility, (ViewGroup) findViewById(R.id.diial));

//        ImageView image = (ImageView) layout.findViewById(R.id.image);
//        image.setImageResource(R.drawable.android); // replace with your own image resource
//
        LinearLayout closedialog = (LinearLayout) layout.findViewById(R.id.closedialog);
        LinearLayout turn_off_id = layout.findViewById(R.id.turn_off_id);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setCancelable(false) // This makes the AlertDialog non-dismissable
                .create();

        // Perform null check on the window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        alertDialog.show();

        turn_off_id.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });


        closedialog.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void openBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.bottomsheetTheme);

        View view = LayoutInflater.from(this).inflate(R.layout.accessibility_layout,
                (ViewGroup) findViewById(R.id.source));

        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        RelativeLayout enableAccessibility = view.findViewById(R.id.enableAccessibility);


        enableAccessibility.setOnClickListener(v -> {
            //Set Accessibility
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });
    }

    private void guideBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.bottomsheetTheme);

        View view = LayoutInflater.from(this).inflate(R.layout.how_to_use_layout,
                (ViewGroup) findViewById(R.id.source));

        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        RelativeLayout enableAccessibility = view.findViewById(R.id.enableAccessibility);


        enableAccessibility.setOnClickListener(v -> {
            //Set Accessibility
            bottomSheetDialog.dismiss();
            guideUser.open();
        });
    }

    private boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE_NAME = getPackageName() + "/" + TapScrollAccessibility.class.getName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                colonSplitter.setString(settingValue);
                while (colonSplitter.hasNext()) {
                    String accessibilityService = colonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE_NAME)) {
                        return true;
                    }
                }
            }
        } else {
            Log.d(TAG, "Accessibility service disabled");
        }
        return false;
    }

    private void startAutoScroll() {
        final int scrollSpeed = 10; // Adjust the speed as needed (smaller values for slower motion)

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isScrolling) {
                    int visibleItemCount = scroll_app_list.getLayoutManager().getChildCount();
                    int totalItemCount = scroll_app_list.getLayoutManager().getItemCount();
                    int pastVisibleItems = ((GridLayoutManager) scroll_app_list.getLayoutManager()).findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        // Reached the end of the list, reset to the beginning
                        scroll_app_list.scrollToPosition(0);
                    } else {
                        // Scroll to the next item
                        scroll_app_list.smoothScrollBy(scrollSpeed, 0);
                    }
                }

                // Schedule the next scroll
                handler.postDelayed(this, scrollSpeed);
            }
        }, scrollSpeed);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        isScrolling = true;
        firstHandle.removeCallbacksAndMessages(null);
        secondHandle.removeCallbacksAndMessages(null);
        thirdHandle.removeCallbacksAndMessages(null);
    }

    void refreshAppList() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.newprogressbar, (ViewGroup) findViewById(R.id.progressID));

//        ImageView image = (ImageView) layout.findViewById(R.id.image);
//        image.setImageResource(R.drawable.android); // replace with your own image resource
//
        TextView text = (TextView) layout.findViewById(R.id.analyze);
        text.setText(R.string.getting_installed_apps);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setCancelable(false) // This makes the AlertDialog non-dismissable
                .create();

        // Perform null check on the window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        alertDialog.show();

        firstHandle.postDelayed(() -> {
            text.setText(R.string.refreshing_the_list);
            getNewList(text, alertDialog);
            firstHandle.removeCallbacksAndMessages(null);
        }, 2200);
    }

    void getNewList(TextView text, AlertDialog alertDialog) {
        PreforSearch.removeAll(getApplicationContext());

        secondHandle.postDelayed(() -> {
            text.setText(R.string.finalizing);
            addingNew(text, alertDialog);
            secondHandle.removeCallbacksAndMessages(null);
        }, 2200);
    }

    void addingNew(TextView text, AlertDialog alertDialog) {
        for (SearchedApp justInfoApps : get()) {
            if (!PreforSearch.getAppInfoList(getApplicationContext()).contains(text)) {
                PreforSearch.addItem(getApplicationContext(), justInfoApps);
            }
        }

        thirdHandle.postDelayed(() -> {
            if (PreforSearch.getAppInfoList(getApplicationContext()).size() == 0) {
                showToast("App broken. Try again!!!");
                alertDialog.dismiss();
                thirdHandle.removeCallbacksAndMessages(null);
            } else {
                alertDialog.dismiss();
                restartApp();
                thirdHandle.removeCallbacksAndMessages(null);
            }
        }, 5200);
    }

    void restartApp() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.newprogressbar, (ViewGroup) findViewById(R.id.progressID));

        TextView text = (TextView) layout.findViewById(R.id.analyze);
        text.setText("Restarting the app...");

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setCancelable(false) // This makes the AlertDialog non-dismissable
                .create();

        // Perform null check on the window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        alertDialog.show();

        Handler finalHandler = new Handler();

        finalHandler.postDelayed(() -> {
            finishAndRemoveTask();
            alertDialog.dismiss();
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                // Handle the case where the intent is null
                Log.e("RestartApp", "Intent is null");
            }

            // Clear the handler by removing any pending messages or tasks
            finalHandler.removeCallbacksAndMessages(null);
        }, 2500);
    }

    void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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

    private static class OtherAdapter extends RecyclerView.Adapter<OtherAdapter.ViewHolder> {
        private static final String SIZE_NAME = "SeekBar";
        private static final String SIZE_KEY = "size";
        List<SearchedApp> appn;
        Context context;
        String seekBarprogress;


        public OtherAdapter(List<SearchedApp> appn, Context context) {
            this.appn = appn;
            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public OtherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_apps_layout, parent, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull OtherAdapter.ViewHolder holder, int position) {
            SearchedApp appInfo = appn.get(position);
            holder.appIcon.setImageDrawable(appInfo.getAppIcon(context));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return appn.size();
        }


        private void showToast(String message) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
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
            }
        }


    }

}