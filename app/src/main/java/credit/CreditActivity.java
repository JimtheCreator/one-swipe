package credit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mappedvision.shortcuts.net.R;

public class CreditActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String THEME_KEY = "theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        LinearLayout theother = findViewById(R.id.theother);
        RelativeLayout footer = findViewById(R.id.footer);
        TextView title = findViewById(R.id.title);
        TextView body = findViewById(R.id.body);
        ImageView close = findViewById(R.id.close);

        findViewById(R.id.close).setOnClickListener(v -> finish());

        if (state()){
            // Change status bar color
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.niceBlack)); // Change to your desired color
            window.setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.niceBlack));
            View view = getWindow().getDecorView();

            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }


            theother.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.niceBlack));
            close.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_close_24));
            footer.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.niceBlack));
            title.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            body.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
    }

    private boolean state() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkThemeEnabled = prefs.getBoolean(THEME_KEY, false);


        return darkThemeEnabled;
    }
}