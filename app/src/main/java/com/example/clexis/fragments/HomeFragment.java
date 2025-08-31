package com.example.clexis.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.clexis.R;
import com.example.clexis.activity.AddLearningPathActivity;
import com.example.clexis.activity.ViewLearningPlanActivity;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.Utils;
import com.example.clexis.models.dto.Module;
import com.example.clexis.models.dto.Task;
import com.example.clexis.models.entity.LearningPath;
import com.example.clexis.models.response.ResponseDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private ProgressDialog progressDialog;
    private View view;
    private LinearLayout emptyContainer,pathContainer,taskContainer,taskFragments;
    private ApiService api;
    private LearningPath path;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();

        emptyContainer = view.findViewById(R.id.emptyContainer);
        pathContainer = view.findViewById(R.id.lpath);
        taskContainer = view.findViewById(R.id.todayFocus);
        taskFragments = view.findViewById(R.id.dailyTaskFragment);
        View noLpath = view.findViewById(R.id.noPath);

        ImageView icon = noLpath.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.pivot);
        TextView heading = noLpath.findViewById(R.id.heading);
        heading.setText("No Active Learning Path");
        TextView description = noLpath.findViewById(R.id.description);
        description.setText("You currently don’t have any active learning paths. Start a new path to continue your learning journey and unlock more skills");
        Button btn = noLpath.findViewById(R.id.actionBtn);
        btn.setText("Create a learning Path");
        btn.setVisibility(VISIBLE);
        btn.setOnClickListener(v->{
            startActivity(new Intent(this.getContext(), AddLearningPathActivity.class));
        });



        api = ApiClient.getRetrofitInstance(this.getContext()).create(ApiService.class);

        setUpHomeFragment();

        progressDialog.dismiss();

        return view;
    }

    public void setUpPersonal(){
        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        long lastUpdated = prefs.getLong("lastUpdated", 0);
        long now = System.currentTimeMillis();
        long diff = now - lastUpdated;
        int streak = prefs.getInt("streak", 0);
        Log.d("TEXT",String.valueOf(streak));
        String name = prefs.getString("studentName","Enow Brenda");
        SharedPreferences.Editor editor = prefs.edit();

        // 24 hours in milliseconds = 24 * 60 * 60 * 1000
        long twentyFourHours = 24 * 60 * 60 * 1000;

        if (diff >= twentyFourHours) {
            // More than 24 hours passed → reset streak
            streak = 0;
            editor.putInt("streak", streak);
            editor.putLong("lastUpdated", now);
            editor.apply();
        }
        TextView streakValue = view.findViewById(R.id.streak);
        streakValue.setText(String.valueOf(streak)+" Days Streak!");
        TextView greeting = view.findViewById(R.id.greet);
        greeting.setText("Hello, "+ name);
        TextView today = view.findViewById(R.id.date);
        Date todayD = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
        String date = sdf.format(todayD);
        today.setText(date);

    }
    public void setUpHomeFragment(){

        setUpPersonal();
        path = getLearningPath();
        if(path == null){
            emptyContainer.setVisibility(VISIBLE);
        }else{

            pathContainer.setVisibility(VISIBLE);
            taskContainer.setVisibility(VISIBLE);
            taskFragments.removeAllViews();

            Utils util = new Utils();
            int dayStatus = util.getDayStatus(path.getStartDate(),path.getDueDate());
            String after = " due";
            if(dayStatus == 0){
                after = " to Start";
            }
            else if(dayStatus == 1){
                after = " remaining";
            }
            TextView pafter = view.findViewById(R.id.after);
            pafter.setText(after);

            TextView ptitle = view.findViewById(R.id.pathTitle);
            TextView pDays = view.findViewById(R.id.days);
            TextView ptype = view.findViewById(R.id.pathType);
            ptitle.setText(path.getTitle());
            pDays.setText(path.getRemainingDays());
            ptype.setText("Goal Type: "+path.getGoalType());


             view.findViewById(R.id.viewBtn).setOnClickListener(v -> {
                 startActivity(new Intent(getContext(), ViewLearningPlanActivity.class));
             });

            List<Task> todayTasks = getTodayTasks(path);
            long pendingCount = todayTasks.stream()
                    .filter(task -> !task.isCompleted())
                    .count();
            long completedCount = todayTasks.stream()
                    .filter(Task::isCompleted)
                    .count();
            TextView pending = taskContainer.findViewById(R.id.pending);
            String count = String.valueOf(pendingCount)+" Pending";
            pending.setText(count);
            if(todayTasks.isEmpty()){
                emptyContainer.setVisibility(VISIBLE);
                View noLpath = view.findViewById(R.id.noPath);
                TextView heading = noLpath.findViewById(R.id.heading);
                heading.setText("No Task For today");
                TextView description = noLpath.findViewById(R.id.description);
                description.setText("You currently don’t have any task scheduled for today. Start a new path to continue your learning journey and unlock more skills");
                noLpath.findViewById(R.id.actionBtn).setVisibility(GONE);

            }else{
                emptyContainer.setVisibility(GONE);
                if(todayTasks.size() == completedCount){
                    SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    long lastUpdated = prefs.getLong("lastUpdated", 0);
                    Calendar lastCal = Calendar.getInstance();
                    lastCal.setTimeInMillis(lastUpdated);

                    Calendar todayCal = Calendar.getInstance(); // now

                    boolean isSameDay = lastCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR)
                            && lastCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR);


                    if(!isSameDay){

                        int streak = prefs.getInt("streak", 0);
                        TextView nstreak = view.findViewById(R.id.streak);
                        nstreak.setText(String.valueOf(streak+1)+" Days Streak!");

                        // Save something
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("streak", streak+1);
                        editor.putLong("lastUpdated",System.currentTimeMillis());
                        editor.apply();
                    }


                }
            }

            for(Task task: todayTasks){
                View taskBox = getLayoutInflater().inflate(R.layout.home_task_box, taskFragments, false);
                TextView title = taskBox.findViewById(R.id.taskTitle);
                TextView moduleName = taskBox.findViewById(R.id.moduleName);
                TextView  description= taskBox.findViewById(R.id.taskDesc);
                CheckBox check = taskBox.findViewById(R.id.checkbox);
                View doneIcon = taskBox.findViewById(R.id.done);

                title.setText(task.getTitle());
                description.setText(task.getDescription());

                if (task.isFrequentTask()) {
                    moduleName.setText("Frequent Task");
                } else {
                    moduleName.setText(task.getModuleName());
                }

                if (task.isCompleted()) {
                    check.setVisibility(View.GONE);
                    doneIcon.setVisibility(View.VISIBLE);
                } else {
                    check.setVisibility(View.VISIBLE);
                    doneIcon.setVisibility(View.GONE);
                }

                check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        // Mark task as completed
                        task.setCompleted(true);

                        // Update UI
                        check.setVisibility(View.GONE);
                        doneIcon.setVisibility(View.VISIBLE);



                        // Optionally persist the change (e.g., update Realm/SQLite/API)
                        saveLearningPath();

                    }
                });
                taskFragments.addView(taskBox);

            }
        }
    }

    public void saveLearningPath(){
        //hit the update endpoint then setup home fragment
        setUpHomeFragment();
    }
    public LearningPath getLearningPath(){
        if(path!=null){
            return path;
        }
        final LearningPath[] path = {getLocalLearningPath()};
        Call<ResponseDto<LearningPath>> call = api.getLearningPath();
        call.enqueue(new Callback<ResponseDto<LearningPath>>() {
            @Override
            public void onResponse(Call<ResponseDto<LearningPath>> call, Response<ResponseDto<LearningPath>> response) {
                progressDialog.dismiss();
                Log.d("API response", "Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<LearningPath> dto = response.body();
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
            public void onFailure(Call<ResponseDto<LearningPath>> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("API response", "Failed to reach the server " + t.getMessage());
            }

        });
        return path[0];

    }
    public LearningPath getLocalLearningPath(){
        path = new LearningPath();
        return path.getDefault();
    }

    public List<Task> getTodayTasks(LearningPath path){
//            List<Task> tasks = path.getFrequentTasks();
//            for(Module module: path.getModules()){
//                module.getTasks().forEach(task -> task.setModuleName(module.getTitle()));
//                tasks.addAll(module.getTasks());
//            }
        // Make a mutable copy
        List<Task> tasks = new ArrayList<>(path.getFrequentTasks());

        for(Module module: path.getModules()){
            module.getTasks().forEach(task -> task.setModuleName(module.getTitle()));
            tasks.addAll(module.getTasks()); // now safe
        }

        List<Task> todayTasks = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // 1 to 31
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            // convert to 0=Sunday ... 6=Saturday

            for (Task task : tasks) {
                int month = calendar.get(Calendar.MONTH) + 1; // Calendar months are 0-based
                int year = calendar.get(Calendar.YEAR);


                String today = String.format("%02d-%02d-%04d", dayOfMonth, month, year);
                // One-time task scheduled for today
                if (task.getDate() != null && task.getDate().equals(today)) {
                    todayTasks.add(task);
                    continue;
                }

                // Daily tasks
                if (Boolean.TRUE.equals(task.isDaily())) {
                    todayTasks.add(task);
                    continue;
                }

                // Weekly tasks
                if (Boolean.TRUE.equals(task.isWeekly()) && task.getSchedule() != null) {
                    if (task.getSchedule().contains(dayOfWeek)) {
                        todayTasks.add(task);
                        continue;
                    }
                }

                // Monthly tasks
                if (Boolean.TRUE.equals(task.isMonthly()) && task.getFrequency() > 0) {
                    if (dayOfMonth == task.getFrequency()) {
                        todayTasks.add(task);
                    }
                }
            }


            Collections.sort(todayTasks, (t1, t2) -> {
                if (t1.isCompleted() == t2.isCompleted()) {
                    return 0; // keep their relative order if both same status
                }
                return t1.isCompleted() ? 1 : -1; // completed goes after pending
            });

            return todayTasks;
        }

}

