package float_widgets;

import static android.content.Context.WINDOW_SERVICE;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import decor.ParallaxItemDecoration;
import mappedvision.shortcuts.net.R;
import mappedvision.shortcuts.net.SearchWindowActivity;
import model.JustInfoApps;
import services.ShortcutService;
import utils.SharedPrefUtils;

public class Window {

    // declaring required variables
    private final Context context;
    private final View mView;
    // Assuming that you have a reference to the main looper
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final WindowManager mWindowManager;
    private final LayoutInflater layoutInflater;
    TextView instructions;
    LinearLayout base_root;
    RecentAdapter recentAdapter;
    AppInfoAdapter adapter;
    RelativeLayout close, search_bar;
    TextView title;
    RecyclerView recyclerView, recent_recyclerView;
    ViewGroup root;

    Handler getHandler = new Handler();
    LinearLayout container;
    private WindowManager.LayoutParams mParams;
    private AppInfoAdapter appInfoAdapter;
    private List<JustInfoApps> appInfoList;

    @SuppressLint("CutPasteId")
    public Window(Context context) {
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
        mView = layoutInflater.inflate(R.layout.window_layout, null);

        mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        base_root = mView.findViewById(R.id.root);
        title = mView.findViewById(R.id.title);
        instructions = mView.findViewById(R.id.instructions);
        recyclerView = mView.findViewById(R.id.recyclerView);
        close = mView.findViewById(R.id.close);
        root = mView.findViewById(R.id.root);
        search_bar = mView.findViewById(R.id.search_bar);
        container = mView.findViewById(R.id.container);
        recent_recyclerView = mView.findViewById(R.id.recent_recyclerView);

        // Set OnClickListener for the RelativeLayout
        close.setOnClickListener(v -> {
            // Handle click event
            close();
        });

        base_root.setOnClickListener(v -> {
            close();
        });

        search_bar.setOnClickListener(v -> {
            // Handle click event
            closeNew();

            try {
                Intent intent = new Intent(context, SearchWindowActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception appropriately, perhaps by logging or displaying an error message.
            }
        });

        int parallaxFactor = context.getResources().getDimensionPixelOffset(R.dimen.parallax_factor);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new ParallaxItemDecoration(parallaxFactor, 0));

        recent_recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        recent_recyclerView.addItemDecoration(new ParallaxItemDecoration(parallaxFactor, 0));

        puthingstonull();

        appInfoList = SharedPrefUtils.getAppInfoList(context);
        adapter = new AppInfoAdapter(context, appInfoList);

        if (appInfoList.size() > 4) {
            instructions.setText("(Scroll left for more)");
        }

        recyclerView.setAdapter(adapter);
        adapter.notifyItemChanged(0);


        // Define the position of the
        // window within the screen
        mParams.gravity = Gravity.FILL_VERTICAL;
        mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -350);
        toast.show();
    }

    private void puthingstonull() {
        // The permission is already granted, proceed with your logic
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            List<UsageStats> recentList = recentAppList(context);
            List<AppInfo> appListTwo = new ArrayList<>();
            Set<String> addedPackagesTwo = new HashSet<>();

            for (UsageStats usageStats : recentList) {
                try {
                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(usageStats.getPackageName(), 0);
                    boolean isUserApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
                    boolean isUpdatedSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
                    if ((isUserApp || isUpdatedSystemApp)
                            && !usageStats.getPackageName().equals(context.getPackageName())
                            && !addedPackagesTwo.contains(usageStats.getPackageName())) {
                        String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                        Drawable appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
                        appListTwo.add(new AppInfo("", usageStats.getPackageName(), appIcon));
                        addedPackagesTwo.add(usageStats.getPackageName());
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }


            // Create a custom adapter
            recentAdapter = new RecentAdapter(appListTwo, context);

//            updateUIOnMainThread();

            updateUIOnMainThread(appListTwo.size());
        });

    }

    private void updateUIOnMainThread(int sized) {
//        recyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();


//        if (sized < 1) {
//            recent_recyclerView.setVisibility(View.GONE);
//            title.setVisibility(View.GONE);
//            return;
//        }
//
//        recent_recyclerView.setAdapter(recentAdapter);
//        recentAdapter.notifyItemChanged(0);

        mainHandler.post(() -> {
            if (sized < 1) {
                recent_recyclerView.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                return;
            }

            TransitionManager.beginDelayedTransition(root);
            recent_recyclerView.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            recent_recyclerView.setAdapter(recentAdapter);
            recentAdapter.notifyItemChanged(0);
        });

    }

    private List<UsageStats> getUsageStatsList(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        return usageStatsList;
    }

    public List<UsageStats> recentAppList(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long endTime = System.currentTimeMillis();
        long beginTime = endTime - 1000 * 60 * 20; // Last half-an hour

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, endTime);
        SortedMap<Long, UsageStats> sortedMap = new TreeMap<>();

        for (UsageStats usageStats : usageStatsList) {
            sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
        }

        List<UsageStats> recentApps = new ArrayList<>(sortedMap.values());
