package model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

//public class JustInfoApps {
//    private String packageName;
//    private int imageDrawableId;
//
//    public JustInfoApps(String packageName, int imageDrawableId) {
//        this.packageName = packageName;
//        this.imageDrawableId = imageDrawableId;
//    }
//
//
//    public String getPackageName() {
//        return packageName;
//    }
//
//    public int getImageDrawableId() {
//        return imageDrawableId;
//    }
//
//    public Drawable getAppIcon(Context context) {
//        try {
//            return context.getPackageManager().getApplicationIcon(packageName);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}

public class JustInfoApps {
    private String packageName;
    private int imageDrawableId;

    public JustInfoApps(String packageName, int imageDrawableId) {
        this.packageName = packageName;
        this.imageDrawableId = imageDrawableId;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getImageDrawableId() {
        return imageDrawableId;
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

