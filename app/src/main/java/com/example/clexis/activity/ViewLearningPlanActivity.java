package com.example.clexis.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.clexis.R;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.Utils;
import com.example.clexis.models.dto.Module;
import com.example.clexis.models.dto.Task;
import com.example.clexis.models.entity.LearningPath;
import com.example.clexis.models.response.ResponseDto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.ParseException;
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

public class ViewLearningPlanActivity extends AppCompatActivity {

    private LearningPath path;
    private ProgressDialog progressDialog;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_learning_plan);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }
        api = ApiClient.getRetrofitInstance(this).create(ApiService.class);

        setupActivity();
        LinearLayout modules = findViewById(R.id.moduleFragment);
        LinearLayout today = findViewById(R.id.dailyTaskFragment);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Login tab
                        modules.setVisibility(View.VISIBLE);
                        today.setVisibility(View.GONE);
                        break;
                    case 1: // Signup tab
                        modules.setVisibility(View.GONE);
                        today.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupActivity() {
        path = getLearningPath();
        if (path == null) {
            Intent newIntent = new Intent(this, AddLearningPathActivity.class);
            newIntent.putExtra("action","create");
            startActivity(newIntent);
            finish();
        } else {
            TextView pathTitle = findViewById(R.id.tv_path_title);
            TextView status  = findViewById(R.id.tv_path_status);
            ImageView edit  = findViewById(R.id.btn_edit_path);
            ImageView delete  = findViewById(R.id.btn_delete);

            edit.setOnClickListener(v -> {
                Intent newIntent = new Intent(this, AddLearningPathActivity.class);
                newIntent.putExtra("action","edit");
                Gson gson = new Gson();
                String pathJson = gson.toJson(path);

                newIntent.putExtra("path", pathJson);
                startActivity(newIntent);

            });
            delete.setOnClickListener(r->{
                new AlertDialog.Builder(this) // use "this" if inside Activity, or "getContext()" if inside Fragment
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this learning path?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            deletePath();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss(); // just close
                        })
                        .show();
            });

            pathTitle.setText(path.getTitle());
            Utils util = new Utils();
            int dayStatus = util.getDayStatus(path.getStartDate(),path.getDueDate());
            String after = " due";
            if(dayStatus == 0){
                after = " to Start";
            }
            else if(dayStatus == 1){
                after = " remaining";
            }

            status.setText("Active • "+path.getRemainingDays()+after);

            TextView ptitle = findViewById(R.id.ptitle);
            TextView pDays = findViewById(R.id.pdays);
            TextView ptype = findViewById(R.id.goalType);
            TextView pafter = findViewById(R.id.after);
            pafter.setText(after);
            ptitle.setText(path.getTitle());
            pDays.setText(path.getRemainingDays());
            ptype.setText("Goal Type: "+path.getGoalType());

            ProgressBar bar = findViewById(R.id.progress_overall);
            TextView pPercentage = findViewById(R.id.tv_progress_percentage);
            TextView pStats = findViewById(R.id.tv_progress_stats);

            int completedModules = 0;
            int completedTasks = 0;
            int totalTasks = 0;
            for(Module module: path.getModules()){
                totalTasks += module.getTasks().size();
                int cTasks = 0;
                for(Task task: module.getTasks()){
                    if(task.isCompleted()){
                        completedTasks++;
                        cTasks++;
                    }
                }
                if(cTasks==module.getTasks().size()){
                    completedModules++;
                }
            }

            if(completedModules == path.getModules().size()){
                LinearLayout completed = findViewById(R.id.completionSection);
                Button createNew = findViewById(R.id.createNewPathButton);
                completed.setVisibility(VISIBLE);
                createNew.setOnClickListener(v -> {
                    Intent newIntent = new Intent(this, AddLearningPathActivity.class);
                    newIntent.putExtra("action","create");
                    startActivity(newIntent);

                });
            }

            int percentage = (int) (((float) completedTasks / totalTasks) * 100);
            pPercentage.setText(percentage+ "% completed");
            bar.setProgress(percentage);
            pStats.setText(completedModules+" of "+path.getModules().size() +" objectives completed");

            LinearLayout frequentTasks = findViewById(R.id.frequent_tasks);

            if(path.getFrequentTasks() == null || path.getFrequentTasks().isEmpty()){
                View noLpath = getLayoutInflater().inflate(R.layout.empty_state,frequentTasks,false);
                TextView heading = noLpath.findViewById(R.id.heading);
                heading.setText("No Frequent Task");
                TextView description = noLpath.findViewById(R.id.description);
                description.setText("You currently don’t have any frequent task scheduled. ");
                frequentTasks.addView(noLpath);
            }else{
                frequentTasks.removeAllViews();
                for(Task task: path.getFrequentTasks()){
                    View taskBox = getLayoutInflater().inflate(R.layout.task_box_lp,frequentTasks,false);
                    TextView taskTitle = taskBox.findViewById(R.id.task_title);
                    TextView frequency = taskBox.findViewById(R.id.date);

                    taskTitle.setText(task.getTitle());
                    frequency.setText(getFrequency(task));
                    frequentTasks.addView(taskBox);
                }
            }

            //setup modules
            View moduleContainer = findViewById(R.id.moduleFragment);
            LinearLayout modules= moduleContainer.findViewById(R.id.modules);
            for(Module module:path.getModules()){
                View moduleBox = getLayoutInflater().inflate(R.layout.module_card, (ViewGroup) moduleContainer,false);
                TextView title = moduleBox.findViewById(R.id.moduleTitle);
                TextView desc = moduleBox.findViewById(R.id.moduleDesc);
                ImageView chevron = moduleBox.findViewById(R.id.chevron);
                LinearLayout tasks = moduleBox.findViewById(R.id.tasks);
                int index = path.getModules().indexOf(module) ;
                index++;

                title.setText("Module "+index +": "+module.getTitle());
                desc.setText("Objective: " +module.getObjective());
                chevron.setOnClickListener(v -> {
                    if (tasks.getVisibility() == View.VISIBLE) {
                        tasks.setVisibility(View.GONE);
                        chevron.setImageResource(R.drawable.chevron_down);
                    }else{
                        tasks.setVisibility(VISIBLE);
                        chevron.setImageResource(R.drawable.chevron_up);
                    }
                });

                for(Task task: module.getTasks()){
                    View taskBox = getLayoutInflater().inflate(R.layout.task_box_lp,tasks,false);
                    TextView taskTitle = taskBox.findViewById(R.id.task_title);
                    TextView frequency = taskBox.findViewById(R.id.date);
                    TextView description = taskBox.findViewById(R.id.task_description);
                    ImageView icon = taskBox.findViewById(R.id.icon);

                    String completedStat = task.isCompleted() ? "Completed" :"Pending";

                    taskTitle.setText(task.getTitle());
                    frequency.setText(task.getDate() +" • "+ completedStat);
                    description.setText(task.getDescription());
                    description.setVisibility(VISIBLE);


                    icon.setImageResource(R.drawable.task_icon);
                    tasks.addView(taskBox);

                }
                modules.addView(moduleBox);


            }
            //today focus
            View focusContainer = findViewById(R.id.dailyTaskFragment);


            List<Task> todayTasks = getTodayTasks(path);
            if(todayTasks.isEmpty()){
                View noLpath = getLayoutInflater().inflate(R.layout.empty_state, (ViewGroup) focusContainer,false);
                TextView heading = noLpath.findViewById(R.id.heading);
                heading.setText("No Task For today");
                TextView description = noLpath.findViewById(R.id.description);
                description.setText("You currently don’t have any task scheduled for today.");

                ((ViewGroup) focusContainer).addView(noLpath);

            }else{
                ((ViewGroup) focusContainer).removeAllViews();
                for(Task task: todayTasks){
                    View taskBox = getLayoutInflater().inflate(R.layout.home_task_box, (ViewGroup) focusContainer, false);
                    TextView title = taskBox.findViewById(R.id.taskTitle);
                    TextView moduleName = taskBox.findViewById(R.id.moduleName);
                    TextView  description= taskBox.findViewById(R.id.taskDesc);
                    TextView  state= taskBox.findViewById(R.id.state);
                    CheckBox check = taskBox.findViewById(R.id.checkbox);
                    ImageView icon = taskBox.findViewById(R.id.icon);

                    check.setVisibility(GONE);
                    state.setVisibility(VISIBLE);
                    if(task.isCompleted()){
                        state.setText("Completed");
                        state.setTextColor(ContextCompat.getColor(this, R.color.green));
                    }
                    title.setText(task.getTitle());
                    description.setText(task.getDescription());

                    if (task.isFrequentTask()) {
                        moduleName.setText("Frequent Task");
                        icon.setImageResource(R.drawable.loop);
                    } else {
                        moduleName.setText(task.getModuleName());
                        icon.setImageResource(R.drawable.task_icon);
                    }

                    ((ViewGroup) focusContainer).addView(taskBox);

                }
            }

        }
    }

    private void deletePath() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting learning path information...");
        progressDialog.setCancelable(false); // prevents the user from canceling
        progressDialog.show();
        deleteLocal();
        Call<ResponseDto<Object>> call = api.deleteLearningPath(); //if no parameter delete active
        call.enqueue(new Callback<ResponseDto<Object>>() {
            @Override
            public void onResponse(Call<ResponseDto<Object>> call, Response<ResponseDto<Object>> response) {
                progressDialog.dismiss();
                Log.d("API response", "Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    ResponseDto<Object> dto = response.body();
                    if(dto.meta.statusCode == 200){
                        //store token logic


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
            public void onFailure(Call<ResponseDto<Object>> call, Throwable t) {
                Log.d("API response", "Failed to reach the server " + t.getMessage());
            }

        });


    }

    private void deleteLocal() {
        Log.d("DeLETE", "deleteLocal: ");
    }

    private String getFrequency(Task task) {
        if(task.isDaily()){
            return "Everyday";
        } else if (task.isWeekly()) {
            StringBuilder value = new StringBuilder("Every ");
            boolean first = true;

            for (int dayIndex : task.getSchedule()) {
                if (!first) {
                    value.append(",");
                }
                first = false;

                switch (dayIndex) {
                    case 0: value.append(" Sunday"); break;
                    case 1: value.append(" Monday"); break;
                    case 2: value.append(" Tuesday"); break;
                    case 3: value.append(" Wednesday"); break;
                    case 4: value.append(" Thursday"); break;
                    case 5: value.append(" Friday"); break;
                    case 6: value.append(" Saturday"); break;
                }
            }
            return value.toString();
        } else {
            return "Every " + task.getFrequency()+" of the month";
        }
    }

    public LearningPath getLearningPath(){

        final LearningPath[] path = {getLocalLearningPath()};
        if(path[0] ==null){
            return null;
        }
        Call<ResponseDto<LearningPath>> call = api.getLearningPath();
        call.enqueue(new Callback<ResponseDto<LearningPath>>() {
            @Override
            public void onResponse(Call<ResponseDto<LearningPath>> call, Response<ResponseDto<LearningPath>> response) {

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