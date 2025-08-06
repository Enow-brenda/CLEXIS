package com.example.clexis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clexis.R;

public class ChatListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chats1);

        View discussion = findViewById(R.id.chatbox);
        View add = findViewById(R.id.btn_new_chat);
        discussion.setOnClickListener(v -> {
            Intent intent = new Intent(this , ChatRoomActivity.class);
            startActivity(intent);
        });
        add.setOnClickListener(v -> {
            Intent intent = new Intent(this , NewChatActivity.class);
            startActivity(intent);
        });
    }

}

