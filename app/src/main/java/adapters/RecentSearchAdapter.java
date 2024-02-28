package adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mappedvision.shortcuts.net.R;
import model.Item;
import model.Recents;
import utils.WebsiteRecentSearch;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.ViewHolder> {
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

    List<String> stringList;
    Activity activity;

    public RecentSearchAdapter(Context context, List<String> stringList, Activity activity) {
        this.context = context;
        this.stringList = stringList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecentSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_search, parent, false);
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
    public void onBindViewHolder(@NonNull RecentSearchAdapter.ViewHolder holder, int position) {
        String string = stringList.get(position);

        if (position == getItemCount()-1){
            holder.stroke.setVisibility(View.GONE);
        }


        // Set the maximum length to 5 characters
        int maxLength = 20;

        // Check if the text exceeds the maximum length
        if (string.length() > maxLength) {
            // Truncate the text and append ellipsis
            String truncatedText = string.substring(0, maxLength) + "...";
            holder.searched_txt.setText(truncatedText);
        } else {
            // Display the original text if it doesn't exceed the maximum length
            holder.searched_txt.setText(string);
        }


        holder.itemView.setOnClickListener(v->{
            openLink(string);
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

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView searched_txt;
        View stroke;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            searched_txt = itemView.findViewById(R.id.searched_txt);
            stroke = itemView.findViewById(R.id.stroke);
        }
    }


    private void openLink(String link) {
        try {
            WebsiteRecentSearch.addItem(context, link);
            // Create an intent to open the link in a web browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle the case where no activity is found to handle the intent
            showToast("No browser found to open");
        } catch (Exception e) {
            // Handle other exceptions
            showToast("Error occurred while opening the link");
            e.printStackTrace();
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -350);
        toast.show();
    }

}
