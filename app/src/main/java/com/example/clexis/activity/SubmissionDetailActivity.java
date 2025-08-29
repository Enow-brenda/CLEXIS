package com.example.clexis.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.clexis.R;
import com.example.clexis.SessionManager;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.Utils;
import com.example.clexis.models.application.BuddyScore;
import com.example.clexis.models.application.Milestone;
import com.example.clexis.models.application.Review;
import com.example.clexis.models.application.Submission;
import com.example.clexis.models.entity.BuddyProgram;
import com.example.clexis.models.entity.LearningPath;
import com.example.clexis.models.entity.Resource;
import com.example.clexis.models.response.ResponseDto;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmissionDetailActivity extends AppCompatActivity {

    private ApiService api;

    private String id;

    private Utils utils = new Utils();

    private BuddyProgram program;
    private String mileStoneIndex;
    private boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submission_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // --- Get Resource Object ---
        Intent intent = getIntent();
        String json = intent.getStringExtra("submission");
        String mileStoneIndex = intent.getStringExtra("milestoneIndex");

        id = intent.getStringExtra("submission");// If you used Gson
        Submission submission = new Gson().fromJson(json, Submission.class);

        if (submission != null && mileStoneIndex!=null) {
            toolbar.setTitle(submission.getAuthorName().split(" ")[0] +"'s Submission");
            bindResource(submission);

        }

        api = ApiClient.getRetrofitInstance(this).create(ApiService.class);


    }

    private void bindResource(Submission submission) {
        LinearLayout submissionB = findViewById(R.id.submission);
        View singleSubmission = getLayoutInflater().inflate(R.layout.milestone_card,submissionB,false);
        TextView authorName  = singleSubmission.findViewById(R.id.authorName);
        TextView authorInitials  = singleSubmission.findViewById(R.id.authorInitials);
        TextView dateB  = singleSubmission.findViewById(R.id.date);
        TextView desc= singleSubmission.findViewById(R.id.desc);
        LinearLayout fileBox= singleSubmission.findViewById(R.id.fileSubmission);
        TextView filename= singleSubmission.findViewById(R.id.fileName);
        ImageView saveBtn= singleSubmission.findViewById(R.id.fileDownloadBtn);
        TextView responses= singleSubmission.findViewById(R.id.reviews);
        TextView totalScore= singleSubmission.findViewById(R.id.score);
        MaterialButton btn= singleSubmission.findViewById(R.id.viewBtn);


        authorName.setText(submission.getAuthorName());
        authorInitials.setText(utils.getInitials(submission.getAuthorName()));
        dateB.setText(submission.getSubmittedDate());
        desc.setText(submission.getDescription());
        if(submission.isFileSubmission()){
            fileBox.setVisibility(VISIBLE);
            filename.setText(submission.getFilename());
            //add download logic
        }else{
            fileBox.setVisibility(GONE);
        }

        totalScore.setText(submission.getScore() + " /20");
        responses.setText(submission.getReviews().size() + " Reviews");
        SessionManager sessionManager = new SessionManager(this);
        boolean submitted = false;
        for(Review review: submission.getReviews()){
            if(review.getUserId().equals(sessionManager.getId())){
                submitted = true;
            }
        }
        if(submission.getUserId().equals(sessionManager.getId()) || submitted){
            btn.setVisibility(GONE);
        }else{
            btn.setText("Grade Now");

            btn.setOnClickListener(v -> {
                // Inflate a custom layout for the modal
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_review, null);

                // Find inputs in the dialog layout
                TextView tvWarning = dialogView.findViewById(R.id.tvWarning);
                TextInputEditText etGrade = dialogView.findViewById(R.id.etGrade);
                TextInputEditText etReview = dialogView.findViewById(R.id.etReview);

                tvWarning.setText("⚠️ You can review this submission only once!");

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                        .setTitle("Add Your Review")
                        .setView(dialogView)
                        .setBackground(new ColorDrawable(Color.WHITE))
                        .setCancelable(false)
                        .setPositiveButton("Submit", null) // Set null first
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getWindow().setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                    // Override positive button click to prevent auto-dismiss
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v2 -> {
                    String gradeText = etGrade.getText() != null ? etGrade.getText().toString().trim() : "";
                    String reviewText = etReview.getText() != null ? etReview.getText().toString().trim() : "";

                    if (gradeText.isEmpty()) {
                        etGrade.setError("Grade is required");
                        return;
                    }

                    if (reviewText.isEmpty()) {
                        etReview.setError("Provide a little description");
                        return;
                    }

                    int grade;
                    try {
                        grade = Integer.parseInt(gradeText);
                    } catch (NumberFormatException e) {
                        etGrade.setError("Enter a valid number");
                        return;
                    }

                    if (grade < 0 || grade > 10) {
                        etGrade.setError("Grade must be between 0 and 10");
                        return;
                    }

                    // Build your new review object
                    Review newReview = Review.builder().build();
                    program = getBuddyProgram(id);

                    if (isOnline) {
                        Milestone milestone = program.getMileStoneList().get(Integer.parseInt(mileStoneIndex));
                        for (Submission submission1 : milestone.getSubmissionList()) {
                            if (submission1.getUserId().equals(submission.getUserId())) {
                                var reviews = submission1.getReviews();
                                reviews.add(newReview);
                                submission1.setReviews(reviews);
                                var updated = updateProgram();
                                if (updated) {
                                    Toast.makeText(SubmissionDetailActivity.this,
                                            "✓ Your review has been submitted successfully!",
                                            Toast.LENGTH_LONG).show();
                                    dialog.dismiss(); // Only dismiss if submission is successful
                                } else {
                                    Toast.makeText(SubmissionDetailActivity.this,
                                            "⚠ Error occurred while processing request. Please try again.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(SubmissionDetailActivity.this,
                                "⚠ No internet connection. Please check your network and try again.",
                                Toast.LENGTH_LONG).show();
                    }
                });

            });

        }

        submissionB.addView(singleSubmission);



        // Responses
        LinearLayout responsesContainer = findViewById(R.id.reviewsB); // add id to LinearLayout holding responses
        responsesContainer.removeAllViews();
        if (submission.getReviews() == null || submission.getReviews().isEmpty()) {
            View empty = getLayoutInflater().inflate(R.layout.empty_state, responsesContainer, false);
            TextView heading  = empty.findViewById(R.id.heading);
            TextView description  = empty.findViewById(R.id.description);
            ImageView icon = empty.findViewById(R.id.icon);

            heading.setText("No Reviews Yet 🤔");
            description.setText("Be the first to review and share your thoughts!");
            icon.setImageResource(R.drawable.flashcard);

            responsesContainer.setVisibility(View.VISIBLE);
            responsesContainer.addView(empty);

        }
        else {

            TextView count = findViewById(R.id.reviewCount);
            count.setText("Buddy Reviews ("+submission.getReviews().size()+")");
            for (Review review : submission.getReviews()) {
                View responseCard = getLayoutInflater().inflate(R.layout.submission_response_card, responsesContainer, false);
                TextView tvResponseAuthor = responseCard.findViewById(R.id.authorName);
                TextView tvResponseDate = responseCard.findViewById(R.id.dateTime);
                TextView tvResponseBody = responseCard.findViewById(R.id.description);
                TextView score = responseCard.findViewById(R.id.score);
                score.setText(String.valueOf(review.getScore()));
                TextView tvResponseInitials = responseCard.findViewById(R.id.tv_profile_initials);


                tvResponseInitials.setText(utils.getInitials(review.getAuthorName()));

                tvResponseAuthor.setText(review.getAuthorName());
                tvResponseDate.setText(review.getDate());
                tvResponseBody.setText(review.getDescription());

                responsesContainer.addView(responseCard);
            }
        }
    }

    private boolean updateProgram() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing your Request...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();

        final boolean[] updated = {false};

        Call<ResponseDto<BuddyProgram>> call = api.updateBuddyProgram(program);
        call.enqueue(new Callback<ResponseDto<BuddyProgram>>() {
            @Override
            public void onResponse(Call<ResponseDto<BuddyProgram>> call, Response<ResponseDto<BuddyProgram>> response) {
                progressDialog.dismiss();
                Log.d("API response", "Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<BuddyProgram> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        updated[0] = true;
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
            public void onFailure(Call<ResponseDto<BuddyProgram>> call, Throwable t) {
                progressDialog.dismiss();

                Log.d("API response", "Failed to reach the server " + t.getMessage());
            }

        });
        return updated[0];
    }

    public BuddyProgram getBuddyProgram(String id){


        final BuddyProgram[] path = {getLocalBuddyProgram()};
        Call<ResponseDto<BuddyProgram>> call = api.getBuddyProgram(id);
        call.enqueue(new Callback<ResponseDto<BuddyProgram>>() {
            @Override
            public void onResponse(Call<ResponseDto<BuddyProgram>> call, Response<ResponseDto<BuddyProgram>> response) {

                isOnline = true;
                Log.d("API response", "Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<BuddyProgram> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        //store token logic
                        path[0] = dto.getData();

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
            public void onFailure(Call<ResponseDto<BuddyProgram>> call, Throwable t) {

                isOnline = false;
                Log.d("API response", "Failed to reach the server " + t.getMessage());
            }

        });
        return path[0];

    }
    public BuddyProgram getLocalBuddyProgram(){
        BuddyProgram buddyProgram = new BuddyProgram();
        buddyProgram.setId("bp-001");
        buddyProgram.setTitle("Java Spring Boot Learning Program");
        buddyProgram.setDescription("A 4-week buddy program to help participants master the fundamentals of Spring Boot, including REST APIs, database integration, and testing.");
        buddyProgram.setDeadline("30-09-2025");
        buddyProgram.setStartDate("01-08-2025");
        buddyProgram.setOpened(true);
        buddyProgram.setAuthorId("admin-123");
        buddyProgram.setAuthorName("Kanjo Elkamira Ndi");

// Milestones
        Milestone milestone1 = new Milestone();
        milestone1.setName("Introduction & Setup");
        milestone1.setDescription("Install Java, IntelliJ, and create your first Spring Boot project.");
        milestone1.setQuiz(false);
        milestone1.setTaskOrQuizCode("task-setup-001");

        Submission submission1 = new Submission();
        submission1.setUserId("user-001");
        submission1.setAuthorName("Mac Brenda Eweh");
        submission1.setSubmittedDate("02-09-2025");
        submission1.setDescription("Submitted project setup with Hello World endpoint.");
        submission1.setSubmissionUrl("http://github.com/user001/springboot-setup");
        submission1.setFileSubmission(true);
        submission1.setFilename("main.py");
        submission1.setScore(10);

        Review review1 = new Review();
        review1.setUserId("admin-123");
        review1.setAuthorName("Kanjo Elkamira Ndi");
        review1.setDescription("Good setup, everything runs fine.");
        review1.setDate("03-09-2025");
        review1.setScore(5);
        review1.setSubmissionReview(true);

        submission1.setReviews(List.of(review1));
        milestone1.setSubmissionList(List.of(submission1));

        Milestone milestone2 = new Milestone();
        milestone2.setName("Building REST APIs");
        milestone2.setDescription("Create a REST API for managing books with CRUD operations.");
        milestone2.setQuiz(true);
        milestone2.setTaskOrQuizCode("quiz-api-002");
        milestone2.setSubmissionList(List.of());

// Add milestones
        buddyProgram.setMileStoneList(List.of(milestone1, milestone2));

// Buddy Scores
        BuddyScore buddy1 = new BuddyScore();
        buddy1.setUserId("user-001");
        buddy1.setScore(15);

        BuddyScore buddy2 = new BuddyScore();
        buddy2.setUserId("user-002");
        buddy2.setScore(12);

        buddyProgram.setBuddies(List.of(buddy1, buddy2));

        return buddyProgram;

    }
}