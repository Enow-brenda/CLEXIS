package com.example.clexis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clexis.R;
import com.google.android.material.tabs.TabLayout;

public class BuddyProgramActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buddy_program_overview);

        View milestone_card = findViewById(R.id.view);
        milestone_card.setOnClickListener(v -> {
            Intent intent = new Intent(this , SubmissionDetailActivity.class);
            startActivity(intent);
        });

        View goalContent = findViewById(R.id.goals);
        View submissionContent = findViewById(R.id.submissions);
        View leaderboardContent = findViewById(R.id.leaderboard);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Login tab
                        goalContent.setVisibility(View.VISIBLE);
                        submissionContent.setVisibility(View.GONE);
                        leaderboardContent.setVisibility(View.GONE);
                        break;
                    case 1: // Signup tab
                        goalContent.setVisibility(View.GONE);
                        submissionContent.setVisibility(View.VISIBLE);
                        leaderboardContent.setVisibility(View.GONE);
                        break;
                    case 2: // Signup tab
                        goalContent.setVisibility(View.GONE);
                        submissionContent.setVisibility(View.GONE);
                        leaderboardContent.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}
