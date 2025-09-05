package com.example.clexis.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.clexis.R;
import com.example.clexis.models.Alert;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.application.Review;
import com.example.clexis.models.entity.Discussion;
import com.example.clexis.models.entity.Resource;
import com.example.clexis.models.response.ResponseDto;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumDiscussionActivity extends AppCompatActivity {

    private Discussion discussion;
    private TextView responseCount;

    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum_2);

        api = ApiClient.getRetrofitInstance(this).create(ApiService.class);

        Intent intent = getIntent();
        String json = intent.getStringExtra("discussion"); // If you used Gson
        discussion = new Gson().fromJson(json, Discussion.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }

        responseCount = findViewById(R.id.responseCount);

        if (discussion != null) {
            bindDiscussion(discussion);
        }
    }

    private void bindDiscussion(Discussion discussion) {
        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Discussion card
        View discussionCard = findViewById(R.id.discussion_card); // Make sure to assign id in your include
        TextView tvTitle = discussionCard.findViewById(R.id.name);
        TextView tvAuthor = discussionCard.findViewById(R.id.authorName);
        TextView tvDate = discussionCard.findViewById(R.id.dateTime);
        TextView tvDescription = discussionCard.findViewById(R.id.description);
        TextView tvResponses = discussionCard.findViewById(R.id.responses);
        TextView tvInitials = discussionCard.findViewById(R.id.tv_profile_initials);


        // Set values
        tvTitle.setText(discussion.getDiscussionTitle());
        tvAuthor.setText(discussion.getAuthorName());
        tvDate.setText(discussion.getDate());
        tvDescription.setText(discussion.getDiscussionBody());
        tvResponses.setText(discussion.getResponses() != null
                ? discussion.getResponses().size() + " Responses"
                : "0 Responses");
        responseCount.setText(discussion.getResponses() != null
                ?  "Responses ("+discussion.getResponses().size() +")"
                : "Responses (0)");





        // Set initials
        String initials = "";
        if (discussion.getAuthorName() != null && !discussion.getAuthorName().isEmpty()) {
            String[] parts = discussion.getAuthorName().split(" ");
            for (String p : parts) {
                if (!p.isEmpty()) initials += p.charAt(0);
            }
        }
        tvInitials.setText(initials.toUpperCase());

        // Responses
        LinearLayout responsesContainer = findViewById(R.id.responses_container); // add id to LinearLayout holding responses
        responsesContainer.removeAllViews();
        if (discussion.getResponses() == null || discussion.getResponses().isEmpty()) {
            View empty = getLayoutInflater().inflate(R.layout.empty_state, responsesContainer, false);
            TextView heading  = empty.findViewById(R.id.heading);
            TextView description  = empty.findViewById(R.id.description);
            ImageView icon = empty.findViewById(R.id.icon);

            heading.setText("No Responses Yet 🤔");
            description.setText("Be the first to respond and share your thoughts!");
            icon.setImageResource(R.drawable.flashcard);

            responsesContainer.setVisibility(View.VISIBLE);
            responsesContainer.addView(empty);

        }
        else {
            for (Review review : discussion.getResponses()) {
                View responseCard = getLayoutInflater().inflate(R.layout.response_card, responsesContainer, false);
                TextView tvResponseAuthor = responseCard.findViewById(R.id.authorName);
                TextView tvResponseDate = responseCard.findViewById(R.id.dateTime);
                TextView tvResponseBody = responseCard.findViewById(R.id.description);
                TextView tvResponseInitials = responseCard.findViewById(R.id.tv_profile_initials);

                String initialsR = "";
                if (discussion.getAuthorName() != null && !discussion.getAuthorName().isEmpty()) {
                    String[] parts = review.getAuthorName().split(" ");
                    for (String p : parts) {
                        if (!p.isEmpty()) initialsR += p.charAt(0);
                    }
                }
                tvResponseInitials.setText(initialsR.toUpperCase());

                tvResponseAuthor.setText(review.getAuthorName());
                tvResponseDate.setText(review.getDate());
                tvResponseBody.setText(review.getDescription());

                responsesContainer.addView(responseCard);
            }
        }



        // Optional: Send button
        ImageButton sendButton = findViewById(R.id.send_button);
        EditText messageInput = findViewById(R.id.message_input);
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                // TODO: send message to backend or add to responses locally
                //add to responses and hit the update endpoint
                Date now = new Date(); // current date & time
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                String currentDateTime = sdf.format(now);
                Review review = Review.builder()
                        .date(currentDateTime)
                        .authorName("Test")
                        .submissionReview(false)
                        .description(message)
                        .build();
                discussion.getResponses().add(review);//for test purpose
                bindDiscussion(discussion);

                updateDiscussion(discussion);
                messageInput.setText("");
            }
        });
    }

    private void updateDiscussion(Discussion newDiscussion) {
        Call<ResponseDto<Discussion>> call = api.updateDiscussion(newDiscussion);
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

                        Toast.makeText(ForumDiscussionActivity.this, "Response Recorded. Keep Growing with the community", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(ForumDiscussionActivity.this, "Could not add Response,Something happened ,Try again", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(ForumDiscussionActivity.this, "Something went Wrong. Try adding response again", Toast.LENGTH_SHORT).show();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseDto<Discussion>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ForumDiscussionActivity.this, "Error Occured while adding Discussion .Check your network connectivity", Toast.LENGTH_SHORT).show();
                Log.e("API", "Error: " + t.getMessage());
            }


        });

    }

}