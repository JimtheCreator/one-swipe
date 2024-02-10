package mappedvision.shortcuts.net;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout autorestartLayout;
    RelativeLayout autorestart, instagramLay, contact;
    TextView state;
    ImageView closepage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        closepage = findViewById(R.id.closepage);
        instagramLay = findViewById(R.id.instagramLay);
        autorestartLayout = findViewById(R.id.autorestartLayout);
        autorestart = findViewById(R.id.autorestart);
        state = findViewById(R.id.state);
        contact = findViewById(R.id.contact);
        checkmanufacturer();

        autorestart.setOnClickListener(v->{
            moveToSettings();
        });

        instagramLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInstagram();
            }
        });


        closepage.setOnClickListener(v->{
            finish();
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUs();
            }
        });

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
        Uri uri = Uri.parse("https://www.instagram.com/devy_the_dev/");

        Intent launchIntent = new Intent(Intent.ACTION_VIEW, uri);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchIntent.setPackage("com.instagram.android");

        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/devy_the_dev/")));
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

}