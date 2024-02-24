package mappedvision.shortcuts.net;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import adapters.AppSearchAdapter;
import decor.ParallaxItemDecoration;
import model.JustInfoApps;
import model.SearchedApp;
import utils.PreforSearch;
import utils.SharedPrefUtils;

public class SearchWindowActivity extends Activity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String THEME_KEY = "theme";
    //    SoftInputAssist softInputAssist;
    AppInfoAdapter adapter;
    SearchView search_bar;
    AppSearchAdapter appSearchAdapter;
    ViewGroup root;
    TextView header;
    RecyclerView suggestions_recyclerview, search_apps_recyclerview;
    LinearLayout quicksearch_holder;
    RelativeLayout youtube, websearch, playstore;
    TextView text1, text2, text3;
    private List<JustInfoApps> appInfoList;
    private List<SearchedApp> appList;

    @Override
    protected void onStart() {
        super.onStart();
        applyTheme();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_window);

        youtube = findViewById(R.id.youtube);
        websearch = findViewById(R.id.websearch);
        playstore = findViewById(R.id.playstore);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        quicksearch_holder = findViewById(R.id.quicksearch_holder);
        root = findViewById(R.id.rooot);

        header = findViewById(R.id.header);
        suggestions_recyclerview = findViewById(R.id.suggestions_recyclerview);
        search_apps_recyclerview = findViewById(R.id.search_apps_recyclerview);
        search_bar = findViewById(R.id.search_bar);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            appList = PreforSearch.getAppInfoList(getApplicationContext());
            appInfoList = SharedPrefUtils.getAppInfoList(getApplicationContext());

            runOnUiThread(() -> {
                appSearchAdapter = new AppSearchAdapter(getApplicationContext(), appList, SearchWindowActivity.this);
                // The permission is already granted, proceed with your logic
                int parallaxFactor = getResources().getDimensionPixelOffset(R.dimen.parallax_factor);
                search_apps_recyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
                search_apps_recyclerview.setHasFixedSize(true);
                search_apps_recyclerview.addItemDecoration(new ParallaxItemDecoration(5, 0));
                search_apps_recyclerview.setAdapter(appSearchAdapter);


                Log.d("SIZE", "No" + appList.size());
                suggestions_recyclerview.addItemDecoration(new ParallaxItemDecoration(5, 0));
                suggestions_recyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
                adapter = new AppInfoAdapter(getApplicationContext(), appInfoList);

                suggestions_recyclerview.setHasFixedSize(true);
                suggestions_recyclerview.setAdapter(adapter);
                adapter.notifyItemChanged(0);
            });
        });

        Log.d("LISTS", "" + appList);

        // Add TextWatcher to dynamically filter the RecyclerView as the user types
        search_bar.requestFocus();

        // Optionally, you can also show the keyboard programmatically
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(search_bar, InputMethodManager.SHOW_IMPLICIT);

        EditText searchEditText = search_bar.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.offwhite));


        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Called when the user submits the query
                performSearch(query);
                return true; // Return true to indicate that the query has bee
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                filterNewtext(newText);
                otherSearch(newText);
                return true;
            }
        });


        // This callback will only be called when MyActivity is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
