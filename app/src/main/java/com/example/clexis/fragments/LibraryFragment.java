package com.example.clexis.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.clexis.R;
import com.example.clexis.activity.AddResourceActivity;
import com.example.clexis.activity.DownloadActivity;
import com.example.clexis.activity.ResourceDescriptionActivity;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.entity.LearningPath;
import com.example.clexis.models.entity.Resource;
import com.example.clexis.models.enums.ResourceType;
import com.example.clexis.models.response.ResponseDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryFragment extends Fragment {

    private LinearLayout llFeaturedContainer, llUserUploadsContainer;
    private ProgressDialog progressDialog;
    private ApiService api;
    private List<Resource> resources;
    private LayoutInflater inflater;
    private ViewGroup content,empty;

    private TabLayout tabLayout;

    private TextView fheading,cheading;

    private FloatingActionButton btn;

    @Override
    public void onResume() {
        super.onResume();
        setupFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater2, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater2.inflate(R.layout.fragment_library, container, false);
        inflater = inflater2;
        content = root.findViewById(R.id.content);
        empty = root.findViewById(R.id.empty);

        fheading = root.findViewById(R.id.FHeading);
        cheading = root.findViewById(R.id.CHeading);

        llFeaturedContainer = root.findViewById(R.id.llFeaturedContainer);
        llUserUploadsContainer = root.findViewById(R.id.llUserUploadsContainer);

        api = ApiClient.getRetrofitInstance(this.getContext()).create(ApiService.class);
        tabLayout = root.findViewById(R.id.tabLayout);
        btn = root.findViewById(R.id.fabUpload);
        btn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddResourceActivity.class));
        });



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Called when a tab enters the selected state
                String tabText = tab.getText().toString();
                switch (tabText) {
                    case "All":
                        showResources("all");
                        break;
                    case "Books":
                        showResources("book");
                        break;
                    case "Thesis":
                        showResources("thesis");
                        break;
                    case "User Uploads":
                        showResources("user");
                        break;
                    case "Papers":
                        showResources("paper");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Called when a tab exits the selected state
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Called when a tab is selected again
            }
        });


        return root;
    }

    public void setupFragment() {
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();
        getListResources(new ResourceCallback() {
            @Override
            public void onResult(List<Resource> result, String errorMessage) {
                progressDialog.dismiss();

                if (result != null && !result.isEmpty()) {
                    resources = result;
                    empty.setVisibility(GONE);
                    content.setVisibility(VISIBLE);
                    renderResources(resources);
                } else if (errorMessage != null) {
                    // Show a message when server is unreachable or user offline
                    content.setVisibility(GONE);
                    empty.setVisibility(VISIBLE);
                    empty.removeAllViews();
                    btn.setVisibility(GONE);
                    tabLayout.setVisibility(GONE);

                    View noResource = inflater.inflate(R.layout.empty_state, empty, false);
                    TextView heading = noResource.findViewById(R.id.heading);
                    TextView desc = noResource.findViewById(R.id.description);
                    Button btn2 = noResource.findViewById(R.id.actionBtn);

                    btn2.setVisibility(VISIBLE);

                    btn2.setText("Go to Downloads");
                    btn2.setOnClickListener(v->{
                        startActivity(new Intent(getContext(), DownloadActivity.class));
                    });

                    heading.setText("Oops! 😅");
                    desc.setText(errorMessage); // Display network error message
                    empty.addView(noResource);
                } else {
                    // No resources but request succeeded
                    tabLayout.setVisibility(GONE);


                    content.setVisibility(GONE);
                    empty.setVisibility(VISIBLE);
                    empty.removeAllViews();

                    View noResource = inflater.inflate(R.layout.empty_state, empty, false);
                    TextView heading = noResource.findViewById(R.id.heading);
                    TextView desc = noResource.findViewById(R.id.description);

                    heading.setText("Be the First! 🚀");
                    desc.setText("No resources here yet. Add the first resource!");
                    empty.addView(noResource);
                }
            }
        });
    }

    public void showResources(String type){
        List<Resource> filtered = List.of();
        switch (type) {
            case "all":

                return;
            case "book":
                filtered = resources.stream()
                        .filter(r -> r.getType().equals(ResourceType.BOOK))
                        .collect(Collectors.toList());

                break;
            case "paper":
                content.setVisibility(GONE);
                empty.setVisibility(VISIBLE);
                empty.removeAllViews();

                View noResource = inflater.inflate(R.layout.empty_state, (ViewGroup) empty, false);
                TextView heading = noResource.findViewById(R.id.heading);
                TextView desc = noResource.findViewById(R.id.description);

                heading.setText("Coming Soon! 🚧");
                desc.setText("This module isn’t available yet, but we’re working on it! Stay tuned for updates and check out other categories in the meantime.");
                empty.addView(noResource);
                return;
            case "thesis":
                filtered = resources.stream()
                        .filter(r -> r.getType().equals(ResourceType.THESIS))
                        .collect(Collectors.toList());
                break;
            case "user":
                filtered = resources.stream()
                        .filter(r -> r.getType().equals(ResourceType.USER_UPLOAD))
                        .collect(Collectors.toList());
                break;
        }
        renderResources(filtered);

    }

    public void renderResources(List<Resource> resources){

        if(resources==null || resources.isEmpty()){
            content.setVisibility(GONE);
            empty.setVisibility(VISIBLE);
            empty.removeAllViews();
            View noResource = inflater.inflate(R.layout.empty_state, (ViewGroup) empty, false);
            TextView heading = noResource.findViewById(R.id.heading);
            TextView desc = noResource.findViewById(R.id.description);


            heading.setText("Oops! Nothing Here 😅");
            desc.setText("Looks like this category is feeling a bit lonely. Why not add a resource or check out other categories to keep things lively?");
            empty.addView(noResource);
            return;
        }
        empty.setVisibility(GONE);
        content.setVisibility(VISIBLE);

        // Use dynamic lists
        List<Resource> featuredResources = new ArrayList<>();
        List<Resource> communityUploads = new ArrayList<>();

        for (Resource res : resources) {
            if (res.isVerified()) {
                featuredResources.add(res);
            } else {
                communityUploads.add(res);
            }
        }

        llFeaturedContainer.removeAllViews();
        if(featuredResources.isEmpty()){
            fheading.setVisibility(GONE);
        }
        for (Resource res : featuredResources) {
            View card = inflater.inflate(R.layout.item_featured_resource, llFeaturedContainer, false);
            setResourceCard(card, res);
            llFeaturedContainer.addView(card);
        }

        llUserUploadsContainer.removeAllViews();
        for (Resource res : communityUploads) {
            View card = inflater.inflate(R.layout.item_user_upload, llUserUploadsContainer, false);

            setResourceCard(card, res);
            llUserUploadsContainer.addView(card);
        }
        if(communityUploads.isEmpty()){
            cheading.setVisibility(GONE);
        }
    }

    public interface ResourceCallback {
        void onResult(List<Resource> result, String errorMessage);
    }

    // Async method to get resources
    public void getListResources(ResourceCallback callback) {
        Call<ResponseDto<List<Resource>>> call = api.getLearningResources();
        call.enqueue(new Callback<ResponseDto<List<Resource>>>() {
            @Override
            public void onResponse(Call<ResponseDto<List<Resource>>> call, Response<ResponseDto<List<Resource>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().meta.statusCode == 200) {
                    callback.onResult(response.body().getData(), null);
                } else {
                    callback.onResult(null, "Server error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseDto<List<Resource>>> call, Throwable t) {
                // Network failure, offline, or server unreachable
                callback.onResult(null, "Please check your internet connection. You can still access resources you have downloaded");
            }
        });
    }



    private void setResourceCard(View card, Resource res) {
        ((TextView) card.findViewById(R.id.tvTitle)).setText(res.getName());
        ((TextView) card.findViewById(R.id.tvUploader)).setText("By: " + res.getUsername());
        ((TextView) card.findViewById(R.id.tvDate)).setText("Uploaded: "+res.getCreationDate());
        ImageView icon = card.findViewById(R.id.ivTypeIcon);
        icon.setImageResource(getResourceIcon(res.getType().name()));

        card.setOnClickListener(v -> {
            Intent intent = new Intent(this.getContext(), ResourceDescriptionActivity.class);

            // Convert Resource object to JSON string
            Gson gson = new Gson();
            String resourceJson = gson.toJson(res);

            intent.putExtra("resource", resourceJson);
            startActivity(intent);
        });


    }

    private int getResourceIcon(String type) {
        switch (type) {

            case "BOOK":
                return R.drawable.book;
            case "USER_UPLOAD":
                return R.drawable.portfolio;
            case "THESIS":
                return R.drawable.summary;
            default:
                return R.drawable.flashcard;
        }
    }
}
