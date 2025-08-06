package com.example.clexis.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.clexis.R;
import com.example.clexis.activity.AIResourceDetailActivity;
import com.example.clexis.activity.AchievementActivity;
import com.example.clexis.activity.NotificationActivity;
import com.example.clexis.activity.ScheduleActivity;
import com.example.clexis.activity.ViewLearningPlanActivity;

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        View lpath = view.findViewById(R.id.lpath);
        View portfolio = view.findViewById(R.id.portfolio);
        View notification = view.findViewById(R.id.notification);
        View schedule = view.findViewById(R.id.schedule);
        lpath.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ViewLearningPlanActivity.class);
            startActivity(intent);
        });
        portfolio.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AchievementActivity.class);
            startActivity(intent);
        });
        notification.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), NotificationActivity.class);
            startActivity(intent);
        });
        schedule.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ScheduleActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
