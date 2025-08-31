package com.example.clexis.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.clexis.R;
import com.example.clexis.SessionManager;
import com.example.clexis.models.Alert;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.application.Review;
import com.example.clexis.models.dto.CommunityStats;
import com.example.clexis.models.entity.Discussion;
import com.example.clexis.models.entity.Discussion;
import com.example.clexis.models.enums.DiscussionType;
import com.example.clexis.models.response.ResponseDto;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ApiService api;
    private LinearLayout online;
    private RelativeLayout offline;

    private LinearLayout statsContainer;

    private List<Discussion> discussionList;
    private LinearLayout discussionContainer;

    private View  noDiscussion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Set your primary color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary));

        setContentView(R.layout.activity_forum_1);
        api = ApiClient.getRetrofitInstance(this).create(ApiService.class);





        online = findViewById(R.id.online);
        offline = findViewById(R.id.offline);
        statsContainer = findViewById(R.id.statsContainer);
        discussionContainer = findViewById(R.id.discussions);
        noDiscussion = findViewById(R.id.noDiscussions);
        TabLayout tabLayout =findViewById(R.id.tab_layout);

        ScrollView scrollView = findViewById(R.id.scrollView);
        FloatingActionButton fabScrollTop = findViewById(R.id.fab_scroll_top);
        ExtendedFloatingActionButton fabNewPost= findViewById(R.id.fab_new_post);

// Set initial visibility
        fabScrollTop.setVisibility(View.GONE);

// Listen for scroll changes
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = scrollView.getScrollY(); // Current vertical scroll
            if (scrollY > 500) { // Adjust this value to your threshold
                fabScrollTop.setVisibility(View.VISIBLE);
            } else {
                fabScrollTop.setVisibility(View.GONE);
            }
        });

