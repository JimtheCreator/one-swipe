package new_ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import decor.ImproveParallax;
import interface_pack.OnBackPressedListener;
import mappedvision.shortcuts.net.MainActivity;
import mappedvision.shortcuts.net.R;
import model.ItemDecorator;
import model.JustInfoApps;
import model.SearchedApp;
import utils.PreforSearch;
import utils.SharedPrefUtils;

public class AppDrawerListActivity extends AppCompatActivity {
    RecyclerView scroll_app_list, appslist_recyclerview;
    SpareAdapter spareAdapter;
    ProgressBar app_prepare_progress;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String THEME_KEY = "theme";
    Handler handler;
    boolean isScrolling = false;
    OtherAdapter adapter;
    List<SearchedApp> justInfoAppsList;
    private int scrollPosition;
    LinearLayout opendrawer;

    OnBackPressedCallback callback;

    List<JustInfoApps> usageStatsList;


    @Override
    protected void onStart() {
        super.onStart();
        // Restore scroll position after the layout has been inflated
        scroll_app_list.scrollToPosition(scrollPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.THEMED); // Replace R.style.AppTheme with your actual theme name
        setContentView(R.layout.activity_app_drawer_list);

        // Retrieve scroll position from Intent extras
        if (getIntent().hasExtra("scrollPosition")) {
            scrollPosition = getIntent().getIntExtra("scrollPosition", 0);
        }

        opendrawer = findViewById(R.id.opendrawer);
        app_prepare_progress = findViewById(R.id.app_prepare_progress);
        appslist_recyclerview = findViewById(R.id.appslist_recyclerview);
        appslist_recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        appslist_recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        appslist_recyclerview.addItemDecoration(new ItemDecorator(3, spacingInPixels, true));

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();

        fade.excludeTarget(decor.findViewById(R.id.drawerList), true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        justInfoAppsList = PreforSearch.getAppInfoList(getApplicationContext());
        spareAdapter = new SpareAdapter(justInfoAppsList, getApplicationContext());

        scroll_app_list = findViewById(R.id.scroll_app_list);
        // Add ParallaxItemDecoration with a parallax factor (adjust as needed)
        int parallaxFactor = getResources().getDimensionPixelOffset(R.dimen.parallax_factor);
        scroll_app_list.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        scroll_app_list.addItemDecoration(new ImproveParallax(parallaxFactor, 17));
        // Now, you can control the scroll behavior

        scroll_app_list.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return true; // Consume the touch event
            }
        });

        scroll_app_list.setAdapter(spareAdapter);
        spareAdapter.notifyItemChanged(0);

        startAutoScroll();

        // Set the status bar color to white
        // Set the status bar color to black
        setStatusBarColor(getWindow(), getResources().getColor(R.color.ballBlack, getTheme()));

        // Set the status bar icons to be light-colored
        setLightStatusBarIcons(getWindow());

        refreshLists();

        findViewById(R.id.closepage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void refreshLists(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {
            List<JustInfoApps> searchedAppList = getAllInstalledApps();
            runOnUiThread(() -> {
                adapter = new OtherAdapter(searchedAppList, getApplicationContext());
                adapter.notifyItemChanged(0);
                appslist_recyclerview.setAdapter(adapter);
                app_prepare_progress.setVisibility(View.GONE);
                TransitionManager.beginDelayedTransition(findViewById(R.id.drawerList));
                opendrawer.setVisibility(View.VISIBLE);
            });
        });
    }

    private void setStatusBarColor(Window window, int color) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void setLightStatusBarIcons(Window window) {
        View decorView = window.getDecorView();
        int flags = decorView.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class SpareAdapter extends RecyclerView.Adapter<SpareAdapter.ViewHolder> {
        List<SearchedApp> appn;
        Context context;

        public SpareAdapter(List<SearchedApp> appn, Context context) {
            this.appn = appn;
            this.context = context;
        }


        @NonNull
        @Override
        public SpareAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.magnified_caurosel, parent, false);
            return new SpareAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SpareAdapter.ViewHolder holder, int position) {
            SearchedApp appInfo = appn.get(position);

            if (appInfo != null) {
                holder.spareappIcon.setImageDrawable(appInfo.getAppIcon(context));
            } else {
                holder.spareappIcon.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return appn.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView spareappIcon;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                spareappIcon = itemView.findViewById(R.id.spareappIcon);
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
        public OtherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_layout, parent, false);
            return new OtherAdapter.ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull OtherAdapter.ViewHolder holder, int position) {
            JustInfoApps appInfo = appn.get(position);

            SharedPreferences prefs = context.getSharedPreferences(SIZE_NAME, MODE_PRIVATE);
            seekBarprogress = prefs.getString(SIZE_KEY, "none");

            holder.appIcon.setImageDrawable(appInfo.getAppIcon(context));

            checkstate(holder.checked, appInfo.getPackageName(), context);

            if (seekBarprogress.equals("0")) {
                holder.shadow.setVisibility(View.VISIBLE);
                holder.shadow.setBackgroundColor(ContextCompat.getColor(context, R.color.darktransparency));
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
                    drawable = ContextCompat.getDrawable(context, R.drawable.darktheme_check_box_outline_blank_24);
                }
            }

            checked.setImageDrawable(drawable);

        }

        private void removeapp(JustInfoApps packagename, ImageView checked) {
            if (state()){
                checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.darktheme_check_box_outline_blank_24));
            }else {
                checked.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.darktheme_check_box_outline_blank_24));
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

}