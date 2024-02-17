package receivers;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.content.ContextCompat;

import services.ShortcutService;
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

}
