package mappedvision.shortcuts.net;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import services.ShortcutService;
import utils.UsageStatsHelper;

public class PermissionActivity extends AppCompatActivity {

    LinearLayout first_screen, secondscreen;
    RelativeLayout proceedbtn;
    TextView policytext, btnTxt;
    View secondview;
    boolean isAccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_permission);

        first_screen = findViewById(R.id.first_screen);
        secondview = findViewById(R.id.secondview);
        secondscreen = findViewById(R.id.secondscreen);
        btnTxt = findViewById(R.id.btnTxt);
        proceedbtn = findViewById(R.id.proceedbtn);
        policytext = findViewById(R.id.policytext);


        String note = "By continuing, you agree to our Privacy Policy";
        SpannableString spannableString = new SpannableString(note);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                gotoURL();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.offwhite));
                ds.setUnderlineText(true);
                ds.clearShadowLayer();
            }
        };


        spannableString.setSpan(clickableSpan1, 32, 46, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        policytext.setText(spannableString);
        policytext.setMovementMethod(LinkMovementMethod.getInstance());


        proceedbtn.setOnClickListener(v -> {
            String state = btnTxt.getText().toString();
            if (state.equals("Continue")) {
                policytext.setVisibility(View.GONE);
                secondscreen.setVisibility(View.VISIBLE);
                first_screen.setVisibility(View.GONE);
                secondview.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.niceblue));
                btnTxt.setText("Allow Usage Access");
            } else {
                requestUsageStatsPermission();
            }

        });

    }

    private void gotoURL() {
        try {
            Uri uri = Uri.parse("https://sites.google.com/view/privacy-policy-oneswipe/home");
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }catch (Exception e ){
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (UsageStatsHelper.isUsageAccessPermissionGranted(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }


    private void requestUsageStatsPermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

}