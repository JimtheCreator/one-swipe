package new_ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import mappedvision.shortcuts.net.R;

public class NewCreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SettingsTheme); // Replace R.style.AppTheme with your actual theme name

        setContentView(R.layout.activity_new_credits);

        findViewById(R.id.close).setOnClickListener(v -> finish());
    }
}