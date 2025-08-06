package com.example.clexis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clexis.R;

public class ForumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum_1);

        View discussion = findViewById(R.id.discussion);
        discussion.setOnClickListener(v -> {
            Intent intent = new Intent(this , ForumDiscussionActivity.class);
            startActivity(intent);
        });
    }

}
