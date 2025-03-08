package new_ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import credit.CreditActivity;
import mappedvision.shortcuts.net.R;
import mappedvision.shortcuts.net.SettingsActivity;
import services.ShortcutService;

public class NewSettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String THEME_KEY = "theme";
    private static final String SIZE_NAME = "SeekBar";
    private static final String SIZE_KEY = "size";
    private static final String COLOR_NAME = "Colour";
    private static final String COLOR_KEY = "value";
    private static final String UI_THEME = "Theme";
    private static final String UI_KEY = "code";
    SwitchCompat switchon;
    View rightview, leftview;
    TextView state, sensitivity_txt, settings_text, c, d, e, f, g, h, i, j, troubleshoot, widgettext;
    ImageView closepage, restart_icon, k, l, m, n, widgeticon, arrowone, arrowthree, arrowfour, arrowfive;
    LinearLayout autorestartLayout, bottomparent, changeUILayout;
    RelativeLayout newUi, autorestart, instagramLay, contact, credits, changesize, overlay, topParent, widgylayout, switchtonewUI;
    boolean isChecked;
    ImageView arrowup, arrowdown;

    AppCompatSeekBar seekBar;
    String seekBarprogress;
    NestedScrollView nested_view;
    Handler getHandler = new Handler();

    String colorKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SettingsTheme); // Replace R.style.AppTheme with your actual theme name
        setContentView(R.layout.activity_new_settings);

        SharedPreferences pref = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        isChecked = pref.getBoolean("Checked", false);

        SharedPreferences sharedPreferences = getSharedPreferences(COLOR_NAME, MODE_PRIVATE);
        colorKey = sharedPreferences.getString(COLOR_KEY, "none");

        SharedPreferences prefs = getSharedPreferences(SIZE_NAME, MODE_PRIVATE);
        seekBarprogress = prefs.getString(SIZE_KEY, "none");

        arrowup = findViewById(R.id.arrowup);
        arrowdown = findViewById(R.id.arrowdown);
        switchon = findViewById(R.id.switchon);
        nested_view = findViewById(R.id.nested_view);
        widgettext = findViewById(R.id.widgettext);
        widgeticon = findViewById(R.id.widgeticon);
        overlay = findViewById(R.id.overlay);
        changesize = findViewById(R.id.changesize);
        rightview = findViewById(R.id.rightview);
        leftview = findViewById(R.id.leftView);
        newUi = findViewById(R.id.newUi);
        changeUILayout = findViewById(R.id.changeUILayout);
        switchtonewUI = findViewById(R.id.switchtonewUI);

        arrowone = findViewById(R.id.arrowone);
        arrowthree = findViewById(R.id.arrowthree);
        arrowfive = findViewById(R.id.arrowfive);
        arrowfour = findViewById(R.id.arrowfour);

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

        widgylayout = findViewById(R.id.widgylayout);
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

        credits.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), NewCreditsActivity.class)));

        instagramLay.setOnClickListener(v -> openInstagram());

        closepage.setOnClickListener(v -> {
            finish();
        });

        contact.setOnClickListener(v -> contactUs());


        widgylayout.setOnClickListener(v -> {
            openDarkWidgetBottomsheet();
        });


        seekBar.setMax(150);

        if (seekBarprogress.equals("none")) {
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
            if (seekBarprogress.equals("0")) {
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
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Handler handler = new Handler();

            handler.postDelayed(() -> {
                finishAndRemoveTask();

                SharedPreferences prefes = getSharedPreferences(SIZE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefes.edit();
                editor.putString(SIZE_KEY, y);
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
                if (stopIntent != null) {
                    stopService(stopIntent);
                }

                Log.d("TAG", "1");

                // Clear the handler by removing any pending messages or tasks
                handler.removeCallbacksAndMessages(null);
            }, 2000);

        });

        newUi.setOnClickListener(new View.OnClickListener() {
            boolean visible;

            @Override
            public void onClick(View v) {
                visible = !visible;
                TransitionManager.beginDelayedTransition(findViewById(R.id.topParent));
                changeUILayout.setVisibility(visible ? View.VISIBLE : View.GONE);

                arrowdown.setVisibility(visible ? View.VISIBLE : View.GONE);
                arrowup.setVisibility(visible ? View.GONE : View.VISIBLE);
            }

        });

        switchon.setChecked(state());

        switchon.setOnClickListener(v -> {
            if (switchon.isChecked()){
                //Visibility OFF
                TransitionManager.beginDelayedTransition(findViewById(R.id.topParent));
                switchtonewUI.setVisibility(View.GONE);
            }else {
                //Visibility ON
                TransitionManager.beginDelayedTransition(findViewById(R.id.topParent));
                switchtonewUI.setVisibility(View.VISIBLE);
            }
        });


        switchtonewUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUI();
            }
        });
    }


    void openDarkWidgetBottomsheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.bottomsheetTheme);

        View view = LayoutInflater.from(this).inflate(R.layout.dark_color_sheet,
                (ViewGroup) findViewById(R.id.source));

        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.offwhite));

        RelativeLayout change = view.findViewById(R.id.change);
        RelativeLayout transparentLayout = view.findViewById(R.id.transparentLayout);
        RelativeLayout solidLayout = view.findViewById(R.id.solidLayout);
        CardView appdrawer = view.findViewById(R.id.appdrawer);
        CardView searchBox = view.findViewById(R.id.box);
        NestedScrollView transparentNest = view.findViewById(R.id.transparentNest);
        NestedScrollView solidNest = view.findViewById(R.id.solidNest);
        ImageView downarrow1 = view.findViewById(R.id.downarrow1);
        ImageView downarrow2 = view.findViewById(R.id.downarrow2);
        ImageView solidicon = view.findViewById(R.id.solidicon);
        ImageView transicon = view.findViewById(R.id.transicon);
        TextView state = view.findViewById(R.id.state);

        ShapeableImageView color1 = view.findViewById(R.id.color1);
        ShapeableImageView color2 = view.findViewById(R.id.color2);
        ShapeableImageView color3 = view.findViewById(R.id.color3);
        ShapeableImageView color4 = view.findViewById(R.id.color4);
        ShapeableImageView color5 = view.findViewById(R.id.color5);
        ShapeableImageView color6 = view.findViewById(R.id.color6);
        ShapeableImageView color7 = view.findViewById(R.id.color7);
        ShapeableImageView color8 = view.findViewById(R.id.color8);
        ShapeableImageView color9 = view.findViewById(R.id.color9);
        ShapeableImageView color10 = view.findViewById(R.id.color10);
        ShapeableImageView colorNONE = view.findViewById(R.id.colorNONE);

        RelativeLayout strokedone = view.findViewById(R.id.strokedone);
        RelativeLayout strokedtwo = view.findViewById(R.id.strokedtwo);
        RelativeLayout strokedthree = view.findViewById(R.id.strokedthree);
        RelativeLayout strokedfour = view.findViewById(R.id.strokedfour);
        RelativeLayout strokedfive = view.findViewById(R.id.strokedfive);
        RelativeLayout strokedsix = view.findViewById(R.id.strokedsix);
        RelativeLayout strokedseven = view.findViewById(R.id.strokedseven);
        RelativeLayout strokedeight = view.findViewById(R.id.strokedeight);
        RelativeLayout strokednine = view.findViewById(R.id.strokednine);
        RelativeLayout strokedten = view.findViewById(R.id.strokedten);
        RelativeLayout strokedeleven = view.findViewById(R.id.strokedeleven);

        checkColor(appdrawer, searchBox, strokedone, strokedtwo, strokedthree, strokedfour, strokedfive, strokedsix, strokedseven, strokedeight, strokednine, strokedten, strokedeleven);

        colorNONE.setOnClickListener(v -> {
            state.setText("none");
            change.setVisibility(View.VISIBLE);
            strokedtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedthree.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfour.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedsix.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedseven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokednine.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedten.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeleven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));


            strokedone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
            appdrawer.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.defaultCOLOR));
            searchBox.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.defaultCOLOR));
        });

        color1.setOnClickListener(v -> {
            String color = "#80FF4081";
            state.setText(color);
            change.setVisibility(View.VISIBLE);
            strokedone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedthree.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfour.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedsix.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedseven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokednine.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedten.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeleven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));


            strokedtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
            appdrawer.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool1));
            searchBox.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool1));
        });

        color2.setOnClickListener(v1 -> {
            String color = "#8081C784";
            state.setText(color);
            change.setVisibility(View.VISIBLE);
            // Change background color of the RelativeLayout
            strokedone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfour.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedsix.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedseven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokednine.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedten.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeleven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));


            strokedthree.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
            appdrawer.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool2));
            searchBox.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool2));
        });

        color3.setOnClickListener(v2 -> {
            String color = "#804CAF50";
            state.setText(color);
            change.setVisibility(View.VISIBLE);
            // Change background color of the RelativeLayout
            strokedone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedthree.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedsix.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedseven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokednine.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedten.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeleven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));

            strokedfour.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
            appdrawer.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool3));
            searchBox.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool3));
        });

        color4.setOnClickListener(v3 -> {
            String color = "#80797DC4";
            state.setText(color);
            change.setVisibility(View.VISIBLE);
            // Change background color of the RelativeLayout
            strokedone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedthree.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfour.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedsix.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedseven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokednine.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedten.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeleven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));

            strokedfive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
            appdrawer.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool4));
            searchBox.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool4));
        });

        color5.setOnClickListener(v4 -> {
            String color = "#80668CFF";
            state.setText(color);
            change.setVisibility(View.VISIBLE);
            // Change background color of the RelativeLayout
            strokedone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedthree.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfour.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedseven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokednine.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedten.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeleven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));

            strokedsix.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
            appdrawer.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool5));
            searchBox.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool5));
        });

        color6.setOnClickListener(v5 -> {
            String color = "#80C2185B";
            state.setText(color);
            change.setVisibility(View.VISIBLE);
            // Change background color of the RelativeLayout
            strokedone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedthree.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfour.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedsix.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokednine.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedten.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeleven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));


            strokedseven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
            appdrawer.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool6));
            searchBox.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool6));
        });

        color7.setOnClickListener(v6 -> {
            String color = "#80FF9800";
            state.setText(color);
            change.setVisibility(View.VISIBLE);
            // Change background color of the RelativeLayout
            strokedone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedthree.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfour.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedsix.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedseven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokednine.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedten.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeleven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));

            strokedeight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
            appdrawer.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool7));
            searchBox.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool7));
        });

        color8.setOnClickListener(v7 -> {
            String color = "#80FF6F00";
            state.setText(color);
            change.setVisibility(View.VISIBLE);
            // Change background color of the RelativeLayout
            strokedone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedthree.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfour.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedsix.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedseven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedten.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeleven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));

            strokednine.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
            appdrawer.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool8));
            searchBox.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool8));
        });

        color9.setOnClickListener(v8 -> {
            String color = "#808BC34A";
            state.setText(color);
            change.setVisibility(View.VISIBLE);
            strokedone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedthree.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfour.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedsix.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedseven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokednine.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeleven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));

            strokedten.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
            appdrawer.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool9));
            searchBox.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool9));
        });

        color10.setOnClickListener(v9 -> {
            String color = "#803F51B5";
            state.setText(color);
            change.setVisibility(View.VISIBLE);
            strokedone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedthree.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfour.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedfive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedsix.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedseven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedeight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokednine.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));
            strokedten.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.invincible_background));

            strokedeleven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
            appdrawer.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool10));
            searchBox.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentCool10));
        });

        solidLayout.setOnClickListener(new View.OnClickListener() {
//            boolean visible;

            @Override
            public void onClick(View v) {
//                visible = !visible;
//
//                solidNest.setVisibility(visible ? View.VISIBLE : View.GONE);
//                downarrow1.setVisibility(visible ? View.VISIBLE : View.GONE);
//                solidicon.setVisibility(visible ? View.GONE : View.VISIBLE);


                showToast("COMING SOON ;))");
            }
        });

        transparentLayout.setOnClickListener(new View.OnClickListener() {
            boolean visible;

            @Override
            public void onClick(View v) {
                visible = !visible;

                transparentNest.setVisibility(visible ? View.VISIBLE : View.GONE);
                downarrow2.setVisibility(visible ? View.VISIBLE : View.GONE);
                transicon.setVisibility(visible ? View.GONE : View.VISIBLE);
            }
        });

        change.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            String gotColor = state.getText().toString();
            Toast toast = Toast.makeText(getApplicationContext(), "Restarting...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            getHandler.postDelayed(() -> {
                finishAndRemoveTask();

                SharedPreferences prefes = getSharedPreferences(COLOR_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefes.edit();
                editor.putString(COLOR_KEY, gotColor);
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
                if (stopIntent != null) {
                    stopService(stopIntent);
                }

                Log.d("TAG", "1");

                // Clear the handler by removing any pending messages or tasks
                getHandler.removeCallbacksAndMessages(null);
            }, 2000);

        });
    }

    private void checkColor(CardView appdrawer, CardView searchBox, RelativeLayout strokedone, RelativeLayout strokedtwo, RelativeLayout strokedthree, RelativeLayout strokedfour, RelativeLayout strokedfive, RelativeLayout strokedsix, RelativeLayout strokedseven, RelativeLayout strokedeight, RelativeLayout strokednine, RelativeLayout strokedten, RelativeLayout strokedeleven) {
        if (colorKey != null) {
            if (!colorKey.equals("none")) {
                Log.d("COLOR CODE TWO", "" + colorKey);
                switch (colorKey) {
                    case "#80FF4081": // two
                        // Do something for black color
                        strokedtwo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
                        appdrawer.setCardBackgroundColor(Color.parseColor(colorKey));
                        searchBox.setCardBackgroundColor(Color.parseColor(colorKey));
                        break;
                    case "#8081C784": // three
                        // Do something for red color
                        strokedthree.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
                        appdrawer.setCardBackgroundColor(Color.parseColor(colorKey));
                        searchBox.setCardBackgroundColor(Color.parseColor(colorKey));
                        break;
                    case "#804CAF50": //four
                        // Do something for white color
                        strokedfour.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
                        appdrawer.setCardBackgroundColor(Color.parseColor(colorKey));
                        searchBox.setCardBackgroundColor(Color.parseColor(colorKey));
                        break;
                    case "#80797DC4": // five
                        // Do something for black color
                        strokedfive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
                        appdrawer.setCardBackgroundColor(Color.parseColor(colorKey));
                        searchBox.setCardBackgroundColor(Color.parseColor(colorKey));
                        break;
                    case "#80668CFF": // six
                        // Do something for red color
                        strokedsix.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
                        appdrawer.setCardBackgroundColor(Color.parseColor(colorKey));
                        searchBox.setCardBackgroundColor(Color.parseColor(colorKey));
                        break;
                    // Add more cases as needed
                    case "#80C2185B": // seven
                        // Do something for red color
                        strokedseven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
                        appdrawer.setCardBackgroundColor(Color.parseColor(colorKey));
                        searchBox.setCardBackgroundColor(Color.parseColor(colorKey));
                        break;

                    case "#80FF9800": // eight
                        // Do something for red color
                        strokedeight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
                        appdrawer.setCardBackgroundColor(Color.parseColor(colorKey));
                        searchBox.setCardBackgroundColor(Color.parseColor(colorKey));
                        break;

                    case "#80FF6F00": // nine
                        // Do something for red color
                        strokednine.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
                        appdrawer.setCardBackgroundColor(Color.parseColor(colorKey));
                        searchBox.setCardBackgroundColor(Color.parseColor(colorKey));
                        break;

                    case "#808BC34A": // ten
                        // Do something for red color
                        strokedten.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
                        appdrawer.setCardBackgroundColor(Color.parseColor(colorKey));
                        searchBox.setCardBackgroundColor(Color.parseColor(colorKey));
                        break;

                    case "#803F51B5": // eleven
                        // Do something for red color
                        strokedeleven.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
                        appdrawer.setCardBackgroundColor(Color.parseColor(colorKey));
                        searchBox.setCardBackgroundColor(Color.parseColor(colorKey));
                        break;

                    default:
                        Log.d("COLOR CODE", "" + colorKey);
                        break;
                }


            } else {
                strokedone.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.red_stroke));
            }
        }


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
            Toast toast = Toast.makeText(NewSettingsActivity.this, "Kindly install an email app and try again", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void openInstagram() {
        Uri uri = Uri.parse("https://www.instagram.com/wire.finds_n_builds/");

        Intent launchIntent = new Intent(Intent.ACTION_VIEW, uri);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchIntent.setPackage("com.instagram.android");

        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/wire.finds_n_builds/")));
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

    private void newUI() {
        SharedPreferences prefs = getSharedPreferences(UI_THEME, MODE_PRIVATE);
        boolean isNewUi = prefs.getBoolean(UI_KEY, false);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(UI_KEY, false);
        editor.apply();


        Toast toast = Toast.makeText(getApplicationContext(), "Restarting app...", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            finishAndRemoveTask();
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
        SharedPreferences prefs = getSharedPreferences(UI_THEME, MODE_PRIVATE);
        boolean isNewUi = prefs.getBoolean(UI_KEY, false);

        return isNewUi;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getHandler.removeCallbacksAndMessages(null);
    }
}