package com.example.clexis.activity;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.icu.util.LocaleData;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import com.example.clexis.models.dto.JoinRequest;
import com.example.clexis.models.entity.BuddyProgram;
import com.example.clexis.models.response.ResponseDto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuddyProgramActivity extends AppCompatActivity {

    private ApiService api;
    private BuddyProgram program;
    private TextView title;
    private String id;
    private String isMember;
    private TabLayout tabLayout;
    private View dialogView;

    private boolean isOnline;
    private Uri selectedFileUri;

    private final Utils utils = new Utils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buddy_program_overview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }

        api = ApiClient.getRetrofitInstance(this).create(ApiService.class);
        
        Intent intent = getIntent();
        isMember = intent.getStringExtra("isMember");
        if(isMember==null){
            isMember="false";
        }
        isMember = "true"; // for test
        id = intent.getStringExtra("id");
        title = findViewById(R.id.titleB);

        setupActivity();
       
        
        View goalContent = findViewById(R.id.goals);
        View submissionContent = findViewById(R.id.submissions);
        View leaderboardContent = findViewById(R.id.leaderboard);
        View requestsContent = findViewById(R.id.requests);
        View notMember = findViewById(R.id.notAllowed);


        tabLayout = findViewById(R.id.tabLayout);
        TabLayout.Tab tab = tabLayout.getTabAt(3); // Get tab at position 1
        if (tab != null) {
            tab.view.setVisibility(GONE);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Login tab
                        goalContent.setVisibility(VISIBLE);
                        submissionContent.setVisibility(GONE);
                        leaderboardContent.setVisibility(GONE);
                        requestsContent.setVisibility(GONE);
                        break;
                    case 1: // Signup tab
                        goalContent.setVisibility(GONE);
                        if(isMember.equals("true")){
                            submissionContent.setVisibility(VISIBLE);
                        }else{
                            notMember.setVisibility(VISIBLE);
                        }

                        leaderboardContent.setVisibility(GONE);
                        requestsContent.setVisibility(GONE);
                        break;
                    case 2: // Signup tab
                        goalContent.setVisibility(GONE);
                        submissionContent.setVisibility(GONE);
                        if(isMember.equals("true")){
                            leaderboardContent.setVisibility(VISIBLE);
                        }else{
                            notMember.setVisibility(VISIBLE);
                        }

                        requestsContent.setVisibility(GONE);
                        break;

                    case 3: // Signup tab
                        requestsContent.setVisibility(VISIBLE);
                        goalContent.setVisibility(GONE);
                        submissionContent.setVisibility(GONE);
                        leaderboardContent.setVisibility(GONE);
                        break;
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }


    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    if (selectedFileUri != null) {
                        // Update the TextView in your dialog
                        TextView tvSelectedFile = dialogView.findViewById(R.id.tvSelectedFile);
                        tvSelectedFile.setText(selectedFileUri.getLastPathSegment());
                    }
                }
            });


    @SuppressLint("ResourceAsColor")
    private void setupActivity() {
        program = getBuddyProgram(id);
        if(program==null){
            finish();
        }else{
           title.setText(program.getTitle());
           View goalComponent = findViewById(R.id.goalLayout);
           View submissionsComponent = findViewById(R.id.submissions);
           View leaderboardComponent = findViewById(R.id.leaderboard);
           View requestComponent = findViewById(R.id.requests);

           TextView description2 = goalComponent.findViewById(R.id.goalDescription);
           description2.setText(program.getDescription());
            String initial = utils.getInitials(program.getAuthorName());
            TextView initials = goalComponent.findViewById(R.id.tv_profile_initials);
            initials.setText(initial);
            TextView author = goalComponent.findViewById(R.id.goalAuthor);
            author.setText(program.getAuthorName());

            TextView status = goalComponent.findViewById(R.id.status);
            TextView date = goalComponent.findViewById(R.id.startOrEnd);
            TextView  buddies = goalComponent.findViewById(R.id.buddiesCount);
            TextView joinBtn = goalComponent.findViewById(R.id.requestToJoin);
            LinearLayout timeline = goalComponent.findViewById(R.id.timelineContainer);
            View progress = goalComponent.findViewById(R.id.progressTracker);


            if(program.isOpened() && isMember.equals("false")){
                joinBtn.setVisibility(VISIBLE);

                joinBtn.setOnClickListener(v -> {
                    // Create TextInputLayout with enhanced Material styling
                    TextInputLayout textInputLayout = new TextInputLayout(BuddyProgramActivity.this, null,
                            com.google.android.material.R.attr.textInputOutlinedStyle);
                    textInputLayout.setHint("Reason for joining");
                    textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
                    textInputLayout.setBoxCornerRadii(16, 16, 16, 16);

                    // Enhanced styling for TextInputLayout
                    textInputLayout.setPadding(40, 40, 40, 40);
                    textInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.primary));
                    textInputLayout.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary)));
                    textInputLayout.setBoxStrokeWidth(2);
                    textInputLayout.setBoxStrokeWidthFocused(3);

                    // Set helper text for better UX
                    textInputLayout.setHelperText("Tell us what motivates you to join this program");
                    textInputLayout.setHelperTextColor(ColorStateList.valueOf(
                            ContextCompat.getColor(this, android.R.color.darker_gray)));

                    // Create TextInputEditText with improved styling
                    TextInputEditText input = new TextInputEditText(BuddyProgramActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                    input.setMinLines(3);
                    input.setMaxLines(6);
                    input.setGravity(Gravity.TOP | Gravity.START);
                    input.setPadding(16, 16, 16, 16);
                    input.setTextSize(16);
                    input.setLineSpacing(4, 1.2f); // Better line spacing for readability

                    // Set text colors
                    input.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                    input.setHintTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

                    // Add EditText inside TextInputLayout
                    textInputLayout.addView(input);

                    // Create dialog with custom styling
                    AlertDialog dialog = new MaterialAlertDialogBuilder(BuddyProgramActivity.this)
                            .setTitle("Join Buddy Program")
                            .setMessage("Please tell us why you want to join this program. Your response will help us understand your motivation and goals.")
                            .setView(textInputLayout)
                            .setPositiveButton("Submit", null) // Set to null initially for custom validation
                            .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                            .create();

                    // Apply custom styling to dialog
                    dialog.setOnShowListener(dialogInterface -> {
                        // Style the dialog background
                        Window window = dialog.getWindow();
                        if (window != null) {
                            window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

                            // Add subtle elevation/shadow
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                window.setElevation(16f);
                            }

                            // Rounded corners for dialog
                            GradientDrawable shape = new GradientDrawable();
                            shape.setShape(GradientDrawable.RECTANGLE);
                            shape.setCornerRadius(24f);
                            shape.setColor(Color.WHITE);
                            window.setBackgroundDrawable(shape);
                        }

                        // Style the buttons
                        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                        if (positiveButton != null) {
                            positiveButton.setTextColor(ContextCompat.getColor(this, R.color.primary));
                            positiveButton.setTypeface(null, Typeface.BOLD);
                            positiveButton.setTextSize(14);
                            positiveButton.setAllCaps(false);

                            // Custom click listener for validation
                            positiveButton.setOnClickListener(buttonView -> {
                                String reason = input.getText().toString().trim();

                                if (reason.isEmpty()) {
                                    // Show error on TextInputLayout instead of Toast
                                    textInputLayout.setError("Please provide a reason before submitting");
                                    textInputLayout.setErrorEnabled(true);

                                    // Shake animation for visual feedback
                                    Animation shake = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
                                    textInputLayout.startAnimation(shake);
                                    return;
                                } else if (reason.length() < 10) {
                                    textInputLayout.setError("Please provide a more detailed reason (at least 10 characters)");
                                    textInputLayout.setErrorEnabled(true);
                                    return;
                                } else {
                                    textInputLayout.setError(null);
                                    textInputLayout.setErrorEnabled(false);
                                }

                                // Disable button to prevent multiple submissions
                                positiveButton.setEnabled(false);
                                positiveButton.setText("Submitting...");

                                // Process the request
                                SessionManager sessionManager = new SessionManager(BuddyProgramActivity.this);
                                var request = JoinRequest.builder()
                                        .why(reason)
                                        .userId(sessionManager.getId())
                                        .build();

                                program = getBuddyProgram(id);

                                if (isOnline) {
                                    var requests = program.getRequests();
                                    requests.add(request);
                                    var updated = updateProgram();

                                    if (updated) {
                                        // Success styling
                                        Toast toast = Toast.makeText(BuddyProgramActivity.this,
                                                "✓ Your request has been submitted successfully!",
                                                Toast.LENGTH_LONG);
                                        // Custom toast styling could be added here
                                        toast.show();
                                        dialog.dismiss();
                                    } else {
                                        // Re-enable button on error
                                        positiveButton.setEnabled(true);
                                        positiveButton.setText("Submit");

                                        Toast.makeText(BuddyProgramActivity.this,
                                                "⚠ Error occurred while processing request. Please try again.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    // Re-enable button on error
                                    positiveButton.setEnabled(true);
                                    positiveButton.setText("Submit");

                                    Toast.makeText(BuddyProgramActivity.this,
                                            "⚠ No internet connection. Please check your network and try again.",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        if (negativeButton != null) {
                            negativeButton.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                            negativeButton.setTextSize(14);
                            negativeButton.setAllCaps(false);
                        }
                    });

                    dialog.show();
                });


            }
            buddies.setText(program.getBuddies().size() + " Buddies");


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


            if(beforeStart){
                //upcoming
                status.setText("Upcoming");
                date.setText(program.getStartDate());
                status.setTextColor(getResources().getColor(R.color.green));
            }
            else if(beforeEnd){
                //ongoing
                status.setText("Ongoing");
                date.setText(program.getDeadline());
                status.setTextColor(getResources().getColor(R.color.primary_light));
            }
            else{
                //finished or due
                status.setText("Already Due");
                date.setText(program.getDeadline());
                status.setTextColor(getResources().getColor(R.color.red));
            }
            int completionCount =0;
            timeline.removeAllViews();
            int milestones = 0;
            for(Milestone milestone: program.getMileStoneList()){

                //milestone setup
                View milestoneContainer = getLayoutInflater().inflate(R.layout.timeline_task_item, timeline,false);
                TextView title = milestoneContainer.findViewById(R.id.milestoneTitle);
                TextView descriptionM = milestoneContainer.findViewById(R.id.milestoneDesc);
                View section = milestoneContainer.findViewById(R.id.memberOnly);
                View notAllowed = milestoneContainer.findViewById(R.id.notAllowed);

                TextView done = milestoneContainer.findViewById(R.id.done);
                ImageView badge = milestoneContainer.findViewById(R.id.doneBadge);
                ImageView chevron = milestoneContainer.findViewById(R.id.chevron);
                LinearLayout taskSection = milestoneContainer.findViewById(R.id.taskSection);
                TextView taskDesc = milestoneContainer.findViewById(R.id.taskDesc);
                MaterialButton submitTaskBtn = milestoneContainer.findViewById(R.id.submitTaskButton);



                title.setText(milestone.getName());
                descriptionM.setText(milestone.getDescription());
                SessionManager sessionManager = new SessionManager(this);
                boolean submitted = false;
                int score = 0;
                if(isMember.equals("true")){
                    section.setVisibility(VISIBLE);
                    notAllowed.setVisibility(GONE);
                    taskDesc.setText(milestone.getTaskOrQuizCode());
                   for(Submission submission: milestone.getSubmissionList()){
                       if(submission.getUserId().equals(sessionManager.getId())){
                           submitted = true;
                           score = submission.getScore();
                           completionCount++;
                       }
                   }
                    final boolean[] showTaskSection = {false};
                   chevron.setOnClickListener(V->{
                       if(!showTaskSection[0]){
                           taskSection.setVisibility(VISIBLE);
                           chevron.setImageResource(R.drawable.chevron_up);
                           showTaskSection[0] = true;
                       }else{
                           taskSection.setVisibility(GONE);
                           chevron.setImageResource(R.drawable.chevron_down);
                           showTaskSection[0] = false;
                       }

                   });

                   if(submitted){
                       done.setText("Completed");
                       done.setTextColor(getResources().getColor(R.color.green));
                       badge.setImageResource(R.color.green);
                       submitTaskBtn.setVisibility(VISIBLE);
                       submitTaskBtn.setBackgroundColor(getResources().getColor(R.color.primary_light));
                       submitTaskBtn.setCornerRadius(0);
                       submitTaskBtn.setText(score+" / 10");


                   }else{
                       done.setText("Pending");
                       done.setTextColor(getResources().getColor(R.color.primary_light));
                       badge.setImageResource(R.color.primary_light);
                       if(!beforeStart){
                           submitTaskBtn.setVisibility(VISIBLE);
                           submitTaskBtn.setOnClickListener(v -> {
                               LayoutInflater inflater = LayoutInflater.from(v.getContext());
                               dialogView = inflater.inflate(R.layout.dialog_submit_task, null);

                               // Bind views inside the dialog
                               TextView tvMilestoneName = dialogView.findViewById(R.id.tvMilestoneName);
                               TextView tvSelectedFile = dialogView.findViewById(R.id.tvSelectedFile);
                               TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);
                               MaterialButton btnUploadFile = dialogView.findViewById(R.id.btnUploadFile);
                               MaterialButton btnSubmit = dialogView.findViewById(R.id.btnSubmit);

                               // Example: set milestone name dynamically
                               tvMilestoneName.setText(milestone.getName());

                               // Create BottomSheetDialog
                               com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                                       new com.google.android.material.bottomsheet.BottomSheetDialog(v.getContext());
                               bottomSheetDialog.setContentView(dialogView);
                               bottomSheetDialog.setCancelable(true);

                               // File picker
                               btnUploadFile.setOnClickListener(fileBtn -> {
                                   Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                   intent.setType("*/*"); // any file
                                   intent.addCategory(Intent.CATEGORY_OPENABLE);
                                   filePickerLauncher.launch(Intent.createChooser(intent, "Select File"));
                               });

                               btnSubmit.setOnClickListener(submitBtn -> {
                                   boolean file = true;
                                   String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

                                   if (selectedFileUri == null) {
                                       file= false;
                                   }

                                   if (description.isEmpty()) {
                                       Toast.makeText(v.getContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
                                       return;
                                   }
                                   SessionManager sessionManager1 = new SessionManager(this);
                                   Date now = new Date();
                                   //do the cloudinary logic
                                   Submission submission = Submission.builder()
                                           .userId(sessionManager1.getId())
                                           .description(description)
                                           .fileSubmission(file)
                                           .submissionUrl("http://cloudinary")
                                           .submittedDate(sdf.format(now))
                                           .score(0)
                                           .reviews(List.of())
                                           .build();
                                   program = getBuddyProgram(id);

                                   if (isOnline) {
                                       var submissions = milestone.getSubmissionList();
                                       submissions.add(submission);
                                       var updated = updateProgram();

                                       if (updated) {
                                           // Success styling
                                           Toast toast = Toast.makeText(BuddyProgramActivity.this,
                                                   "✓ Your task submission has been done successfully!",
                                                   Toast.LENGTH_LONG);
                                           // Custom toast styling could be added here
                                           toast.show();
                                           bottomSheetDialog.dismiss();
                                       } else {

                                           Toast.makeText(BuddyProgramActivity.this,
                                                   "⚠ Error occurred while processing request. Please try again.",
                                                   Toast.LENGTH_LONG).show();
                                       }
                                   } else {
                                       // Re-enable button on error

                                       Toast.makeText(BuddyProgramActivity.this,
                                               "⚠ No internet connection. Please check your network and try again.",
                                               Toast.LENGTH_LONG).show();
                                   }

                               });
                               // Build the modal dialog
                               bottomSheetDialog.show();

                           });

                       }
                       else{
                           submitTaskBtn.setVisibility(GONE);
                       }


                   }
                }else{
                    section.setVisibility(GONE);
                    notAllowed.setVisibility(VISIBLE);
                }

                timeline.addView(milestoneContainer);

                LinearLayout userList = leaderboardComponent.findViewById(R.id.users);

                //leaderboard setups
                View leaderboardContainer = getLayoutInflater().inflate(R.layout.fragment_buddy_leaderboard, (ViewGroup) leaderboardComponent,false);
                TextView programTitle = leaderboardComponent.findViewById(R.id.programTitle);
                TextView programDaysLeft = leaderboardComponent.findViewById(R.id.daysLeft);
                TextView programParticipants = leaderboardComponent.findViewById(R.id.participants);

                programTitle.setText(program.getTitle());
                programParticipants.setText(program.getBuddies().size() + " Participants");
                programDaysLeft.setText(program.getStartDate() + " to "+ program.getDeadline());

                List<BuddyScore> scores = new ArrayList<>(program.getBuddies());

                scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
                int userScore = 0 ;
                int rank= 0;
                SessionManager sessionManager2 = new SessionManager(this);
                for(BuddyScore bScore:scores){
                    if(bScore.getUserId().equals(sessionManager2.getId())){
                        userScore = bScore.getScore();
                        rank = scores.indexOf(bScore) + 1;
                    }
                }
                TextView ranking = leaderboardComponent.findViewById(R.id.rank);
                ranking.setText("You ranked "+rank +" with "+userScore +" Points");
                scores = scores.size() > 10 ? scores.subList(0, 10) : new ArrayList<>(scores);
                if(scores.get(0).getScore()==0){
                    View empty1 = getLayoutInflater().inflate(R.layout.empty_state,userList,false);
                    TextView heading  = empty1.findViewById(R.id.heading);
                    TextView description  = empty1.findViewById(R.id.description);
                    ImageView iconB = empty1.findViewById(R.id.icon);

                    heading.setText("Competition Coming Soon! \uD83D\uDE80");
                    description.setText("There are no submissions yet, so the leaderboard is empty. Be the first to submit your progress and kick off the competition!");
                    iconB.setImageResource(R.drawable.socials);

                    userList.addView(empty1);
                }else{
                    userList.removeAllViews();
                    for(BuddyScore buddyScore: scores){

                        View user = getLayoutInflater().inflate(R.layout.component_leaderboard_users,userList,false);
                        TextView number = user.findViewById(R.id.rankB);
                        int num = scores.indexOf(buddyScore) + 1;
                        number.setText(num +". ");
                        TextView chatName = user.findViewById(R.id.tv_chat_name);
                        TextView chatInitials = user.findViewById(R.id.tv_profile_initials);
                        TextView points = user.findViewById(R.id.points);
                        ImageView medals = user.findViewById(R.id.medal);

                        chatName.setText(buddyScore.getUsername());
                        chatInitials.setText(utils.getInitials(buddyScore.getUsername()));
                        points.setText(buddyScore.getScore() + " Points");

                        if(scores.indexOf(buddyScore) <=3 ){
                            user.setBackgroundColor(getColor(R.color.green));
                        }

                        //adjust medals in future


                        userList.addView(user);
                    }

                }





                //submissions setup
                View submissionsContainer = getLayoutInflater().inflate(R.layout.fragment_milestone_submission, (ViewGroup) submissionsComponent,false);
                MaterialCardView milestoneBox  = submissionsContainer.findViewById(R.id.milestoneHeader);
                LinearLayout submissionList = submissionsContainer.findViewById(R.id.submissionList);
                LinearLayout submissions = submissionsContainer.findViewById(R.id.submissions);
                ImageView icon = submissionsContainer.findViewById(R.id.icon);
                TextView milestoneCount = submissionsContainer.findViewById(R.id.milestoneCount);
                TextView milestoneTitle = submissionsContainer.findViewById(R.id.milestoneTitle);
                TextView submissionCounts = submissionsContainer.findViewById(R.id.submissionCount);
                LinearLayout milestonesList = submissionsComponent.findViewById(R.id.submissionContainer);

                int mNumber = milestones +1;
                milestoneCount.setText("Milestone "+ mNumber );
                milestones++;
                milestoneTitle.setText(milestone.getName());
                submissionCounts.setText(milestone.getSubmissionList().size() + " Submissions");

                milestonesList.addView(submissionsContainer);

                milestoneBox.setOnClickListener(v->{
                    // Cast the view to CardView
                    CardView cardView = (CardView) v;

                    // Use ContextCompat to get the actual color value
                    int white = ContextCompat.getColor(v.getContext(), R.color.white);
                    int black = ContextCompat.getColor(v.getContext(), R.color.black);
                    int primaryLight = ContextCompat.getColor(v.getContext(), R.color.primary);

                    View empty = getLayoutInflater().inflate(R.layout.empty_state,submissionList,false);
                    TextView heading  = empty.findViewById(R.id.heading);
                    TextView description  = empty.findViewById(R.id.description);
                    ImageView iconB = empty.findViewById(R.id.icon);

                    heading.setText("No Submission Here Yet 🌟");
                    description.setText("No submissions yet. Be the first to submit your progress and inspire your study buddies!");
                    iconB.setImageResource(R.drawable.flashcard);

                    if(milestone.getSubmissionList().size()==0){
                        submissions.addView(empty);
                    }else{
                        for(Submission submission: milestone.getSubmissionList()){
                            View singleSubmission = getLayoutInflater().inflate(R.layout.milestone_card,submissionList,false);
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
                                //add doenload logic
                            }else{
                                fileBox.setVisibility(GONE);
                            }

                            totalScore.setText(submission.getScore() + " /20");
                            responses.setText(submission.getReviews().size() + " Reviews");

                            btn.setOnClickListener(Vv->{
                                Intent intent = new Intent(this, SubmissionDetailActivity.class);
                                intent.putExtra("programId", program.getId());
                                intent.putExtra("milestoneIndex",String.valueOf(program.getMileStoneList().indexOf(milestone)));
                                Gson gson = new Gson();
                                String resourceJson = gson.toJson(submission);

                                intent.putExtra("submission", resourceJson);
                                startActivity(intent);
                            });

                            submissions.addView(singleSubmission);
                        }
                    }

                    if (submissionList.getVisibility() == View.VISIBLE) {
                        cardView.setCardBackgroundColor(white);
                        milestoneCount.setTextColor(black);
                        milestoneTitle.setTextColor(black);
                        submissionCounts.setTextColor(black);
                        icon.setColorFilter(black, PorterDuff.Mode.SRC_IN);
                        submissionList.setVisibility(GONE); // hide it

                        submissions.removeAllViews();



                    } else {
                        cardView.setCardBackgroundColor(primaryLight);
                        milestoneCount.setTextColor(white);
                        milestoneTitle.setTextColor(white);
                        submissionCounts.setTextColor(white);
                        icon.setColorFilter(white, PorterDuff.Mode.SRC_IN);
                        submissionList.setVisibility(View.VISIBLE); // show it
                    }

                });



            }
            if(isMember.equals("true")){
                progress.setVisibility(VISIBLE);
                TextView metrics = goalComponent.findViewById(R.id.completionMetrics);
                TextView rate = goalComponent.findViewById(R.id.percentage);
                ProgressBar bar = goalComponent.findViewById(R.id.pBar);
                String metricsValue = completionCount+" of "+program.getMileStoneList().size() +" tasks completed";
                metrics.setText(metricsValue);
                int percentage = (int) (double) ((completionCount / program.getMileStoneList().size()) * 100);
                rate.setText(percentage + "% completed");
                bar.setProgress(percentage);

            }else{
                progress.setVisibility(GONE);
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
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Study Buddy Program...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();

        final BuddyProgram[] path = {getLocalBuddyProgram()};
        Call<ResponseDto<BuddyProgram>> call = api.getBuddyProgram(id);
        call.enqueue(new Callback<ResponseDto<BuddyProgram>>() {
            @Override
            public void onResponse(Call<ResponseDto<BuddyProgram>> call, Response<ResponseDto<BuddyProgram>> response) {
                progressDialog.dismiss();
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
                progressDialog.dismiss();
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
        buddy1.setUsername("Buddy1");
        buddy1.setScore(15);

        BuddyScore buddy2 = new BuddyScore();
        buddy2.setUserId("user-002");
        buddy1.setUsername("Buddy2");
        buddy2.setScore(12);

        BuddyScore buddy3 = new BuddyScore();
        buddy3.setUserId("user-003");
        buddy3.setUsername("Buddy3");
        buddy3.setScore(5);

        BuddyScore buddy4 = new BuddyScore();
        buddy4.setUserId("user-004");
        buddy4.setUsername("Buddy4");
        buddy4.setScore(6);

        buddyProgram.setBuddies(List.of(buddy1, buddy2));

        return buddyProgram;

    }
}
