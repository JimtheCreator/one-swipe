package adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import float_widgets.Window;
import mappedvision.shortcuts.net.R;
import model.JustInfoApps;

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.ViewHolder> {
    private List<JustInfoApps> appInfoList;
    private Context context;

    public AppInfoAdapter(Context context, List<JustInfoApps> appInfoList) {
        this.context = context;
        this.appInfoList = appInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_apps, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JustInfoApps appInfo = appInfoList.get(position);
        Drawable appIcon =appInfo.getAppIcon(context);

        if (appIcon != null) {
            holder.imageView.setImageDrawable(appIcon);
        }


        holder.itemView.setOnClickListener(v -> {
            String packageName = appInfo.getPackageName();
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                context.startActivity(launchIntent);
            } else {
                // Handle case where app cannot be launched
                showToast("App not found");
            }

            Window window = new Window(context);
            window.closeNew();
        });
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.appIcon);
        }
    }
}

