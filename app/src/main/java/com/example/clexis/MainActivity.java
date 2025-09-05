package com.example.clexis;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.clexis.activity.AuthActivity;
import com.example.clexis.activity.HomeActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ImageView flagImage;
    private LinearLayout languageToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(); // Apply saved language
        EdgeToEdge.enable(this);

        SessionManager session = new SessionManager(this);

        if (session.isLoggedIn()) {
            // Go to Dashboard
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            // Show Landing Page
            setContentView(R.layout.activity_main);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(0,systemBars.top,0,systemBars.bottom);
                return insets;
            });
            LottieAnimationView animationView = findViewById(R.id.lottieAnimationView);


            animationView.setAnimation(R.raw.landing); // From raw folder

            animationView.setSpeed(1.0f);
            animationView.setRepeatCount(LottieDrawable.INFINITE); // For continuous looping

            animationView.playAnimation();

            Button auth = findViewById(R.id.startBtn);
            auth.setOnClickListener(v->{
                Intent authIntent = new Intent(this, AuthActivity.class);
                startActivity(authIntent);
            });

            languageToggle = findViewById(R.id.language_toggle);
            flagImage = languageToggle.findViewById(R.id.lang_flag);


            // Display current language
            setLanguageUI(getSavedLangCode());

            // Handle click
            languageToggle.setOnClickListener(v -> {
                Log.d("LandingActivity", "Language switcher clicked!");
                Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
                showLanguageDialog();
            });

        }



    }

    private void showLanguageDialog() {
        final String[] languageNames = {"English", "Français"};
        final String[] languageCodes = {"en", "fr"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Language");
        builder.setItems(languageNames, (dialog, which) -> {
            setLocale(languageCodes[which]);
            recreate();
        });

        builder.show();
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Save preference
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", langCode);
        editor.apply();
    }

    private void loadLocale() {
        String lang = getSavedLangCode();
        setLocale(lang);
    }

    private String getSavedLangCode() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        return prefs.getString("My_Lang", "en");
    }

    private void setLanguageUI(String langCode) {
        if (langCode.equals("fr")) {
            flagImage.setImageResource(R.drawable.flag_fr);

        } else {
            flagImage.setImageResource(R.drawable.flag_en);
        }
    }
}