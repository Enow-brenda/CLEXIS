package com.example.clexis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clexis.R;

public class BuddyProgramListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buddy_list);

        View program = findViewById(R.id.program1);
        View add = findViewById(R.id.addProgram);
        program.setOnClickListener(v -> {
            Intent intent = new Intent(this , BuddyProgramActivity.class);
            startActivity(intent);
        });
        add.setOnClickListener(v -> {
            Intent intent = new Intent(this , AddBuddyProgramActivity.class);
            startActivity(intent);
        });
    }
}
