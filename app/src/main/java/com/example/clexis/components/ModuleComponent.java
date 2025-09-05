package com.example.clexis.components;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.clexis.R;
import com.example.clexis.models.dto.Module;
import com.example.clexis.models.dto.Task;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class ModuleComponent {
    
    public interface OnDeleteListener {
        void onDelete();
    }
    
    public interface OnModuleChangeListener {
        void onModuleChanged(Module module);
    }
    
    private Context context;
    private View moduleView;
    private Module module;
    private List<TaskComponent> taskComponents = new ArrayList<>();
    
    private TextInputEditText etModuleName, etObjective;
    private LinearLayout tasksContainer;
    private View tasksEmpty;
    private ImageButton btnDeleteModule, btnAddTask;
    
    private OnDeleteListener deleteListener;
    private OnModuleChangeListener moduleChangeListener;
    
    public ModuleComponent(Context context) {
        this.context = context;
        this.module = new Module("", "", new ArrayList<>());
        initializeView();
        setupListeners();
    }
    
    public ModuleComponent(Context context, Module existingModule) {
        this.context = context;
        this.module = existingModule != null ? existingModule : new Module("", "", new ArrayList<>());
        initializeView();
        setupListeners();
        loadModuleData();
    }
    
    private void initializeView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        moduleView = inflater.inflate(R.layout.component_module, null);
        
        etModuleName = moduleView.findViewById(R.id.et_module_name);
        etObjective = moduleView.findViewById(R.id.et_objective);
        tasksContainer = moduleView.findViewById(R.id.tasks_container);
        tasksEmpty = moduleView.findViewById(R.id.tasks_empty);
        btnDeleteModule = moduleView.findViewById(R.id.btn_delete_module);
        btnAddTask = moduleView.findViewById(R.id.btn_add_task);
        
        setupEmptyTasksState();
        updateTasksEmptyState();
    }
    
    private void setupEmptyTasksState() {
        TextView heading = tasksEmpty.findViewById(R.id.heading);
        TextView description = tasksEmpty.findViewById(R.id.description);
        heading.setText("No Tasks Added");
        description.setText("Add tasks to define what needs to be accomplished in this module");
    }
    
    private void setupListeners() {
        etModuleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                module.setTitle(s.toString().trim());
                notifyModuleChanged();
            }
        });
        
        etObjective.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                module.setObjective(s.toString().trim());
                notifyModuleChanged();
            }
        });
        
        btnDeleteModule.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete();
            }
        });
        
        btnAddTask.setOnClickListener(v -> addTaskComponent());
    }
    
    private void loadModuleData() {
        if (module != null) {
            etModuleName.setText(module.getTitle());
            etObjective.setText(module.getObjective());
            
            // Load existing tasks
            if (module.getTasks() != null) {
                for (Task task : module.getTasks()) {
                    addTaskComponent(task);
                }
            }
        }
    }
    
    private void addTaskComponent() {
        addTaskComponent(null);
    }

    private void addTaskComponent(Task existingTask) {
        TaskComponent taskComponent = existingTask != null ?
                new TaskComponent(context, existingTask) :
                new TaskComponent(context);

        taskComponent.setOnDeleteListener(() -> {
            tasksContainer.removeView(taskComponent.getView());
            taskComponents.remove(taskComponent);

            // Remove from module tasks by ID
            String taskIdToRemove = taskComponent.getTask().getId();
            module.getTasks().removeIf(t -> t.getId().equals(taskIdToRemove));

            updateTasksEmptyState();
            notifyModuleChanged();
        });


        taskComponent.setOnTaskChangeListener(task -> {
            // Update existing task in module by ID
            for (int i = 0; i < module.getTasks().size(); i++) {
                if (module.getTasks().get(i).getId().equals(task.getId())) {
                    module.getTasks().set(i, task);
                    break;
                }
            }
            notifyModuleChanged();
        });

        tasksContainer.addView(taskComponent.getView());
        taskComponents.add(taskComponent);

        // Only add new tasks
        if (existingTask == null) {
            module.getTasks().add(taskComponent.getTask());
        }

        updateTasksEmptyState();
        notifyModuleChanged();
    }


    private void updateTasksEmptyState() {
        tasksEmpty.setVisibility(taskComponents.isEmpty() ? View.VISIBLE : View.GONE);
    }
    
    private void notifyModuleChanged() {
        if (moduleChangeListener != null) {
            moduleChangeListener.onModuleChanged(module);
        }
    }
    
    public boolean isValid() {
        String moduleName = etModuleName.getText().toString().trim();
        String objective = etObjective.getText().toString().trim();
        
        if (moduleName.isEmpty()) {
            etModuleName.setError("Module name is required");
            return false;
        }
        
        if (objective.isEmpty()) {
            etObjective.setError("Module objective is required");
            return false;
        }
        
        if (taskComponents.isEmpty()) {
            Toast.makeText(context, "Module must have at least one task", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Validate all tasks
        for (TaskComponent taskComponent : taskComponents) {
            if (!taskComponent.isValid()) {
                return false;
            }
        }
        
        return true;
    }
    
    // Getters and Setters
    public View getView() {
        return moduleView;
    }
    
    public Module getModule() {
        return module;
    }
    
    public void setOnDeleteListener(OnDeleteListener listener) {
        this.deleteListener = listener;
    }
    
    public void setOnModuleChangeListener(OnModuleChangeListener listener) {
        this.moduleChangeListener = listener;
    }
}