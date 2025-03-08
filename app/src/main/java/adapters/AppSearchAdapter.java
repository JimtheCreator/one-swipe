package adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mappedvision.shortcuts.net.R;
import model.JustInfoApps;
import model.SearchedApp;

public class AppSearchAdapter extends RecyclerView.Adapter<AppSearchAdapter.ViewHolder> {
    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */

    Context context;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String THEME_KEY = "theme";
    List<SearchedApp> searchedAppList;
    Activity activity;


    public AppSearchAdapter(Context context, List<SearchedApp> searchedAppList, Activity activity) {
        this.context = context;
        this.searchedAppList = searchedAppList;
        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.searched_app, parent, false);
        return new ViewHolder(view);
    }


    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchedApp appInfo = searchedAppList.get(position);

        if (appInfo.getAppIcon(context) != null){
            holder.imageView.setImageDrawable(appInfo.getAppIcon(context));

            String textToDisplay = appInfo.getAppName();

            // Set the maximum length to 5 characters
            int maxLength = 10;

            // Check if the text exceeds the maximum length
            if (textToDisplay.length() > maxLength) {
                // Truncate the text and append ellipsis
                String truncatedText = textToDisplay.substring(0, maxLength) + "...";
                holder.appName.setText(truncatedText);
            } else {
                // Display the original text if it doesn't exceed the maximum length
                holder.appName.setText(appInfo.getAppName());
            }



        }

        if (state()){
            holder.appName.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.itemView.setOnClickListener(v -> {
            String packageName = appInfo.getPackageName();
            openApp(packageName);
            hideKeyboard();
        });
    }

    private void hideKeyboard() {
        // Get the input method manager
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        // Check if no view has focus
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(context);
        }

        // Hide the keyboard
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -350);
        toast.show();
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return Math.min(searchedAppList.size(), 4);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView appName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
        }
    }

    public void filterList(List<SearchedApp> filteredList) {
        this.searchedAppList = filteredList;
        notifyDataSetChanged();
    }

    public void updateList(List<SearchedApp> newList) {
        if (newList != null) {
            this.searchedAppList.clear(); // Make sure searchedApps is initialized properly
//            this.searchedAppList.addAll(newList);

            searchedAppList.addAll(newList);
            notifyDataSetChanged();
        } else {
            // Handle the case when newList is null
            Log.e("AppSearchAdapter", "New list is null");
        }
    }

    private boolean state() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkThemeEnabled = prefs.getBoolean(THEME_KEY, false);


        return darkThemeEnabled;
    }



}