//                restartService();
                finishAffinity();
            }
        };

        adjustScreen();
    }

    void otherSearch(String query){
        // Set the maximum length to 5 characters
        int maxLength = 20;

        // Check if the text exceeds the maximum length
        if (query.length() > maxLength) {
            // Truncate the text and append ellipsis
            String truncatedText = query.substring(0, maxLength) + "...";
            text1.setText(truncatedText);
            text2.setText(truncatedText);
            text3.setText(truncatedText);
        }

        else {
            // Display the original text if it doesn't exceed the maximum length
            text1.setText(query);
            text2.setText(query);
            text3.setText(query);
        }


        if (query.isEmpty() || query.startsWith(" ") || query.endsWith(" ")){
            TransitionManager.beginDelayedTransition(root);
            quicksearch_holder.setVisibility(View.GONE);
        }else {
            TransitionManager.beginDelayedTransition(root);
            quicksearch_holder.setVisibility(View.VISIBLE);
        }


        youtube.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setPackage("com.google.android.youtube");
            intent.putExtra("query", query);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        websearch.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, query);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                hideKeyboard();
            } else {
                showToast("Something went wrong!!!!");
                hideKeyboard();
            }

        });

        playstore.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://search?q=" + URLEncoder.encode(query, "UTF-8")));
                startActivity(intent);
                hideKeyboard();
            } catch (ActivityNotFoundException | UnsupportedEncodingException e) {
                // The Play Store app is not installed on the device, use the browser version.
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/search?q=" + URLEncoder.encode(query, "UTF-8")));
                    startActivity(intent);
                    hideKeyboard();
                } catch (UnsupportedEncodingException ex) {
                    // Handle the exception
                    showToast("Something went wrong!!!!");
                    hideKeyboard();
                }
            }

        });
    }

    private void performSearch(String query) {
        if (query.length() >= 3) {
            for (SearchedApp searchedApp : appList) {
                if (searchedApp.getAppName().toLowerCase().startsWith(query.toLowerCase())) {
                    openApp(searchedApp.getPackageName());
                } else {
                    hideKeyboard();
                }
            }
        } else {
            hideKeyboard();
        }
    }

    private void openApp(String packagename) {
        try {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packagename);
            if (launchIntent != null) {
                startActivity(launchIntent);
                hideKeyboard();
            } else {
                // The app is not installed on the device
                // You might want to handle this case accordingly
                showToast("App not found");
                hideKeyboard();
            }
        } catch (Exception e) {
            // An exception occurred, handle it if needed
            e.printStackTrace();
        }

    }

    private void filterNewtext(String newText) {
        List<SearchedApp> searchedAppList = new ArrayList<>();

        for (SearchedApp searchedApp : appList) {
            if (searchedApp.getAppName().toLowerCase().contains(newText.toLowerCase())) {
                searchedAppList.add(searchedApp);
            }
        }


        if (searchedAppList.isEmpty()) {
            // Clear the adapter if the search text does not match any items
            search_apps_recyclerview.setVisibility(View.GONE);
            suggestions_recyclerview.setVisibility(View.VISIBLE);
            header.setText("Suggestions");
        } else {
            // Update the search adapter with the filtered list
            search_apps_recyclerview.setVisibility(View.VISIBLE);
            suggestions_recyclerview.setVisibility(View.GONE);
            header.setText("Top Hit");
            appSearchAdapter.filterList(searchedAppList);
        }

    }

    private void adjustScreen() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

//        softInputAssist.onDestroy();
    }

//    private void restartService() {
//        if (UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
//            // The permission is granted
//            Intent ii = new Intent(getApplicationContext(), ShortcutService.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                ContextCompat.startForegroundService(getApplicationContext(), ii);
//            } else {
//                startService(ii);
//            }
//        }
//    }


    @Override
    protected void onResume() {
        overridePendingTransition(0, 0);
        super.onResume();
//        softInputAssist.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        finishAffinity();
//        softInputAssist.onPause();
    }

    private void applyTheme() {
        RelativeLayout rooot = findViewById(R.id.rooot);
        RelativeLayout topsearch = findViewById(R.id.topsearch);
        LinearLayout sheet = findViewById(R.id.sheet);

        if (state()) {
            // Change status bar color
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.niceBlack)); // Change to your desired color
            window.setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.niceBlack));
            View view = getWindow().getDecorView();

            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

            topsearch.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.for_search));
            sheet.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sharp_corners));
            rooot.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.niceBlack));
//            rooot.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dark_wallpaper));
            header.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            text1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            text2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            text3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
    }

    private void hideKeyboard() {
        // Get the input method manager
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Check if no view has focus
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(getApplicationContext());
        }

        // Hide the keyboard
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -350);
        toast.show();
    }

    private boolean state() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkThemeEnabled = prefs.getBoolean(THEME_KEY, false);
        return darkThemeEnabled;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.changed, parent, false);
            return new AppInfoAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppInfoAdapter.ViewHolder holder, int position) {
            JustInfoApps appInfo = appInfoList.get(position);

            if (appInfo.getAppIcon(context) != null) {
                Drawable appIcon = appInfo.getAppIcon(context);

                if (appIcon != null) {
                    holder.imageView.setImageDrawable(appIcon);
                }
            }

            holder.itemView.setOnClickListener(v -> {
                String packageName = appInfo.getPackageName();
                openApp(packageName);
                hideKeyboard();
            });
        }

        private void openApp(String packagename) {
            try {
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packagename);
                if (launchIntent != null) {
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
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        @Override
        public int getItemCount() {
            if (appInfoList.size() > 8) {
                return 8;
            }

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