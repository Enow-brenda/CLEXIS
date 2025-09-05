package com.example.clexis.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.clexis.R;
import com.example.clexis.components.FrequentTaskComponent;
import com.example.clexis.components.ModuleComponent;
import com.example.clexis.models.dto.Module;
import com.example.clexis.models.dto.Task;
import com.example.clexis.models.entity.LearningPath;
import com.example.clexis.models.entity.Resource;
import com.example.clexis.repository.LearningPathRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class AddLearningPathActivity extends AppCompatActivity {

    private static final String ACTION_ADD = "create";
    private static final String ACTION_EDIT = "edit";

    private TextInputEditText etTitle, etDescription;
    private TextView tvStartDate, tvDueDate;
    private LinearLayout modulesContainer, fTasksContainer;
    private View modulesEmpty, fTaskEmpty;
    private Button btnCreatePath, btnCancel;

    private List<Module> modules = new ArrayList<>();
    private List<Task> frequentTasks = new ArrayList<>();
    private String currentAction = ACTION_ADD;

    private LearningPath path;
    private Calendar startDate, dueDate;

    private LearningPathRepository learningPathRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_learning_path);

        learningPathRepository = new LearningPathRepository(this);

        setupStatusBar();
        initializeViews();
        setupClickListeners();
        handleIntentData();
    }


    private void setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }
    }

    private void initializeViews() {
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvDueDate = findViewById(R.id.tv_due_date);
        modulesContainer = findViewById(R.id.modules);
        fTasksContainer = findViewById(R.id.fTasks);
        modulesEmpty = findViewById(R.id.modulesEmpty);
        fTaskEmpty = findViewById(R.id.fTaskEmpty);
        btnCreatePath = findViewById(R.id.btn_create_path);
        btnCancel = findViewById(R.id.btn_cancel);

        setupEmptyStates();
    }

    private void setupEmptyStates() {
        // Setup modules empty state
        TextView moduleHeading = modulesEmpty.findViewById(R.id.heading);
        TextView moduleDesc = modulesEmpty.findViewById(R.id.description);
        ImageView moduleIcon = modulesEmpty.findViewById(R.id.icon);
        moduleHeading.setText("No Modules Added");
        moduleDesc.setText("Add learning modules to organize your content and track progress effectively");
        moduleIcon.setImageResource(R.drawable.books);

        // Setup frequent tasks empty state
        TextView fTaskHeading = fTaskEmpty.findViewById(R.id.heading);
        TextView fTaskDesc = fTaskEmpty.findViewById(R.id.description);
        ImageView fTaskIcon = fTaskEmpty.findViewById(R.id.icon);
        fTaskHeading.setText("No Task Added");
        fTaskDesc.setText("Add recurring activities like daily reading, practice sessions, or weekly reviews");
        fTaskIcon.setImageResource(R.drawable.loop);

        updateEmptyStates();
    }

    private void setupClickListeners() {
        Button generate = findViewById(R.id.generate);
        generate.setOnClickListener(v -> {
            Intent intent = new Intent(this, GenerateActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.addModule).setOnClickListener(v -> addModuleComponent());
        findViewById(R.id.addFrequent).setOnClickListener(v -> addFrequentTaskComponent());

        findViewById(R.id.layout_start_date).setOnClickListener(v -> showDatePicker(true));
        findViewById(R.id.layout_due_date).setOnClickListener(v -> showDatePicker(false));

        btnCreatePath.setOnClickListener(v -> validateAndSave());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        currentAction = intent.getStringExtra("action");


        if (ACTION_EDIT.equals(currentAction)) {
            btnCreatePath.setText("Update Learning Path");
            // Load existing data if editing
            loadExistingData(intent);
        }
    }

    private void loadExistingData(Intent intent) {
        // Load existing learning path data
        String json = intent.getStringExtra("path");
        path = new Gson().fromJson(json, LearningPath.class);

        etTitle.setText(path.getTitle());
        etDescription.setText(path.getDescription());
        tvStartDate.setText(path.getStartDate());
        tvDueDate.setText(path.getDueDate());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            if (path.getStartDate() != null) {
                startDate = Calendar.getInstance();
                startDate.setTime(dateFormat.parse(path.getStartDate()));
                tvStartDate.setText(path.getStartDate());
            }

            if (path.getDueDate() != null) {
                dueDate = Calendar.getInstance();
                dueDate.setTime(dateFormat.parse(path.getDueDate()));
                tvDueDate.setText(path.getDueDate());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // Deduplicate modules by id (or moduleName if no id)
        List<Module> uniqueModules = new ArrayList<>();
        Set<String> moduleIds = new HashSet<>();
        if (path.getModules() != null) {
            for (Module module : path.getModules()) {
                if (module.getId() == null) module.setId(UUID.randomUUID().toString());
                if (!moduleIds.contains(module.getId())) {
                    // Deduplicate tasks inside this module
                    List<Task> uniqueTasks = new ArrayList<>();
                    Set<String> taskIds = new HashSet<>();
                    if (module.getTasks() != null) {
                        for (Task task : module.getTasks()) {
                            if (task.getId() == null) task.setId(UUID.randomUUID().toString());
                            if (!taskIds.contains(task.getId())) {
                                uniqueTasks.add(task);
                                taskIds.add(task.getId());
                            }
                        }
                    }
                    module.setTasks(uniqueTasks);

                    uniqueModules.add(module);
                    moduleIds.add(module.getId());
                }
            }
        }
        path.setModules(uniqueModules);

        // Load modules into UI
        modules.clear();
        modulesContainer.removeAllViews();
        for (Module module : path.getModules()) {
            ModuleComponent moduleComponent = new ModuleComponent(this, module);
            moduleComponent.setOnDeleteListener(() -> {
                modulesContainer.removeView(moduleComponent.getView());
                modules.remove(moduleComponent.getModule());
                updateEmptyStates();
            });
            modulesContainer.addView(moduleComponent.getView());
            modules.add(moduleComponent.getModule());
        }

        // Deduplicate frequent tasks by id
        List<Task> uniqueTasks = new ArrayList<>();
        Set<String> taskIds = new HashSet<>();
        if (path.getFrequentTasks() != null) {
            for (Task task : path.getFrequentTasks()) {
                if (task.getId() == null) task.setId(UUID.randomUUID().toString());
                if (!taskIds.contains(task.getId())) {
                    uniqueTasks.add(task);
                    taskIds.add(task.getId());
                }
            }
        }
        path.setFrequentTasks(uniqueTasks);

        // Load frequent tasks into UI
        frequentTasks.clear();
        fTasksContainer.removeAllViews();
        for (Task task : path.getFrequentTasks()) {
            FrequentTaskComponent taskComponent = new FrequentTaskComponent(this, task);
            taskComponent.setOnDeleteListener(() -> {
                fTasksContainer.removeView(taskComponent.getView());
                frequentTasks.remove(taskComponent.getTask());
                updateEmptyStates();
            });
            fTasksContainer.addView(taskComponent.getView());
            frequentTasks.add(taskComponent.getTask());
        }

        updateEmptyStates();
    }


    private void addModuleComponent() {
        ModuleComponent moduleComponent = new ModuleComponent(this);
        moduleComponent.setOnDeleteListener(() -> {
            modulesContainer.removeView(moduleComponent.getView());
            modules.remove(moduleComponent.getModule());
            updateEmptyStates();
        });

        moduleComponent.setOnModuleChangeListener(module -> updateEmptyStates());

        modulesContainer.addView(moduleComponent.getView());
        modules.add(moduleComponent.getModule());
        updateEmptyStates();
    }

    private void addFrequentTaskComponent() {
        FrequentTaskComponent taskComponent = new FrequentTaskComponent(this);
        taskComponent.setOnDeleteListener(() -> {
            fTasksContainer.removeView(taskComponent.getView());
            frequentTasks.remove(taskComponent.getTask());
            updateEmptyStates();
        });

        taskComponent.setOnTaskChangeListener(task -> updateEmptyStates());

        fTasksContainer.addView(taskComponent.getView());
        frequentTasks.add(taskComponent.getTask());
        updateEmptyStates();
    }

    private void updateEmptyStates() {
        modulesEmpty.setVisibility(modules.isEmpty() ? View.VISIBLE : View.GONE);
        fTaskEmpty.setVisibility(frequentTasks.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(selectedDate.getTime());

                    if (isStartDate) {
                        startDate = selectedDate;
                        tvStartDate.setText(formattedDate);
                    } else {
                        dueDate = selectedDate;
                        tvDueDate.setText(formattedDate);
                    }

                    // Validate dates
                    validateDates();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        if (isStartDate) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        } else if (startDate != null) {
            datePickerDialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
        }

        datePickerDialog.show();
    }

    private void validateDates() {
        if (startDate != null && dueDate != null && startDate.after(dueDate)) {
            Toast.makeText(this, "Due date must be after start date", Toast.LENGTH_SHORT).show();
            dueDate = null;
            tvDueDate.setText("Select Date");
        }
    }

    private void validateAndSave() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Validate required fields
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        if (startDate == null) {
            Toast.makeText(this, "Please select a start date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dueDate == null) {
            Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate modules
        if (modules.isEmpty()) {
            Toast.makeText(this, "Please add at least one module", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if modules have tasks
        boolean hasValidModule = false;
        for (Module module : modules) {
            if (module.getTasks() != null && !module.getTasks().isEmpty()) {
                hasValidModule = true;
                break;
            }
        }

        if (!hasValidModule) {
            Toast.makeText(this, "Each module must have at least one task", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save or update learning path

        if(path==null){
            path = new LearningPath();
            path.setId(UUID.randomUUID().toString());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedStart = dateFormat.format(startDate.getTime());
        String formattedDue = dateFormat.format(dueDate.getTime());

        path.setTitle(title);
        path.setDescription(description);
        path.setStartDate(formattedStart);
        path.setDueDate(formattedDue);
        path.setModules(modules);
        path.setFrequentTasks(frequentTasks);

        // Save or update learning path
        saveLearningPath(path);
    }

    private void saveLearningPath(LearningPath learningPath) {
        // Create learning path object with all data
        // This would involve saving to database or passing back to calling activity


        // Add modules and tasks data as needed
        if(currentAction.equals(ACTION_EDIT)){
            learningPathRepository.update(path.getId(),learningPath);
            Toast.makeText(this,"Learning path updated successfully" , Toast.LENGTH_SHORT).show();
        }
        else{
            learningPath.setActive(true);
            learningPathRepository.add(learningPath);

            Toast.makeText(this,"Learning path added successfully" , Toast.LENGTH_SHORT).show();
        }



        startActivity(new Intent(this, ViewLearningPlanActivity.class));
        finish();
    }
}