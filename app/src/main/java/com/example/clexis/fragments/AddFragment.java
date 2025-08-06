package com.example.clexis.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.clexis.R;
import com.example.clexis.activity.AIResourceDetailActivity;

public class AddFragment extends Fragment {
    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        View resourceBox = view.findViewById(R.id.resource);
        resourceBox.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AIResourceDetailActivity.class);
            intent.putExtra("type", "transcribe"); // could be: flashcard, quiz, summary, transcribe
            intent.putExtra("title", "Machine Learning Basics");
            intent.putExtra("uploader", "elkamira");
            startActivity(intent);
        });

        return view;



    }
}
