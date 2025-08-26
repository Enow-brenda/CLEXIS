package com.example.clexis.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.clexis.R;
import com.example.clexis.SessionManager;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.application.BuddyScore;
import com.example.clexis.models.application.Milestone;
import com.example.clexis.models.application.Review;
import com.example.clexis.models.application.Submission;
import com.example.clexis.models.entity.BuddyProgram;
import com.example.clexis.models.response.ResponseDto;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuddyProgramListActivity extends AppCompatActivity {

    private ApiService api;
    private List<BuddyProgram> buddyProgramList;
    private LinearLayout container;

    private String active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buddy_list);

        api = ApiClient.getRetrofitInstance(this).create(ApiService.class);
        container = findViewById(R.id.programs) ;
        active = "ongoing";

        setUpActivity(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Called when a tab enters the selected state
                String tabText = tab.getText().toString();
                active = tabText.toLowerCase();
                setUpActivity(false);
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

        View add = findViewById(R.id.addProgram);

        add.setOnClickListener(v -> {
            Intent intent = new Intent(this , AddBuddyProgramActivity.class);
            startActivity(intent);
        });
    }

    public void setUpActivity(boolean reload){
        if(buddyProgramList==null || reload){
            buddyProgramList = getBuddyPrograms();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); // adjust format to your string

        Date now = new Date();

        List<BuddyProgram> upcoming = buddyProgramList.stream()
                .filter(p -> {
                    try {
                        Date start = sdf.parse(p.getStartDate());
                        return start.after(now);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toList());
        Log.d("upcoming",String.valueOf(upcoming));

        List<BuddyProgram> ongoing = buddyProgramList.stream()
                .filter(p -> {
                    try {
                        Date start = sdf.parse(p.getStartDate());
                        Date end = sdf.parse(p.getDeadline());
                        return !start.after(now) && !end.before(now);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toList());

        List<BuddyProgram> past = buddyProgramList.stream()
                .filter(p -> {
                    try {
                        Date end = sdf.parse(p.getDeadline());
                        return end.before(now);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toList());
        switch (active){
            case "ongoing":{
                renderPrograms(ongoing);
                break;
            }
            case "upcoming":{
                renderPrograms(upcoming);
                break;
            }
            case "past":{
                renderPrograms(past);
                break;
            }
        }

    }
    
    

    private List<BuddyProgram> getBuddyPrograms() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Study Buddy Program...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();

        final List<BuddyProgram>[] buddyPrograms = new List[]{getLocalPrograms()}; //50 random BuddyPrograms
        Call<ResponseDto<List<BuddyProgram>>> call = api.getBuddyPrograms(); //50 random BuddyPrograms
        call.enqueue(new Callback<ResponseDto<List<BuddyProgram>>>() {
            @Override
            public void onResponse(Call<ResponseDto<List<BuddyProgram>>> call, Response<ResponseDto<List<BuddyProgram>>> response) {
                progressDialog.dismiss();
                Log.d("API response", "Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<List<BuddyProgram>> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        //store token logic
                        buddyPrograms[0] = dto.getData();

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
            public void onFailure(Call<ResponseDto<List<BuddyProgram>>> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("API response", "Failed to reach the server " + t.getMessage());
            }

        });
        return buddyPrograms[0];
    }
    
    private List<BuddyProgram> getLocalPrograms(){
        return getDummyBuddyPrograms();
    }
    
    public void renderPrograms(List<BuddyProgram> programs){
        container.removeAllViews();
        if(programs==null || programs.isEmpty()){
            View empty = getLayoutInflater().inflate(R.layout.empty_state, container, false);
            TextView heading  = empty.findViewById(R.id.heading);
            TextView description  = empty.findViewById(R.id.description);
            ImageView icon = empty.findViewById(R.id.icon);

            heading.setText("Nothing Here Yet 🌟");
            description.setText("Stay tuned! New study programs will appear here soon. Meanwhile, you can start one and invite your study buddies.");
            icon.setImageResource(R.drawable.buddy);


            container.addView(empty);
        }else{
            for(BuddyProgram program : programs){
                View programBox = getLayoutInflater().inflate(R.layout.item_buddy_program, container, false);
                TextView heading = programBox.findViewById(R.id.programTitle);
                TextView creator = programBox.findViewById(R.id.creatorName);
                TextView buddies = programBox.findViewById(R.id.buddiesCount);
                TextView daysLeft = programBox.findViewById(R.id.daysLeft);
                TextView status = programBox.findViewById(R.id.statusBadge);
                TextView description = programBox.findViewById(R.id.descriptionP);
                ProgressBar progressBar = programBox.findViewById(R.id.progressBar);
                TextView progressText = programBox.findViewById(R.id.progressText);
                ImageView icon = programBox.findViewById(R.id.programIcon);
                Button btn  = programBox.findViewById(R.id.actionButton);

                heading.setText(program.getTitle());
                creator.setText(program.getAuthorName());
                description.setText(program.getDescription());
                buddies.setText(program.getBuddies().size() + " Study Buddies");

                Date today = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                boolean beforeStart = false;
                boolean beforeEnd = false;
                try {
                    // Parse start date
                    Date startDate = sdf.parse(program.getStartDate());
                    Date endDate = sdf.parse(program.getDeadline());

                    // Compare
                    if(today.before(startDate)) {
                        beforeStart = true;
                    } else if(today.before(endDate)) {
                        beforeEnd = true;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long days = 0;
                if(beforeStart){
                    //upcoming
                    days = daysDifference(program.getStartDate());
                    daysLeft.setText("Starts in "+days+" Days ");
                    status.setText("Upcoming");
                    status.setBackgroundColor(getResources().getColor(R.color.green));
                }
                else if(beforeEnd){
                    //ongoing
                    days = daysDifference(program.getStartDate());
                    daysLeft.setText("Ends in "+days+" Days ");
                    status.setText("Ongoing");
                    status.setBackgroundColor(getResources().getColor(R.color.primary_light));
                }
                else{
                    //finished or due
                    days = daysDifference(program.getDeadline());
                    daysLeft.setText(days+" Days Due");
                    status.setText("Already Due");
                    status.setBackgroundColor(getResources().getColor(R.color.red));
                }
                SessionManager sessionManager = new SessionManager(this);
                boolean exists = program.getBuddies().stream()
                        .anyMatch(us -> us.getUserId().equals(sessionManager.getId()));

                if(exists){
                    //collect the milestones completed
                    int rate =0;
                    for(Milestone milestone: program.getMileStoneList()){
                        boolean hasSubmitted = milestone.getSubmissionList().stream()
                                .anyMatch(submission -> submission.getUserId().equals(sessionManager.getId()));
                        if(hasSubmitted){
                            rate++;
                        }
                    }

                    float percentage = ((float) rate /program.getMileStoneList().size())*100;
                    progressBar.setProgress((int)percentage);
                    progressText.setText( percentage + "% Complete");
                }else{
                    progressBar.setVisibility(View.GONE);
                    progressText.setVisibility(View.GONE);
                    btn.setText("View Program");
                }

                if(program.isOpened()){
                    icon.setImageResource(R.drawable.open);
                }else{
                    icon.setImageResource(R.drawable.close);
                }

                btn.setOnClickListener(v->{
                    Intent intent = new Intent(this, BuddyProgramActivity.class);
                    intent.putExtra("isMember",exists);
                    intent.putExtra("programId", program.getId());
                });


                container.addView(programBox);



            }


        }
    }

    public long daysDifference(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date today = new Date();

        try {
            Date startDate = sdf.parse(date);

            if(today.before(startDate)) {
                long diffInMillis = startDate.getTime() - today.getTime();
                long daysDiff = Math.abs(TimeUnit.MILLISECONDS.toDays(diffInMillis));;
                return daysDiff;
            } else {
                System.out.println("Program has started or is in the past");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<BuddyProgram> getDummyBuddyPrograms() {
        List<BuddyProgram> buddyPrograms = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            BuddyProgram program = new BuddyProgram();
            program.setId("prog-" + i);
            program.setTitle("Buddy Program " + i);
            program.setDescription("This is a description for program " + i);
            program.setDeadline("30-09-2025");
            program.setStartDate("01-09-2025");
            program.setOpened(i % 2 == 0); // alternate between open/closed
            program.setAuthorId("author-" + i);
            program.setAuthorName("Author " + i);

            // ==== Milestones ====
            List<Milestone> milestones = new ArrayList<>();
            for (int j = 1; j <= 2; j++) {
                Milestone milestone = new Milestone();
                milestone.setName("Milestone " + j + " of Program " + i);
                milestone.setDescription("Description of milestone " + j);
                milestone.setQuiz(j % 2 == 0);
                milestone.setTaskOrQuizCode("TQ-" + i + "-" + j);

                // ==== Submissions ====
                List<Submission> submissions = new ArrayList<>();
                for (int k = 1; k <= 2; k++) {
                    Submission submission = new Submission();
                    submission.setUserId("user-" + k);
                    submission.setSubmittedDate("26-08-2025");
                    submission.setDescription("Submission " + k + " for milestone " + j);
                    submission.setSubmissionUrl("http://example.com/submission/" + i + j + k);
                    submission.setFileSubmission(k % 2 == 0);
                    submission.setScore(k * 10);

                    // ==== Reviews ====
                    List<Review> reviews = new ArrayList<>();
                    for (int r = 1; r <= 2; r++) {
                        Review review = new Review();
                        review.setUserId("reviewer-" + r);
                        review.setAuthorName("Reviewer " + r);
                        review.setDescription("Review " + r + " on submission " + k);
                        review.setDate("26-08-2025");
                        review.setScore(r * 5);
                        review.setSubmissionReview(true);
                        reviews.add(review);
                    }

                    submission.setReviews(reviews);

                    submissions.add(submission);
                }
                milestone.setSubmissionList(submissions);

                milestones.add(milestone);
            }
            program.setMileStoneList(milestones);

            // ==== Buddy Scores ====
            List<BuddyScore> buddyScores = new ArrayList<>();
            for (int b = 1; b <= 3; b++) {
                BuddyScore buddyScore = new BuddyScore();
                buddyScore.setUserId("buddy-" + b);
                buddyScore.setScore(b * 20);
                buddyScores.add(buddyScore);
            }
            program.setBuddies(buddyScores);

            buddyPrograms.add(program);
        }

        return buddyPrograms;
    }

}