//        Log.d("SIZE", ""+recentApps.size());
//        if (recentApps.size() > 4) {
//            return recentApps.subList(recentApps.size() - 6, recentApps.size()); // Return last 4 apps
//        } else {
//            return recentApps; // If less than 4 apps, return all
//        }


        return recentApps;
    }

    public void open() {
        try {
            // check if the view is already
            // inflated or present in the window
            if (mView.getWindowToken() == null) {
                if (mView.getParent() == null) {
                    mWindowManager.addView(mView, mParams);
                }
            }
        } catch (Exception e) {
            Log.d("Error1", e.toString());
        }
    }

    public void closeNew() {
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
        } catch (Exception e) {
            Log.e("Error2", e.toString());
        }

    }

    public void close() {
        TransitionManager.beginDelayedTransition(root);
        recyclerView.setVisibility(View.GONE);
        close.setVisibility(View.GONE);
        search_bar.setVisibility(View.GONE);
        container.setVisibility(View.GONE);

        vibrate();

        getHandler.postDelayed(() -> {
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
            } catch (Exception e) {
                Log.e("Error2", e.toString());
            }

            getHandler.removeCallbacksAndMessages(null);

        }, 1000);
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            // Vibrate for 500 milliseconds
            vibrator.vibrate(30);
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
        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_apps, parent, false);
            return new MyAdapter.ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
            AppInfo appInfo = appn.get(position);

//            holder.maincontainer.setBackgroundColor(ContextCompat.getColor(context, R.color.invicibleColor));

            //holder.textView.setText(appInfo.name);
            holder.appIcon.setImageDrawable(appInfo.icon);
//            String textToDisplay = appInfo.name;
//
//            // Set the maximum length to 5 characters
//            int maxLength = 7;
//
//            // Check if the text exceeds the maximum length
//            if (textToDisplay.length() > maxLength) {
//                // Truncate the text and append ellipsis
//                String truncatedText = textToDisplay.substring(0, maxLength) + "...";
//                holder.appname.setText(truncatedText);
//            } else {
//                // Display the original text if it doesn't exceed the maximum length
//                holder.appname.setText(textToDisplay);
//            }
//
//            holder.checked.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openApp(appInfo.packagename);
                }
            });
        }

        private void openApp(String packagename) {

            try {
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packagename);

                if (launchIntent != null) {
                    context.startActivity(launchIntent);
                    close();
                } else {
                    // The app is not installed on the device
                    // You might want to handle this case accordingly
                    showToast();
                }
            } catch (Exception e) {
                // An exception occurred, handle it if needed
                e.printStackTrace();
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return appn.size();
        }

        private void showToast() {
            Toast toast = Toast.makeText(context, "App not found", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -350);
            toast.show();
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

    }

    private class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

        List<AppInfo> appn;
        Context context;

        // Constructor to initialize the adapter with data

        public RecentAdapter(List<AppInfo> appn, Context context) {
            this.appn = appn;
            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public RecentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_apps, parent, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull RecentAdapter.ViewHolder holder, int position) {
            AppInfo appInfo = appn.get(position);


            holder.appIcon.setImageDrawable(appInfo.icon);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openApp(appInfo.packagename);
                }
            });
        }

        private void openApp(String packagename) {

            try {
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packagename);

                if (launchIntent != null) {
                    context.startActivity(launchIntent);
                    close();
                } else {
                    // The app is not installed on the device
                    // You might want to handle this case accordingly
                    showToast();
                }
            } catch (Exception e) {
                // An exception occurred, handle it if needed
                e.printStackTrace();
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            if (appn.size() > 8) {
                return 8;
            }

            return appn.size();
        }

        private void showToast() {
            Toast toast = Toast.makeText(context, "App not found", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -350);
            toast.show();
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

    }

    private class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.ViewHolder> {
        private List<JustInfoApps> appInfoList;
        private Context context;

        public AppInfoAdapter(Context context, List<JustInfoApps> appInfoList) {
            this.context = context;
            this.appInfoList = appInfoList;
        }

        @NonNull
        @Override
        public AppInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_apps, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppInfoAdapter.ViewHolder holder, int position) {
            JustInfoApps appInfo = appInfoList.get(position);
            Drawable appIcon = appInfo.getAppIcon(context);

            if (appIcon != null) {
                holder.imageView.setImageDrawable(appIcon);
            }


            holder.itemView.setOnClickListener(v -> {
                String packageName = appInfo.getPackageName();
                openApp(packageName);
            });
        }

        private void openApp(String packagename) {
            try {
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packagename);
                if (launchIntent != null) {
                    close();
                    context.startActivity(launchIntent);
                } else {
                    // The app is not installed on the device
                    // You might want to handle this case accordingly
                    showToast("App not found");
                }
            } catch (Exception e) {
                // An exception occurred, handle it if needed
                e.printStackTrace();
            }

        }

        private void showToast(String message) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -350);
            toast.show();
        }

        @Override
        public int getItemCount() {
            return appInfoList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.appIcon);
            }
        }

    }
}
