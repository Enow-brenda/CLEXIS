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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.clexis.R;
import com.example.clexis.activity.AddResourceActivity;
import com.example.clexis.activity.ResourceDescriptionActivity;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.entity.LearningPath;
import com.example.clexis.models.entity.Resource;
import com.example.clexis.models.enums.ResourceType;
import com.example.clexis.models.response.ResponseDto;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater2, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater2.inflate(R.layout.fragment_library, container, false);
        inflater = inflater2;
        content = root.findViewById(R.id.content);
        empty = root.findViewById(R.id.empty);
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();

        llFeaturedContainer = root.findViewById(R.id.llFeaturedContainer);
        llUserUploadsContainer = root.findViewById(R.id.llUserUploadsContainer);

        api = ApiClient.getRetrofitInstance(this.getContext()).create(ApiService.class);
        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        root.findViewById(R.id.fabUpload).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddResourceActivity.class));
        });


        setupFragment();
        progressDialog.dismiss();

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

    public void  setupFragment(){
        resources = getListResources();
        if(resources.isEmpty()){
            empty.setVisibility(VISIBLE);
            empty.removeAllViews();
            View noResource = inflater.inflate(R.layout.empty_state,  empty, false);
            TextView heading = noResource.findViewById(R.id.heading);
            TextView desc = noResource.findViewById(R.id.description);


            heading.setText("Be the First! 🚀");
            desc.setText("No resources here yet. This is your chance to lead the way—add the first resource and inspire others!");
            empty.addView(noResource);
        }else{
            empty.setVisibility(GONE);
            content.setVisibility(VISIBLE);
            renderResources(resources);
        }
    }

    public void showResources(String type){
        List<Resource> filtered = List.of();
        switch (type) {
            case "all":
                setupFragment();
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
    }

    public List<Resource> getListResources(){
        final List<Resource>[] resources = new List[]{getLocalResources()};
        Call<ResponseDto<List<Resource>>> call = api.getLearningResources();
        call.enqueue(new Callback<ResponseDto<List<Resource>>>() {
            @Override
            public void onResponse(Call<ResponseDto<List<Resource>>> call, Response<ResponseDto<List<Resource>>> response) {
                progressDialog.dismiss();
                Log.d("API response", "Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<List<Resource>> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        //store token logic
                        resources[0] = dto.getData();

                    }
                }
                else{
                    String errorString = null;
                    try {
                        if (response.errorBody() != null) {
                            errorString = response.errorBody().string();
                            Log.d("API response", "Error String: " + errorString);

                            // Parse the JSON string into ResponseDto
                            Gson gson = new Gson();
                            ResponseDto<Object> errorResponse = gson.fromJson(
                                    errorString,
                                    new TypeToken<ResponseDto<Object>>(){}.getType()
                            );

                            // Now you can access fields
                            Log.d("API response", "Error message: " + errorResponse.getMeta().getMessage());
                            Log.d("API response", "Error code: " + errorResponse.getMeta().getStatusCode());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseDto<List<Resource>>> call, Throwable t) {
                Log.d("API response", "Failed to reach the server " + t.getMessage());
            }

        });
        return resources[0];
    }

    private List<Resource> getLocalResources() {
        Resource r1 = Resource.builder()
                .id("res_001")
                .name("Java Basics PDF")
                .description("Comprehensive guide to core Java concepts.")
                .type(ResourceType.BOOK)
                .verified(true)
                .rejected(false)
                .free(true)
                .price(0)
                .merchantNumber("1234567890")
                .fileUrl("https://example.com/java_basics.pdf")
                .imageUrl("https://example.com/images/java_basics.jpg")
                .userId("user_001")
                .username("Alice")
                .creationDate("2025-08-22")
                .build();

        Resource r2 = Resource.builder()
                .id("res_002")
                .name("OOP Video Tutorial")
                .description("Video lessons on Object-Oriented Programming in Java.")
                .type(ResourceType.USER_UPLOAD)
                .verified(true)
                .rejected(false)
                .free(false)
                .price(500)
                .merchantNumber("1234567891")
                .fileUrl("https://example.com/oop_tutorial.mp4")
                .imageUrl("https://example.com/images/oop_video.jpg")
                .userId("user_002")
                .username("Bob")
                .creationDate("2025-08-20")
                .build();

        Resource r3 = Resource.builder()
                .id("res_003")
                .name("Spring Boot Guide")
                .description("Step-by-step tutorial for Spring Boot applications.")
                .type(ResourceType.BOOK)
                .verified(true)
                .rejected(false)
                .free(true)
                .price(0)
                .merchantNumber("1234567892")
                .fileUrl("https://example.com/spring_boot.pdf")
                .imageUrl("https://example.com/images/spring_boot.jpg")
                .userId("user_003")
                .username("Charlie")
                .creationDate("2025-08-21")
                .build();

        Resource r4 = Resource.builder()
                .id("res_004")
                .name("Algorithm Cheatsheet")
                .description("Quick reference for common algorithms and data structures.")
                .type(ResourceType.USER_UPLOAD)
                .verified(true)
                .rejected(false)
                .free(true)
                .price(0)
                .merchantNumber("1234567893")
                .fileUrl("https://example.com/algorithms.pdf")
                .imageUrl("https://example.com/images/algorithms.jpg")
                .userId("user_004")
                .username("Diana")
                .creationDate("2025-08-19")
                .build();

        Resource r5 = Resource.builder()
                .id("res_005")
                .name("Advanced Java Notes")
                .description("Notes covering advanced Java topics, awaiting verification.")
                .type(ResourceType.BOOK)
                .verified(false)
                .rejected(false)
                .free(false)
                .price(300)
                .merchantNumber("1234567894")
                .fileUrl("https://example.com/advanced_java.pdf")
                .imageUrl("https://example.com/images/advanced_java.jpg")
                .userId("user_005")
                .username("Eve")
                .creationDate("2025-08-22")
                .build();

        Resource r6 = Resource.builder()
                .id("res_006")
                .name("Data Science Cheatsheet")
                .description("Key concepts and formulas for quick reference in data science.")
                .type(ResourceType.USER_UPLOAD)
                .verified(true)
                .rejected(false)
                .free(true)
                .price(0)
                .merchantNumber("1234567895")
                .fileUrl("https://example.com/ds_cheatsheet.pdf")
                .imageUrl("https://example.com/images/ds_cheatsheet.jpg")
                .userId("user_006")
                .username("Frank")
                .creationDate("2025-08-18")
                .build();

        Resource r7 = Resource.builder()
                .id("res_007")
                .name("Machine Learning Basics")
                .description("Introductory slides on machine learning concepts, not yet verified.")
                .type(ResourceType.BOOK)
                .verified(false)
                .rejected(false)
                .free(false)
                .price(200)
                .merchantNumber("1234567896")
                .fileUrl("https://example.com/ml_basics.pptx")
                .imageUrl("https://example.com/images/ml_basics.jpg")
                .userId("user_007")
                .username("Grace")
                .creationDate("2025-08-23")
                .build();

        return List.of(r1,r2,r3,r4,r5,r6,r7);
//        return List.of();
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
