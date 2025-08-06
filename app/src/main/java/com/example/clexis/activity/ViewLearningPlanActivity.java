package com.example.clexis.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.clexis.R;
import com.google.android.material.tabs.TabLayout;

public class ViewLearningPlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_learning_plan);

        LinearLayout modules = findViewById(R.id.moduleFragment);
        LinearLayout today = findViewById(R.id.dailyTaskFragment);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Login tab
                        modules.setVisibility(View.VISIBLE);
                        today.setVisibility(View.GONE);
                        break;
                    case 1: // Signup tab
                        modules.setVisibility(View.GONE);
                        today.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }


}