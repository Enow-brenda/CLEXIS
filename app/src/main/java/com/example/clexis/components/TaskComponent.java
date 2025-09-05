package com.example.clexis.components;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.clexis.R;
import com.example.clexis.models.dto.Task;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TaskComponent {
    
    public interface OnDeleteListener {
        void onDelete();
    }
    
    public interface OnTaskChangeListener {
        void onTaskChanged(Task task);
    }
    
    private Context context;
    private View taskView;
    private Task task;
    
    private TextInputEditText etTaskTitle, etTaskDescription;
    private TextView tvTaskDate;
    private ImageButton btnDeleteTask;
    private View layoutTaskDate;
    
    private OnDeleteListener deleteListener;
    private OnTaskChangeListener taskChangeListener;
    private Calendar selectedDate;
    
    public TaskComponent(Context context) {
        this.context = context;
        this.task = new Task();
        this.task.setFrequentTask(false); // Regular module task
        initializeView();
        setupListeners();
    }
    
    public TaskComponent(Context context, Task existingTask) {
        this.context = context;
        this.task = existingTask != null ? existingTask : new Task();
        if (this.task.getId() == null) {
            this.task = new Task(); // Create new if null
        }
        this.task.setFrequentTask(false); // Regular module task
        initializeView();
        setupListeners();
        loadTaskData();
    }
    
    private void initializeView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        taskView = inflater.inflate(R.layout.component_task, null);
        
        etTaskTitle = taskView.findViewById(R.id.et_task_title);
        etTaskDescription = taskView.findViewById(R.id.et_task_description);
        tvTaskDate = taskView.findViewById(R.id.tv_task_date);
        btnDeleteTask = taskView.findViewById(R.id.btn_delete_task);
        layoutTaskDate = taskView.findViewById(R.id.layout_task_date);
    }
    
    private void setupListeners() {
        etTaskTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                task.setTitle(s.toString().trim());
                notifyTaskChanged();
            }
        });
        
        etTaskDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                task.setDescription(s.toString().trim());
                notifyTaskChanged();
            }
        });
        
        layoutTaskDate.setOnClickListener(v -> showDatePicker());
        
        btnDeleteTask.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete();
            }
        });
    }
    
    private void loadTaskData() {
        if (task != null) {
            etTaskTitle.setText(task.getTitle());
            etTaskDescription.setText(task.getDescription());
            
            if (task.getDate() != null && !task.getDate().isEmpty()) {
                tvTaskDate.setText(task.getDate());
            }
        }
    }
    
    private void showDatePicker() {
        Calendar calendar = selectedDate != null ? selectedDate : Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            context,
            (view, year, month, dayOfMonth) -> {
                selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(selectedDate.getTime());
                
                tvTaskDate.setText(formattedDate);
                task.setDate(formattedDate);
                notifyTaskChanged();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    
    private void notifyTaskChanged() {
        if (taskChangeListener != null) {
            taskChangeListener.onTaskChanged(task);
        }
    }
    
    public boolean isValid() {
        String title = etTaskTitle.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();
        
        if (title.isEmpty()) {
            etTaskTitle.setError("Task title is required");
            etTaskTitle.requestFocus();
            return false;
        }
        
        if (description.isEmpty()) {
            etTaskDescription.setError("Task description is required");
            etTaskDescription.requestFocus();
            return false;
        }
        
        if (task.getDate() == null || task.getDate().isEmpty()) {
            // Date is optional for tasks, but you can make it required if needed
            // Toast.makeText(context, "Please select a due date for the task", Toast.LENGTH_SHORT).show();
            // return false;
        }
        
        return true;
    }
    
    // Getters and Setters
    public View getView() {
        return taskView;
    }
    
    public Task getTask() {
        return task;
    }
    
    public void setOnDeleteListener(OnDeleteListener listener) {
        this.deleteListener = listener;
    }
    
    public void setOnTaskChangeListener(OnTaskChangeListener listener) {
        this.taskChangeListener = listener;
    }
}