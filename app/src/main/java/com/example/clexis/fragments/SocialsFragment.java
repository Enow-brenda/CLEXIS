package com.example.clexis.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.clexis.R;
import com.example.clexis.activity.BuddyProgramListActivity;
import com.example.clexis.activity.ChatListActivity;
import com.example.clexis.activity.ForumActivity;

public class SocialsFragment extends Fragment {
    public SocialsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_socials, container, false);
        View forum = view.findViewById(R.id.forum);
        View buddy = view.findViewById(R.id.buddy);
        View chats = view.findViewById(R.id.chats);
        forum.setOnClickListener(v -> {
            Intent intent = new Intent(this.getContext(), ForumActivity.class);
            startActivity(intent);
        });
        chats.setOnClickListener(v -> {
            Intent intent = new Intent(this.getContext(), ChatListActivity.class);
            startActivity(intent);
        });
        buddy.setOnClickListener(v -> {
            Intent intent = new Intent(this.getContext(), BuddyProgramListActivity.class);
            startActivity(intent);
        });
        return view;
    }
}
