package com.example.clexis.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.clexis.R;
import com.example.clexis.SessionManager;
import com.example.clexis.fragments.LibraryFragment;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.application.BuddyScore;
import com.example.clexis.models.dto.StudentProfileObject;
import com.example.clexis.models.entity.BuddyProgram;
import com.example.clexis.models.entity.Resource;
import com.example.clexis.models.entity.Student;
import com.example.clexis.models.enums.AcademicLevel;
import com.example.clexis.models.enums.ResourceType;
import com.example.clexis.models.response.ResponseDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AchievementActivity extends AppCompatActivity {

    private StudentProfileObject student;
    private ApiService api;

    private SessionManager sessionManager ;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_portfolio);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }
        api = ApiClient.getRetrofitInstance(this).create(ApiService.class);

        sessionManager = new SessionManager(this);
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        if(userId==null){
            userId = "user";
        }
        setupActivity();
    }

    private void setupActivity() {
        student = getStudentByUserId(userId);
        if(student==null){
            finish();
        }else{
            TextView name = findViewById(R.id.learnerName);
            TextView created = findViewById(R.id.dateCreated);
            Button icon = findViewById(R.id.iconButton);

            name.setText(student.getFullName());
            created.setText("Learner since "+student.getDateCreated().replace("T"," "));
            String myId = sessionManager.getId();
            if(myId == null) {
                myId = "user";
            }
            ScrollView profileScrollView = findViewById(R.id.scrollView);

            if(!myId.equals(userId)){
                icon.setText("Chat with Me");
                Drawable chatIcon = ContextCompat.getDrawable(this, R.drawable.chats);

                icon.setCompoundDrawablesWithIntrinsicBounds(chatIcon, null, null, null);
                icon.setOnClickListener(v->{
                    Intent newIntent = new Intent(this,ChatRoomActivity.class);
                    startActivity(newIntent);
                });
            }else{
                //handle share functionality onclick
                icon.setOnClickListener(v->{
                    shareProfileAsImage(profileScrollView);
                });

            }

            //setup metrics boxes
            Object[][] datas = new Object[3][3]; // 3 entries, each has 3 values

            datas[0] = new Object[]{"XP Points", String.valueOf(student.getPoints()), R.drawable.medal};
            datas[1] = new Object[]{"Completed", String.valueOf(student.getLearningPath()), R.drawable.l_path};
            datas[2] = new Object[]{"Leaderboard","#"+student.getRank(), R.drawable.portfolio};

            LinearLayout achievement = findViewById(R.id.achievements);

            for (Object obj : datas) {
                Object[] data = (Object[]) obj; // cast here
                View metricBox = getLayoutInflater().inflate(R.layout.item_achievement_card, achievement, false);
                TextView metric = metricBox.findViewById(R.id.metrics);
                TextView value = metricBox.findViewById(R.id.value);
                ImageView metricIcon = metricBox.findViewById(R.id.image);

                metric.setText((String) data[0]);
                value.setText(String.valueOf(data[1])); // if integer
                // set icon if needed
                metricIcon.setImageResource((int)data[2]);

                achievement.addView(metricBox);
            }
            //bio and interest setup
            TextView bio = findViewById(R.id.tv_bio);
            TextView level = findViewById(R.id.tv_academic_level);
            TextView profession = findViewById(R.id.tv_profession);
            TextView language = findViewById(R.id.tv_language);

            bio.setText(student.getBioOrInterest());
            level.setText(student.getAcademicLevel());
            profession.setText(student.getProfession());
            language.setText(student.getLanguage());


            //progress bar setup
            TextView completionRate = findViewById(R.id.completionMetrics);
            TextView percentageText = findViewById(R.id.percentage);
            ProgressBar progress = findViewById(R.id.pBar);
            Integer completed = student.getCompletedTasks();
            Integer total = student.getTasks();

            if (completed == null) completed = 0;
            if (total == null || total == 0) total = 1; // Avoid division by zero

            int percentage = (int) (((float) completed / total) * 100);
            completionRate.setText(completed + " out of "+total+" completed");

            percentageText.setText(percentage + "% completed");
            progress.setProgress(percentage);

            //
            Object[][] cDatas = new Object[3][3]; // 3 entries, each has 3 values

            cDatas[0] = new Object[]{"Discussion", String.valueOf(student.getDiscussions()), R.drawable.socials};
            cDatas[1] = new Object[]{"Competitions", String.valueOf(student.getPrograms()), R.drawable.buddy};
            cDatas[2] = new Object[]{"Resources",String.valueOf(student.getResourceShared()), R.drawable.library};

            LinearLayout community = findViewById(R.id.communityImpact);

            for (Object obj : cDatas) {
                Object[] data = (Object[]) obj; // cast here
                View metricBox = getLayoutInflater().inflate(R.layout.item_achievement_card, community, false);
                TextView metric = metricBox.findViewById(R.id.metrics);
                TextView value = metricBox.findViewById(R.id.value);
                ImageView metricIcon = metricBox.findViewById(R.id.image);

                metric.setText((String) data[0]);
                value.setText(String.valueOf(data[1])); // if integer
                // set icon if needed
                metricIcon.setImageResource((int)data[2]);

                community.addView(metricBox);
            }
            LinearLayout recentResources = findViewById(R.id.recentResources);
            LinearLayout recentProgram = findViewById(R.id.recentProgram);
            TextView heading1 = findViewById(R.id.heading1);
            TextView heading2 = findViewById(R.id.heading2);

            //recent shared resources
            if (student.getRecentResources()==null || student.getRecentResources().isEmpty()){
                heading1.setVisibility(View.GONE);
            }else{
                recentResources.removeAllViews();

                for(Resource res: student.getRecentResources()){
                    View card = getLayoutInflater().inflate(R.layout.item_user_upload, recentResources, false);

                    ((TextView) card.findViewById(R.id.tvTitle)).setText(res.getName());
                    ((TextView) card.findViewById(R.id.tvUploader)).setVisibility(View.GONE);
                    ((TextView) card.findViewById(R.id.tvDate)).setText("Uploaded: "+res.getCreationDate());
                    ImageView iconB = card.findViewById(R.id.ivTypeIcon);
                    iconB.setImageResource(getResourceIcon(res.getType().name()));

                    card.setOnClickListener(v -> {
                        Intent intent = new Intent(this, ResourceDescriptionActivity.class);

                        // Convert Resource object to JSON string
                        Gson gson = new Gson();
                        String resourceJson = gson.toJson(res);

                        intent.putExtra("resource", resourceJson);
                        startActivity(intent);
                    });
                    recentResources.addView(card);
                }

            }


            if (student.getRecentPrograms()==null || student.getRecentPrograms().isEmpty()){
                heading2.setVisibility(View.GONE);
            }else{
                recentProgram.removeAllViews();

                for(BuddyProgram program: student.getRecentPrograms()){
                    View card = getLayoutInflater().inflate(R.layout.buddy_competition_element, recentProgram, false);
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
                    TextView ranking = card.findViewById(R.id.rank);
                    TextView score = card.findViewById(R.id.scoreB);
                    TextView nameB = card.findViewById(R.id.name);
                    ImageView medal = card.findViewById(R.id.medal);

                    ranking.setText("Ranked "+ rank+ " of "+ scores.size()+ " participants");
                    score.setText(String.valueOf(userScore));
                    nameB.setText(program.getTitle());
                    if(rank == 1 ){
                        medal.setImageResource(R.drawable.first_medal);
                    }else if(rank == 2 ){
                        medal.setImageResource(R.drawable.second_place);
                    }else if(rank == 3){
                        medal.setImageResource(R.drawable.third_place);
                    }

                    recentProgram.addView(card);
                }
            }



        }
    }

    private void shareProfileAsImage(View profileView) {
        Bitmap bitmap = getBitmapFromView(profileView);
        Uri uri = saveBitmapToCache(bitmap);
        if(uri != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Profile"));
        }
    }
    private Bitmap getBitmapFromView(View view) {
        // Measure and layout the view
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        // Create bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw white background first
        canvas.drawColor(Color.WHITE);

        // Draw the view onto the canvas
        view.draw(canvas);

        return bitmap;
    }


    private Uri saveBitmapToCache(Bitmap bitmap) {
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // Create directory if not exists
            File file = new File(cachePath, "profile.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            // Get file URI using FileProvider
            return androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    file
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    private StudentProfileObject getStudentByUserId(String userId) {
            final StudentProfileObject[] student = {getLocalStudent()};
            Call<ResponseDto<StudentProfileObject>> call = api.getStudentProfile(userId);
            call.enqueue(new Callback<ResponseDto<StudentProfileObject>>() {
                @Override
                public void onResponse(Call<ResponseDto<StudentProfileObject>> call, Response<ResponseDto<StudentProfileObject>> response) {

                    Log.d("API response", "Code: " + response.code() + ", Message: " + response.message());

                    if (response.isSuccessful() && response.body() != null) {
                        ResponseDto<StudentProfileObject> dto = response.body();
                        if(dto.meta.statusCode == 200){
                            //store token logic
                            student[0] = dto.getData();

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
                public void onFailure(Call<ResponseDto<StudentProfileObject>> call, Throwable t) {

                    Log.d("API response", "Failed to reach the server " + t.getMessage());
                }

            });
            return student[0];

        }

    private StudentProfileObject getLocalStudent() {
        StudentProfileObject learner = new StudentProfileObject();
        learner.setId("L001");
        learner.setUserId("U1001");
        learner.setFullName("Kanjo Elkamira Ndi");
        learner.setAcademicLevel("Undergraduate"); // Example enum
        learner.setBioOrInterest("Passionate about AI, design, and community learning.");
        learner.setPhoneNumber("+237650000000");
        learner.setProfession("Software Developer");
        learner.setLanguage("English");
        learner.setPoints(1200);
        learner.setLearningPath(3); // Example: 3rd stage of learning
        learner.setRank(5); // Example rank among peers
        learner.setDiscussions(42);
        learner.setResourceShared(10);
        learner.setCompletedTasks(5);
        learner.setTasks(10);
        learner.setPrograms(4);

// Sample Buddy Programs
        List<BuddyProgram> programs = new ArrayList<>();
        BuddyProgramActivity buddyProgramActivity = new BuddyProgramActivity();
        programs.add(buddyProgramActivity.getLocalBuddyProgram());
        learner.setRecentPrograms(programs);

// Sample Resources
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
        List<Resource> resources = new ArrayList<>();
        resources.add(r1);
        resources.add(r2);

        learner.setRecentResources(resources);


// Date created
        learner.setDateCreated("2025-08-30T12:45:00Z");

        return learner;
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
