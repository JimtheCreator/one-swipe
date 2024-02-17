package mappedvision.shortcuts.net;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import credit.CreditActivity;
import services.ShortcutService;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String THEME_KEY = "theme";

    private static final String SIZE_NAME = "SeekBar";
    private static final String SIZE_KEY = "size";
    View rightview, leftview;
//    TextView packagetxt,a, b;
//    RelativeLayout first_box;
    TextView state, sensitivity_txt, settings_text, c, d,e,f,g,h,i,j, troubleshoot;
    ImageView closepage, restart_icon,k,l,m,n;
    LinearLayout autorestartLayout, bottomparent;
    RelativeLayout autorestart, instagramLay, contact, credits, theme, changesize, overlay, topParent;
    //    SwitchCompat switchCompat;
    boolean isChecked;

    AppCompatSeekBar seekBar;
    String seekBarprogress;
    NestedScrollView nested_view;

    @Override
    protected void onStart() {
        super.onStart();
        applyTheme();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        isChecked = pref.getBoolean("Checked", false);

        SharedPreferences prefs = getSharedPreferences(SIZE_NAME, MODE_PRIVATE);
        seekBarprogress = prefs.getString(SIZE_KEY, "none");

        nested_view = findViewById(R.id.nested_view);
//        switchCompat = findViewById(R.id.switchCompat);
        theme = findViewById(R.id.theme);
//        packagetxt = findViewById(R.id.packagetxt);
        overlay = findViewById(R.id.overlay);
        changesize = findViewById(R.id.changesize);
        rightview = findViewById(R.id.rightview);
        leftview = findViewById(R.id.leftView);

//        a = findViewById(R.id.a);
//        b = findViewById(R.id.b);
        c = findViewById(R.id.c);
        d = findViewById(R.id.d);
        e = findViewById(R.id.e);
        f = findViewById(R.id.f);
        g = findViewById(R.id.g);
        h = findViewById(R.id.h);
        i = findViewById(R.id.i);
        j = findViewById(R.id.j);
        k = findViewById(R.id.k);
        l = findViewById(R.id.l);
        m = findViewById(R.id.m);
        n = findViewById(R.id.n);

        restart_icon = findViewById(R.id.restart_icon);
        bottomparent = findViewById(R.id.bottomparent);
        settings_text = findViewById(R.id.settings_text);
        topParent = findViewById(R.id.topParent);
        sensitivity_txt = findViewById(R.id.sensitivity_txt);
//        first_box = findViewById(R.id.first_box);
        troubleshoot = findViewById(R.id.troubleshoot);
        closepage = findViewById(R.id.closepage);
        instagramLay = findViewById(R.id.instagramLay);
        autorestartLayout = findViewById(R.id.autorestartLayout);
        autorestart = findViewById(R.id.autorestart);
        seekBar = findViewById(R.id.seekBar);
        credits = findViewById(R.id.credits);
        state = findViewById(R.id.state);
        contact = findViewById(R.id.contact);
        restart_icon = findViewById(R.id.restart_icon);
        checkmanufacturer();

        autorestart.setOnClickListener(v -> {
            moveToSettings();
        });

        credits.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CreditActivity.class)));

        instagramLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInstagram();
            }
        });


        closepage.setOnClickListener(v -> {
            finish();
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUs();
            }
        });


        theme.setOnClickListener(v -> {
            if (state()){
                openDarkBottomsheet();
            }else {
                openBottomsheet();
            }

        });

        seekBar.setMax(150);

        if (seekBarprogress.equals("none")){
            sensitivity_txt.setText("50%");
            seekBar.setProgress(50);

            // Define the height you want programmatically
            int desiredHeightInPixels = getResources().getDisplayMetrics().heightPixels / 4; // Change this to whatever height you need
            // Get the current layout params of the view
            ViewGroup.LayoutParams params = rightview.getLayoutParams(); // Assuming 'rightview' is the view you want to modify
            ViewGroup.LayoutParams leftParams = leftview.getLayoutParams(); // Assuming 'leftView' is another view you want to modify
            // Update the height property of the layout params
            params.height = desiredHeightInPixels;
            params.width = 50;
            leftParams.height = desiredHeightInPixels;
            leftParams.width = 50;

            leftview.setLayoutParams(leftParams);
            rightview.setLayoutParams(params);
        }

        else {
            sensitivity_txt.setText(seekBarprogress + "%");
            seekBar.setProgress(Integer.parseInt(seekBarprogress));

            // Define the height you want programmatically
            int desiredHeightInPixels = getResources().getDisplayMetrics().heightPixels / 4; // Change this to whatever height you need
            // Get the current layout params of the view
            ViewGroup.LayoutParams params = rightview.getLayoutParams(); // Assuming 'rightview' is the view you want to modify
            ViewGroup.LayoutParams leftParams = leftview.getLayoutParams(); // Assuming 'leftView' is another view you want to modify
            // Update the height property of the layout params
            params.height = desiredHeightInPixels;
            params.width = Integer.parseInt(seekBarprogress);
            leftParams.height = desiredHeightInPixels;
            leftParams.width = Integer.parseInt(seekBarprogress);

            leftview.setLayoutParams(leftParams);
            rightview.setLayoutParams(params);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensitivity_txt.setText(progress + "%");
                overlay.setVisibility(View.VISIBLE);

                // Define the height you want programmatically
                int desiredHeightInPixels = getResources().getDisplayMetrics().heightPixels / 4; // Change this to whatever height you need
                // Get the current layout params of the view
                ViewGroup.LayoutParams params = rightview.getLayoutParams(); // Assuming 'rightview' is the view you want to modify
                ViewGroup.LayoutParams leftParams = leftview.getLayoutParams(); // Assuming 'leftView' is another view you want to modify
                // Update the height property of the layout params
                params.height = desiredHeightInPixels;
                params.width = progress;
                leftParams.height = desiredHeightInPixels;
                leftParams.width = progress;

                leftview.setLayoutParams(leftParams);
                rightview.setLayoutParams(params);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                changesize.setVisibility(View.VISIBLE);
                overlay.setVisibility(View.GONE);
            }
        });

        changesize.setOnClickListener(v -> {
            changesize.setEnabled(false);

            Log.d("TAG", "0");
            if (seekBarprogress.equals("0")){
                // From an Activity
                Intent stopIntent = new Intent(this, ShortcutService.class);
                stopService(stopIntent);

                Log.d("TAG", "1");
            }

            nested_view.setEnabled(false);
            String y = String.valueOf(seekBar.getProgress());

            SharedPreferences preferences = getSharedPreferences(SIZE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.remove(SIZE_KEY);
            edit.apply();

            Toast toast = Toast.makeText(getApplicationContext(), "Restarting app...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            Handler handler = new Handler();

            handler.postDelayed(() -> {
                finishAndRemoveTask();

                SharedPreferences prefes = getSharedPreferences(SIZE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefes.edit();
                editor.putString(SIZE_KEY,y);
                editor.apply();

                toast.cancel();

                Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());

                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // Handle the case where the intent is null
                    Log.e("RestartApp", "Intent is null");
                }

                // From an Activity
                Intent stopIntent = new Intent(this, ShortcutService.class);
                if (stopIntent != null){
                    stopService(stopIntent);
                }

                Log.d("TAG", "1");

                // Clear the handler by removing any pending messages or tasks
                handler.removeCallbacksAndMessages(null);
            }, 2000);


        });
    }

    void openBottomsheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.bottomsheetTheme);

        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_layout,
                (ViewGroup) findViewById(R.id.rootbottom));

        bottomSheetDialog.setCancelable(true);

        bottomSheetDialog.setContentView(view);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.offwhite));


        ViewGroup viewGroup = view.findViewById(R.id.rootbottom);
        RelativeLayout lightmode = view.findViewById(R.id.lightmode);
        RelativeLayout darkmode = view.findViewById(R.id.darkmode);
        RelativeLayout change = view.findViewById(R.id.change);
        TextView state = view.findViewById(R.id.state);

        ImageView checktwo = view.findViewById(R.id.checktwo);
        ImageView checkone = view.findViewById(R.id.checkone);

        state();

        if (state()) {
            checkone.setVisibility(View.GONE);
            checktwo.setVisibility(View.VISIBLE);
        } else {
            checktwo.setVisibility(View.GONE);
            checkone.setVisibility(View.VISIBLE);
        }

        lightmode.setOnClickListener(v -> {
//            bottomSheetDialog.dismiss();
            checktwo.setVisibility(View.GONE);
            checkone.setVisibility(View.VISIBLE);

//            TransitionManager.beginDelayedTransition(viewGroup);
            change.setVisibility(View.VISIBLE);
            state.setText("Light");
        });

        darkmode.setOnClickListener(v -> {
//            bottomSheetDialog.dismiss();
            checkone.setVisibility(View.GONE);
            checktwo.setVisibility(View.VISIBLE);

//            TransitionManager.beginDelayedTransition(viewGroup);
            change.setVisibility(View.VISIBLE);
            state.setText("Dark");
        });

        change.setOnClickListener(v -> {
            bottomSheetDialog.setCancelable(false);
            if (state.getText().toString().equals("Light")) {
                toggleLightTheme(bottomSheetDialog);
            } else {
                toggleDarkTheme(bottomSheetDialog);
            }
        });


        bottomSheetDialog.show();
    }

    void openDarkBottomsheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.bottomsheetTheme);

        View view = LayoutInflater.from(this).inflate(R.layout.darksheet,
                (ViewGroup) findViewById(R.id.rootbottom));

        bottomSheetDialog.setCancelable(true);

        bottomSheetDialog.setContentView(view);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));


        ViewGroup viewGroup = view.findViewById(R.id.rootbottom);
        RelativeLayout lightmode = view.findViewById(R.id.lightmode);
        RelativeLayout darkmode = view.findViewById(R.id.darkmode);
        RelativeLayout change = view.findViewById(R.id.change);
        TextView state = view.findViewById(R.id.state);

        ImageView checktwo = view.findViewById(R.id.checktwo);
        ImageView checkone = view.findViewById(R.id.checkone);

        state();

        if (state()) {
            checkone.setVisibility(View.GONE);
            checktwo.setVisibility(View.VISIBLE);
        } else {
            checktwo.setVisibility(View.GONE);
            checkone.setVisibility(View.VISIBLE);
        }

        lightmode.setOnClickListener(v -> {
//            bottomSheetDialog.dismiss();
            checktwo.setVisibility(View.GONE);
            checkone.setVisibility(View.VISIBLE);

//            TransitionManager.beginDelayedTransition(viewGroup);
            change.setVisibility(View.VISIBLE);
            state.setText("Light");
        });

        darkmode.setOnClickListener(v -> {
//            bottomSheetDialog.dismiss();
            checkone.setVisibility(View.GONE);
            checktwo.setVisibility(View.VISIBLE);

//            TransitionManager.beginDelayedTransition(viewGroup);
            change.setVisibility(View.VISIBLE);
            state.setText("Dark");
        });

        change.setOnClickListener(v -> {
            bottomSheetDialog.setCancelable(false);
            if (state.getText().toString().equals("Light")) {
                toggleLightTheme(bottomSheetDialog);
            } else {
                toggleDarkTheme(bottomSheetDialog);
            }
        });

        bottomSheetDialog.show();
    }


    private void checkmanufacturer() {
        try {
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                autorestartLayout.setVisibility(View.VISIBLE);
                state.setText("Enable Autostart");
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                autorestartLayout.setVisibility(View.VISIBLE);
                state.setText("Enable Startup");
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                autorestartLayout.setVisibility(View.VISIBLE);
                state.setText("Enable Autostart");
            } else if ("huawei".equalsIgnoreCase(manufacturer)) {
                autorestartLayout.setVisibility(View.VISIBLE);
                state.setText("Enable Protect Startup");
            }

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void contactUs() {
        // Example data
        String emailSubject = "FEEDBACK";
        String[] emailReceiver = {"jimmywire@skiff.com"};

        // Create an Intent with the ACTION_SENDTO action
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

        // Set the data for the intent (specify the "mailto" scheme)
        emailIntent.setData(Uri.parse("mailto:"));

        // Set the email subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);

        // Set the email recipients
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailReceiver);

        // Verify if there's an email app to handle the intent
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            // Start the activity
            startActivity(emailIntent);
        } else {
            Toast toast = Toast.makeText(SettingsActivity.this, "Kindly install an email app and try again", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void openInstagram() {
        Uri uri = Uri.parse("https://www.instagram.com/wire.develops/");

        Intent launchIntent = new Intent(Intent.ACTION_VIEW, uri);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchIntent.setPackage("com.instagram.android");

        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/wire.develops/")));
        }
    }

    void moveToSettings() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;

            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("huawei".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            } else {
                // Handle other manufacturers or provide a default behavior
            }

            PackageManager packageManager = getApplicationContext().getPackageManager();
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            if (list != null && list.size() > 0) {
                startActivity(intent);
            } else {
                showToast("Something went wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void toggleLightTheme(BottomSheetDialog bottomSheetDialog) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkThemeEnabled = prefs.getBoolean(THEME_KEY, false);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(THEME_KEY, false);
        editor.apply();

        Toast toast = Toast.makeText(getApplicationContext(), "Restarting app...", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            finishAndRemoveTask();
            bottomSheetDialog.dismiss();
            toast.cancel();
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                // Handle the case where the intent is null
                Log.e("RestartApp", "Intent is null");
            }

            // Clear the handler by removing any pending messages or tasks
            handler.removeCallbacksAndMessages(null);
        }, 2000);


    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkThemeEnabled = prefs.getBoolean(THEME_KEY, false);

        if (darkThemeEnabled) {
            setDarkTheme();
        }
    }

    private void setDarkTheme() {
        // Change status bar color
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.darkTheme)); // Change to your desired color
        window.setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        View view = getWindow().getDecorView();
        if (view != null) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


        bottomparent.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        topParent.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.darkTheme));
        closepage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_close_24));
        restart_icon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.white_restart_alt_24));
        settings_text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        troubleshoot.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        state.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
//        a.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
//        b.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey));
        c.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        d.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        e.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        f.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        g.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        h.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        i.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        j.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        k.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.white_generating_tokens_24));
        l.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.white_handshake_24));
        m.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ig));
        n.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.white_alternate_email_24));

//        packagetxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey));
//        first_box.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dark_drawable));

    }

    private void toggleDarkTheme(BottomSheetDialog bottomSheetDialog) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkThemeEnabled = prefs.getBoolean(THEME_KEY, false);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(THEME_KEY, true);
        editor.apply();


        Toast toast = Toast.makeText(getApplicationContext(), "Restarting app...", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            finishAndRemoveTask();
            bottomSheetDialog.dismiss();
            toast.cancel();
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                // Handle the case where the intent is null
                Log.e("RestartApp", "Intent is null");
            }

            // Clear the handler by removing any pending messages or tasks
            handler.removeCallbacksAndMessages(null);
        }, 2000);
    }

    private boolean state() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkThemeEnabled = prefs.getBoolean(THEME_KEY, false);


        return darkThemeEnabled;
    }

}