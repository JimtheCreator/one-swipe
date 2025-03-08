package receivers;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import services.ShortcutService;
import services.TapScrollAccessibility;
import utils.UsageStatsHelper;

public class BroadcastBootLoader extends BroadcastReceiver {

    private static final String SIZE_NAME = "SeekBar";
    private static final String SIZE_KEY = "size";
    String seekBarprogress;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(SIZE_NAME, MODE_PRIVATE);
        seekBarprogress = prefs.getString(SIZE_KEY, "none");

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            boolean accessibilityServiceEnabled = isAccessibilityEnabled(context);
            if (accessibilityServiceEnabled) {
                Intent in = new Intent(context, TapScrollAccessibility.class);
                ContextCompat.startForegroundService(context, in);
            }

            if (UsageStatsHelper.isUsageAccessPermissionGranted(context)) {
                if (!seekBarprogress.equals("0")) {
                    // The permission is granted
                    Intent ii = new Intent(context, ShortcutService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(context, ii);
                    } else {
                        context.startService(ii);
                    }
                }

            }

        }

    }

    private boolean isAccessibilityEnabled(Context context) {
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE_NAME = context.getPackageName() + "/" + TapScrollAccessibility.class.getName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if (settingValue != null) {
                colonSplitter.setString(settingValue);

                while (colonSplitter.hasNext()) {
                    String accessibilityService = colonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE_NAME)) {
                        return true;
                    }
                }
            }
        } else {
            Log.d(TAG, "Accessibility service disabled");
        }
        return false;
    }


}
