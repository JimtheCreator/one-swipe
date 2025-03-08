package adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mappedvision.shortcuts.net.R;
import model.JustInfoApps;
import utils.SharedPrefUtils;

public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.ViewHolder> {

    Context context;
    List<JustInfoApps> justInfoAppsList;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String THEME_KEY = "theme";

    private static final String SIZE_NAME = "SeekBar";
    private static final String SIZE_KEY = "size";
    String seekBarprogress;

    public FavouriteListAdapter(Context context, List<JustInfoApps> justInfoAppsList) {
        this.context = context;
        this.justInfoAppsList = justInfoAppsList;
    }

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


    @NonNull
    @Override
    public FavouriteListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_layout, parent, false);
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
    public void onBindViewHolder(@NonNull FavouriteListAdapter.ViewHolder holder, int position) {
        JustInfoApps appInfo = justInfoAppsList.get(position);

        SharedPreferences prefs = context.getSharedPreferences(SIZE_NAME, MODE_PRIVATE);
        seekBarprogress = prefs.getString(SIZE_KEY, "none");

        holder.bind(appInfo);

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

            if (justInfoAppsList == null || position < 0 || position >= justInfoAppsList.size()) {
                showToast("Slow down!!!");
                return;
            }

            // Remove the item from the dataset
            JustInfoApps removedApp = justInfoAppsList.remove(position);

            // Notify the adapter
            notifyItemRemoved(position);

            // Update the positions of other items in the list
            if (position != justInfoAppsList.size()) {
                notifyItemRangeChanged(position, justInfoAppsList.size() - position);
            }else {
                Log.d("TAG","EXCEPTION THROWN");
            }

            SharedPrefUtils.removeAppInfo(context, removedApp);
        });

    }


    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return justInfoAppsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView appIcon, checked;
        RelativeLayout shadow;
        ViewGroup clicked_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clicked_icon = itemView.findViewById(R.id.clicked_icon);
            appIcon = itemView.findViewById(R.id.appIcon);
            shadow = itemView.findViewById(R.id.shadow);
            checked = itemView.findViewById(R.id.checked);
        }

        public void bind(JustInfoApps item) {
            // Bind data to your views here
            appIcon.setImageDrawable(item.getAppIcon(context));
        }
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
}