// Scroll to top when FAB is clicked
        fabScrollTop.setOnClickListener(v -> scrollView.smoothScrollTo(0, 0));



        setupActivity();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Called when a tab enters the selected state
                int position = tab.getPosition();
                showDiscussions(position);// index of the selected tab


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

        fabNewPost.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View view = getLayoutInflater().inflate(R.layout.add_discussion_modal, null);
            bottomSheetDialog.setContentView(view);

            TextInputEditText titleInput = view.findViewById(R.id.discussionTitle);
            TextInputEditText bodyInput = view.findViewById(R.id.discussionBody);
            MaterialAutoCompleteTextView spinnerType = view.findViewById(R.id.spinnerType);
            Button submitBtn = view.findViewById(R.id.submitDiscussion);

            String[] types = new String[]{"General", "Help", "Exam Preparation", "Tips & Motivation"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.dropdown_item, types);
            spinnerType.setAdapter(adapter);

            submitBtn.setOnClickListener(v1 -> {
                String title = titleInput.getText().toString().trim();
                String body = bodyInput.getText().toString().trim();
                String selectedType = spinnerType.getText().toString();

                if(!title.isEmpty() && !body.isEmpty() && !selectedType.isEmpty()){

                    ArrayAdapter<String> adapter2 = (ArrayAdapter<String>) spinnerType.getAdapter();
                    int position = adapter2.getPosition(selectedType);

                   
                    SharedPreferences prefs =this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    String name = prefs.getString("studentName","Enow Brenda");
                    SessionManager sessionManager = new SessionManager(this);
                    Date now = new Date(); // current date & time
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                    String currentDateTime = sdf.format(now);

                    Discussion newDiscussion = Discussion.builder()
                            .authorName(name)
                            .date(currentDateTime)
                            .type(getDiscussionType(position))
                            .discussionBody(body)
                            .discussionTitle(title)
                            .responses(List.of())
                            .userId(sessionManager.getId())
                            .build();
                    discussionList.add(newDiscussion);//for test
                    renderDiscussions(discussionList);//for test
                    addDiscussion(newDiscussion);
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            });

            bottomSheetDialog.show();
        });


    }

    private void addDiscussion(Discussion newDiscussion) {
        Call<ResponseDto<Discussion>> call = api.addDiscussion(newDiscussion);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();

        call.enqueue(new Callback<ResponseDto<Discussion>>() {
            @Override
            public void onResponse(Call<ResponseDto<Discussion>> call, Response<ResponseDto<Discussion>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<Discussion> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        Alert.showAlert(
                                ForumActivity.this,
                                "\uD83C\uDF89 Hooray! Discussion Created",
                                "Your discussion has been successfully added to the community. Thank you for sharing your thoughts! 🎉",
                                true,
                                null
                        );


                    }else{
                        Alert.showAlert(
                                ForumActivity.this,
                                "\uD83D\uDE22 Oops! Something went wrong",
                                "We couldn’t create your discussion this time. Please check your input or try again later.\n" + dto.getErrors(),
                                false,
                                null
                        );

                    }

                } else{
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

                            // Show a meaningful message to the user
                            Alert.showAlert(ForumActivity.this,"Discussion Addition Failed",errorResponse.getErrors(),false,null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseDto<Discussion>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ForumActivity.this, "Error Occured while adding Discussion .Check your network connectivity", Toast.LENGTH_SHORT).show();
                Log.e("API", "Error: " + t.getMessage());
            }


        });
    }

    private DiscussionType getDiscussionType(int position) {
        switch (position){
            case 0:{
                return DiscussionType.GENERAL;
            }
            case 1:{
                return DiscussionType.HELP;
            }
            case 2:{
                return DiscussionType.EXAM_PREPARATION;
            }
            case 3:{
                return DiscussionType.TIPS;
            }
        }
        return null;
    }

    public void showDiscussions(int position){
        switch(position){
            case 0:
                renderDiscussions(discussionList);
                break;
            case 1:
                List<Discussion> filtered = discussionList.stream()
                        .filter(discussion -> discussion.getType() == DiscussionType.GENERAL)
                        .collect(Collectors.toList());
                renderDiscussions(filtered);
                break;
            case 2:
                List<Discussion> filtered2 = discussionList.stream()
                        .filter(discussion -> discussion.getType() == DiscussionType.HELP)
                        .collect(Collectors.toList());
                renderDiscussions(filtered2);
                break;
            case 3:
                List<Discussion> filtered3 = discussionList.stream()
                        .filter(discussion -> discussion.getType() == DiscussionType.EXAM_PREPARATION)
                        .collect(Collectors.toList());
                renderDiscussions(filtered3);
                break;
            case 4:
                List<Discussion> filtered4 = discussionList.stream()
                        .filter(discussion -> discussion.getType() == DiscussionType.TIPS)
                        .collect(Collectors.toList());
                renderDiscussions(filtered4);
                break;

        }
    }
    public void setupActivity(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();
       CommunityStats stats = getCommunityStats();
       if(stats==null){
           online.setVisibility(View.GONE);
           offline.setVisibility(View.VISIBLE);
           statsContainer.removeAllViews();
       }else{
           List<Pair<String, Object>> metrics = Arrays.asList(
                   new Pair<>("Active Posts", stats.getActivePost()),
                   new Pair<>("Members", stats.getCommunityMembers()),
                   new Pair<>("Online", stats.getOnline())
           );
           for (Pair<String, Object> metric : metrics) {
               View statsC = getLayoutInflater().inflate(R.layout.item_achievement_card, statsContainer, false);

               TextView titleView = statsC.findViewById(R.id.metrics);
               TextView valueView = statsC.findViewById(R.id.value);
               ImageView image = statsC.findViewById(R.id.image);

               // Set values
               titleView.setText(metric.first);
               valueView.setText(String.valueOf(metric.second));

               switch(metric.first){
                   case "Active Posts":{
                       image.setImageResource(R.drawable.book);
                       break;
                   }
                   case "Members":{
                       image.setImageResource(R.drawable.user);
                       break;
                   }
                   case "Online":{
                       image.setImageResource(R.drawable.profile);
                       break;
                   }

               }

               // Add to container
               statsContainer.addView(statsC);
           }


       }
       discussionList = getListDiscussions();
        progressDialog.dismiss();
       if(discussionList.isEmpty()){
           discussionContainer.removeAllViews();
           TextView heading  = noDiscussion.findViewById(R.id.heading);
           TextView description  = noDiscussion.findViewById(R.id.description);
           ImageView icon = noDiscussion.findViewById(R.id.icon);

           heading.setText("No Discussions Yet 🤔");
           description.setText("Be the first to start a conversation and spark ideas!");
           icon.setImageResource(R.drawable.forum);

           noDiscussion.setVisibility(View.VISIBLE);
       }else{

           renderDiscussions(discussionList);
       }
    }

    private void renderDiscussions(List<Discussion> discussions) {


       discussionContainer.removeAllViews();
        noDiscussion.setVisibility(View.GONE);
       if(discussions.isEmpty()){

           TextView heading  = noDiscussion.findViewById(R.id.heading);
           TextView description  = noDiscussion.findViewById(R.id.description);
           ImageView icon = noDiscussion.findViewById(R.id.icon);

           heading.setText("No Discussions Yet 🤔");
           description.setText("There are no discussions in this category yet. Start a conversation and get the community talking!");
           icon.setImageResource(R.drawable.socials);

           noDiscussion.setVisibility(View.VISIBLE);
       }
        for (Discussion discussion : discussions) {
            // Inflate the discussion card
            View card = getLayoutInflater().inflate(R.layout.discussion_card, discussionContainer, false);

            TextView title = card.findViewById(R.id.name);
            title.setText(discussion.getDiscussionTitle());
            // Set author's name
            TextView authorName = card.findViewById(R.id.authorName);
            authorName.setText(discussion.getAuthorName());

            // Set discussion date/time
            TextView dateTime = card.findViewById(R.id.dateTime);
            dateTime.setText(discussion.getDate()); // make sure this is a formatted string

            // Set discussion description
            TextView description = card.findViewById(R.id.description);
            description.setText(discussion.getDiscussionBody());

            // Set profile initials (optional)
            TextView tvProfileInitials = card.findViewById(R.id.tv_profile_initials);
            String initials = getInitials(discussion.getAuthorName());
            tvProfileInitials.setText(initials);


            // Set number of responses
            TextView responses = card.findViewById(R.id.responses);
            responses.setText(discussion.getResponses().size() + " Responses");

            // Add click listeners if needed
            card.setOnClickListener(v -> {
                // handle click on discussion card
                Intent intent = new Intent(this, ForumDiscussionActivity.class);

                // Convert Discussion object to JSON string
                Gson gson = new Gson();
                String discussionJson = gson.toJson(discussion);

                intent.putExtra("discussion", discussionJson);
                startActivity(intent);

            });

            // Add the card to the container
            discussionContainer.addView(card);
        }


    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "";
        String[] parts = fullName.split(" ");
        String initials = "";
        for (String part : parts) {
            if (!part.isEmpty()) {
                initials += part.charAt(0);
            }
        }
        return initials.toUpperCase();
    }
    public List<Discussion> getListDiscussions(){
        final List<Discussion>[] discussions = new List[]{getCachedDiscussions()}; //50 random discussions
        Call<ResponseDto<List<Discussion>>> call = api.getCommunityDiscussions(); //50 random discussions
        call.enqueue(new Callback<ResponseDto<List<Discussion>>>() {
            @Override
            public void onResponse(Call<ResponseDto<List<Discussion>>> call, Response<ResponseDto<List<Discussion>>> response) {
                progressDialog.dismiss();
                Log.d("API response", "Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<List<Discussion>> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        //store token logic
                        discussions[0] = dto.getData();

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
            public void onFailure(Call<ResponseDto<List<Discussion>>> call, Throwable t) {
                Log.d("API response", "Failed to reach the server " + t.getMessage());
            }

        });
        return discussions[0];
    }

    public CommunityStats getCommunityStats(){
        final CommunityStats[] stats = {null}; //50 random discussions
        Call<ResponseDto<CommunityStats>> call = api.getCommunityStats(); //50 random discussions
        call.enqueue(new Callback<ResponseDto<CommunityStats>>() {
            @Override
            public void onResponse(Call<ResponseDto<CommunityStats>> call, Response<ResponseDto<CommunityStats>> response) {
                progressDialog.dismiss();
                Log.d("API response", "Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<CommunityStats> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        //store token logic
                        stats[0] = dto.getData();

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
            public void onFailure(Call<ResponseDto<CommunityStats>> call, Throwable t) {
                Log.d("API response", "Failed to reach the server " + t.getMessage());
            }

        });
        return stats[0];
    }

    private List<Discussion> getCachedDiscussions() {
        List<Discussion> discussions = new ArrayList<>();

        // Example responses
        List<Review> reviews1 = new ArrayList<>();
        reviews1.add(new Review("u101", "Alice", "Great tips, thanks!", "2025-08-25 12:30", 5, false));
        reviews1.add(new Review("u102", "Bob", "I also follow interval sessions.", "2025-08-25 14:00", 4, false));

        List<Review> reviews2 = new ArrayList<>();
        reviews2.add(new Review("u103", "Charlie", "I recommend 'Effective Java'", "2025-08-24 16:20", 5, false));

        // Add discussions
        discussions.add(new Discussion(
                "d001",
                "Exam Preparation Tips",
                "I’ve been studying non-stop for finals. Here are a few tips: 1. Break study sessions into intervals. 2. Focus on weak topics. 3. Practice past exams.",
                "u100",
                "John Smith",
                reviews1,
                "2025-08-24 10:00",
                DiscussionType.GENERAL
        ));

        discussions.add(new Discussion(
                "d002",
                "Best Books for Learning Java",
                "Looking for recommendations for Java books that are beginner-friendly and practical.",
                "u101",
                "Jane Doe",
                reviews2,
                "2025-08-23 09:30",
                DiscussionType.HELP
        ));

        return discussions;
    }

}
