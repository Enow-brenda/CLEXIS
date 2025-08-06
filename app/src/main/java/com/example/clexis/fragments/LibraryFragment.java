package com.example.clexis.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.clexis.R;
import com.example.clexis.activity.ResourceDescriptionActivity;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private LinearLayout llFeaturedContainer, llUserUploadsContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_library, container, false);

        llFeaturedContainer = root.findViewById(R.id.llFeaturedContainer);
        llUserUploadsContainer = root.findViewById(R.id.llUserUploadsContainer);



        String[][] resources = {
                {"Learn Java", "Alice", "01 Aug 2025", "pdf", "true"},
                {"Android Basics", "Bob", "28 July 2025", "book", "false"},
                {"Kotlin Mastery", "Cece", "29 July 2025", "video", "true"},
                {"Thesis on AI", "Diana", "31 July 2025", "thesis", "false"},
                {"Machine Learning Notes", "Eric", "01 Aug 2025", "pdf", "true"},
                {"Creative Writing", "Fatima", "30 July 2025", "doc", "true"}
        };

// Use dynamic lists
        List<String[]> featuredResources = new ArrayList<>();
        List<String[]> communityUploads = new ArrayList<>();

        for (String[] res : resources) {
            if (res[4].equalsIgnoreCase("true")) {
                featuredResources.add(res);
            } else {
                communityUploads.add(res);
            }
        }


        for (String[] res : featuredResources) {
            View card = inflater.inflate(R.layout.item_featured_resource, llFeaturedContainer, false);
            setResourceCard(card, res);
            llFeaturedContainer.addView(card);
        }

        for (String[] res : communityUploads) {
            View card = inflater.inflate(R.layout.item_user_upload, llUserUploadsContainer, false);

            setResourceCard(card, res);
            llUserUploadsContainer.addView(card);
        }

        return root;
    }

    private void setResourceCard(View card, String[] res) {
        ((TextView) card.findViewById(R.id.tvTitle)).setText(res[0]);
        ((TextView) card.findViewById(R.id.tvUploader)).setText("By: " + res[1]);
        ((TextView) card.findViewById(R.id.tvDate)).setText("Uploaded: "+res[2]);
        ImageView icon = card.findViewById(R.id.ivTypeIcon);
        icon.setImageResource(getResourceIcon(res[3]));

        card.setOnClickListener(v -> {
            Intent intent = new Intent(this.getContext(), ResourceDescriptionActivity.class);
            startActivity(intent);
        });

    }

    private int getResourceIcon(String type) {
        switch (type) {
            case "pdf":
                return R.drawable.summary;
            case "book":
                return R.drawable.book;
            case "video":
                return R.drawable.portfolio;
            case "thesis":
                return R.drawable.book;
            case "doc":
                return R.drawable.book;
            default:
                return R.drawable.flashcard;
        }
    }
}
