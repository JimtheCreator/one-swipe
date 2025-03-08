package mappedvision.shortcuts.net;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.transition.TransitionManager;
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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import adapters.AppSearchAdapter;
import adapters.RecentSearchAdapter;
import decor.ParallaxItemDecoration;
import model.JustInfoApps;
import model.SearchedApp;
import model.SelectedItem;

import space.SpacebetweenItems;
import utils.PreforSearch;
import utils.SharedPrefUtils;
import utils.WebsiteRecentSearch;


public class SearchWindowActivity extends Activity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String THEME_KEY = "theme";
    AppInfoAdapter adapter;
    SearchView search_bar;
    AppSearchAdapter appSearchAdapter;
    ViewGroup root;
    TextView header, clearRecents;
    RecyclerView suggestions_recyclerview, search_apps_recyclerview, recentsearchRecyclerView;
    LinearLayout quicksearch_holder, recentsearchLayout, holderofsearch;
    RelativeLayout youtube, websearch, playstore;
    TextView text1, text2, text3;
    RecentSearchAdapter recentSearchAdapter;
    List<SelectedItem> recentsList;
    NestedScrollView swipey;
    private List<JustInfoApps> appInfoList;
    private List<SearchedApp> appList;
    private static final String COLOR_NAME = "Colour";
    private static final String COLOR_KEY = "value";

    String colorKey;


    @Override
    protected void onStart() {
        super.onStart();
        applyTheme();
        loadDATA();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set window flags to disable animations
        getWindow().setWindowAnimations(0);
        // Set window flags to keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_search_window);

        SharedPreferences prefs = getSharedPreferences(COLOR_NAME, MODE_PRIVATE);
        colorKey = prefs.getString(COLOR_KEY, "none");

        swipey = findViewById(R.id.swipey);
        search_bar = findViewById(R.id.search_bar);
        clearRecents = findViewById(R.id.clearRecents);
        recentsearchLayout = findViewById(R.id.recentsearchLayout);
        recentsearchRecyclerView = findViewById(R.id.recentsearchRecyclerView);
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

        root.setOnClickListener(v -> {
            hideKeyboard();
        });


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


        clearRecents.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(root);
            recentsearchLayout.setVisibility(View.GONE);
            WebsiteRecentSearch.removeStringArray(getApplicationContext());
        });

        swipey.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Check if the keyboard is currently open
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isAcceptingText()) {
                    // Hide the keyboard
                    imm.hideSoftInputFromWindow(swipey.getWindowToken(), 0);
                }
            }
        });


    }

    private void loadDATA() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            // Load data
            appList = PreforSearch.getAppInfoList(getApplicationContext());
            appInfoList = SharedPrefUtils.getAppInfoList(getApplicationContext());
            recentsList = WebsiteRecentSearch.getStringArray(getApplicationContext());

            // Update UI on the main thread
            runOnUiThread(() -> {
                // Update RecyclerViews and Adapters
                recentSearchAdapter = new RecentSearchAdapter(getApplicationContext(), recentsList, SearchWindowActivity.this);
                recentsearchRecyclerView.addItemDecoration(new SpacebetweenItems(40));
                recentsearchRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recentsearchRecyclerView.setAdapter(recentSearchAdapter);

                if (recentsList.size() == 0) {
                    recentsearchLayout.setVisibility(View.GONE);
                }

                appSearchAdapter = new AppSearchAdapter(getApplicationContext(), appList, SearchWindowActivity.this);
                int parallaxFactor = getResources().getDimensionPixelOffset(R.dimen.parallax_factor);
                search_apps_recyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
                search_apps_recyclerview.setHasFixedSize(true);
                search_apps_recyclerview.addItemDecoration(new ParallaxItemDecoration(5, 0));
                search_apps_recyclerview.setAdapter(appSearchAdapter);

                suggestions_recyclerview.addItemDecoration(new ParallaxItemDecoration(5, 0));
                suggestions_recyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
                adapter = new AppInfoAdapter(getApplicationContext(), appInfoList);
                suggestions_recyclerview.setHasFixedSize(true);
                suggestions_recyclerview.setAdapter(adapter);
                adapter.notifyItemChanged(0);
            });
        });

    }


    void otherSearch(String query) {
        // Set the maximum length to 5 characters
        int maxLength = 20;

        // Check if the text exceeds the maximum length
        if (query.length() > maxLength) {
            // Truncate the text and append ellipsis
            String truncatedText = query.substring(0, maxLength) + "...";
            text1.setText(truncatedText);
            text2.setText(truncatedText);
            text3.setText(truncatedText);
        } else {
            // Display the original text if it doesn't exceed the maximum length
            text1.setText(query);
            text2.setText(query);
            text3.setText(query);
        }


        if (query.isEmpty() || query.startsWith(" ") || query.endsWith(" ")) {
            if (WebsiteRecentSearch.getStringArray(getApplicationContext()).size() >= 1) {
                recentsearchLayout.setVisibility(View.VISIBLE);
            }

            TransitionManager.beginDelayedTransition(root);
            quicksearch_holder.setVisibility(View.GONE);
        } else {
            recentsearchLayout.setVisibility(View.GONE);
            TransitionManager.beginDelayedTransition(root);
            quicksearch_holder.setVisibility(View.VISIBLE);
        }


        youtube.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setPackage("com.google.android.youtube");
            intent.putExtra("query", query);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Start the activity if the intent can be resolved
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                hideKeyboard();

                // Create a SelectedItem object with the query and origin
                SelectedItem selectedItem = new SelectedItem(query, "youtube");

                // Check if the query is not already in the recent search list
                List<SelectedItem> recentSearchList = WebsiteRecentSearch.getStringArray(getApplicationContext());
                if (!containsSelectedItem(recentSearchList, query)) {
                    WebsiteRecentSearch.addItem(getApplicationContext(), selectedItem);
                }
            }
        });


        websearch.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, query);

            // Start the web search activity if the intent can be resolved
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                hideKeyboard();

                // Create a SelectedItem object with the query and origin
                SelectedItem selectedItem = new SelectedItem(query, "google");

                // Check if the query is not already in the recent search list
                List<SelectedItem> recentSearchList = WebsiteRecentSearch.getStringArray(getApplicationContext());
                if (!containsSelectedItem(recentSearchList, query)) {
                    WebsiteRecentSearch.addItem(getApplicationContext(), selectedItem);
                }
            } else {
                showToast("Something went wrong!!!!");
                hideKeyboard();
            }
        });


        playstore.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://search?q=" + URLEncoder.encode(query, "UTF-8")));

                // Start the Play Store activity if the intent can be resolved
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                    hideKeyboard();

                    // Create a SelectedItem object with the query and origin
                    SelectedItem selectedItem = new SelectedItem(query, "playstore");

                    // Check if the query is not already in the recent search list
                    List<SelectedItem> recentSearchList = WebsiteRecentSearch.getStringArray(getApplicationContext());
                    if (!containsSelectedItem(recentSearchList, query)) {
                        WebsiteRecentSearch.addItem(getApplicationContext(), selectedItem);
                    }
                } else {
                    // If Play Store app is not available, open the browser version
                    openPlayStoreInBrowser(query);
                }
            } catch (UnsupportedEncodingException e) {
                showToast("Something went wrong!!!!");
                hideKeyboard();
            }
        });
    }

    // Helper method to open Play Store in the browser if Play Store app is not available
    private void openPlayStoreInBrowser(String query) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/search?q=" + URLEncoder.encode(query, "UTF-8")));
            startActivity(intent);
            hideKeyboard();
            // Create a SelectedItem object with the query and origin
            SelectedItem selectedItem = new SelectedItem(query, "playstore");

            // Check if the query is not already in the recent search list
            List<SelectedItem> recentSearchList = WebsiteRecentSearch.getStringArray(getApplicationContext());
            if (!containsSelectedItem(recentSearchList, query)) {
                WebsiteRecentSearch.addItem(getApplicationContext(), selectedItem);
            }
        } catch (UnsupportedEncodingException ex) {
            showToast("Something went wrong!!!!");
            hideKeyboard();
        }
    }

    // Helper method to check if the recent search list contains a SelectedItem with the given query
    private boolean containsSelectedItem(List<SelectedItem> recentSearchList, String query) {
        for (SelectedItem selectedItem : recentSearchList) {
            if (selectedItem.getSelectedItem().equals(query)) {
                return true;
            }
        }
        return false;
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
        if (appList != null) {
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
    }

    private void adjustScreen() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        softInputAssist.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finishAffinity();
    }

    private void applyTheme() {
        CardView topsearch = findViewById(R.id.topsearch);
        CardView sheet = findViewById(R.id.sheet);

        if (colorKey != null){
            if (!colorKey.equals("none")){
                sheet.setCardBackgroundColor(Color.parseColor(colorKey));
                topsearch.setCardBackgroundColor(Color.parseColor(colorKey));
            }
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
            return Math.min(appInfoList.size(), 8);
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