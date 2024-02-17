package model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class SearchedApp {
    String appName, packageName;


    private int imageDrawableId;

    public SearchedApp(String appName, String packageName, int imageDrawableId) {
        this.appName = appName;
        this.packageName = packageName;
        this.imageDrawableId = imageDrawableId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getAppIcon(Context context) {
        try {
            // Retrieve the app icon using the package name
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
