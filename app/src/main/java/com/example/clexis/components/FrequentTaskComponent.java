package com.example.clexis.components;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.clexis.R;
import com.example.clexis.models.dto.Task;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class FrequentTaskComponent {

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
    private RadioGroup radioFrequencyType;
    private LinearLayout layoutDaysSelection, layoutFrequencyCustom;
    private TextView tvFrequencyValue;
    private SeekBar seekBarFrequency;
    private ImageButton btnDeleteTask;

    // Day selection checkboxes
    private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday,
            cbFriday, cbSaturday, cbSunday;

    private OnDeleteListener deleteListener;
    private OnTaskChangeListener taskChangeListener;

    public FrequentTaskComponent(Context context) {
        this.context = context;
        this.task = new Task();
        this.task.setFrequentTask(true);
        this.task.setSchedule(new ArrayList<>());
        initializeView();
        setupListeners();
    }

    public FrequentTaskComponent(Context context, Task existingTask) {
        this.context = context;
        this.task = existingTask != null ? existingTask : new Task();
        if (this.task.getId() == null) {
            this.task = new Task();
        }
        this.task.setFrequentTask(true);
        if (this.task.getSchedule() == null) {
            this.task.setSchedule(new ArrayList<>());
        }
        initializeView();
        setupListeners();
        loadTaskData();
    }

    private void initializeView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        taskView = inflater.inflate(R.layout.component_frequent_task, null);

        etTaskTitle = taskView.findViewById(R.id.et_frequent_task_title);
        etTaskDescription = taskView.findViewById(R.id.et_frequent_task_description);
        radioFrequencyType = taskView.findViewById(R.id.radio_frequency_type);
        layoutDaysSelection = taskView.findViewById(R.id.layout_days_selection);
        layoutFrequencyCustom = taskView.findViewById(R.id.layout_frequency_custom);
        tvFrequencyValue = taskView.findViewById(R.id.tv_frequency_value);
        seekBarFrequency = taskView.findViewById(R.id.seekbar_frequency);
        btnDeleteTask = taskView.findViewById(R.id.btn_delete_frequent_task);

        // Day checkboxes
        cbMonday = taskView.findViewById(R.id.cb_monday);
        cbTuesday = taskView.findViewById(R.id.cb_tuesday);
        cbWednesday = taskView.findViewById(R.id.cb_wednesday);
        cbThursday = taskView.findViewById(R.id.cb_thursday);
        cbFriday = taskView.findViewById(R.id.cb_friday);
        cbSaturday = taskView.findViewById(R.id.cb_saturday);
        cbSunday = taskView.findViewById(R.id.cb_sunday);

        // Setup initial states
        layoutDaysSelection.setVisibility(View.GONE);
        layoutFrequencyCustom.setVisibility(View.GONE);
        seekBarFrequency.setMax(30); // Max 30 days
        seekBarFrequency.setProgress(1);
        tvFrequencyValue.setText("1 day");
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

        radioFrequencyType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_daily) {
                task.setDaily(true);
                task.setWeekly(false);
                task.setMonthly(false);
                layoutDaysSelection.setVisibility(View.GONE);
                layoutFrequencyCustom.setVisibility(View.GONE);
            } else if (checkedId == R.id.radio_weekly) {
                task.setDaily(false);
                task.setWeekly(true);
                task.setMonthly(false);
                layoutDaysSelection.setVisibility(View.VISIBLE);
                layoutFrequencyCustom.setVisibility(View.GONE);
            } else if (checkedId == R.id.radio_monthly) {
                task.setDaily(false);
                task.setWeekly(false);
                task.setMonthly(true);
                layoutDaysSelection.setVisibility(View.GONE);
                layoutFrequencyCustom.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.radio_custom) {
                task.setDaily(false);
                task.setWeekly(false);
                task.setMonthly(false);
                layoutDaysSelection.setVisibility(View.GONE);
                layoutFrequencyCustom.setVisibility(View.VISIBLE);
            }
            notifyTaskChanged();
        });

        // Day selection listeners
        CompoundButton.OnCheckedChangeListener dayChangeListener = (buttonView, isChecked) -> {
            updateSelectedDays();
            notifyTaskChanged();
        };

        cbMonday.setOnCheckedChangeListener(dayChangeListener);
        cbTuesday.setOnCheckedChangeListener(dayChangeListener);
        cbWednesday.setOnCheckedChangeListener(dayChangeListener);
        cbThursday.setOnCheckedChangeListener(dayChangeListener);
        cbFriday.setOnCheckedChangeListener(dayChangeListener);
        cbSaturday.setOnCheckedChangeListener(dayChangeListener);
        cbSunday.setOnCheckedChangeListener(dayChangeListener);

        // Frequency slider listener
        seekBarFrequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) progress = 1;
                task.setFrequency(progress);
                String text = progress == 1 ? "1 day" : progress + " days";
                tvFrequencyValue.setText(text);
                if (fromUser) notifyTaskChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

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

            // Set frequency type and show appropriate layouts
            if (task.isDaily()) {
                radioFrequencyType.check(R.id.radio_daily);
                layoutDaysSelection.setVisibility(View.GONE);
                layoutFrequencyCustom.setVisibility(View.GONE);
            } else if (task.isWeekly()) {
                radioFrequencyType.check(R.id.radio_weekly);
                layoutDaysSelection.setVisibility(View.VISIBLE);
                layoutFrequencyCustom.setVisibility(View.GONE);
                loadSelectedDays();
            } else if (task.isMonthly()) {
                radioFrequencyType.check(R.id.radio_monthly);
                layoutDaysSelection.setVisibility(View.GONE);
                layoutFrequencyCustom.setVisibility(View.VISIBLE);
            } else if (task.getFrequency() > 0) {
                radioFrequencyType.check(R.id.radio_monthly);
                layoutDaysSelection.setVisibility(View.GONE);
                layoutFrequencyCustom.setVisibility(View.VISIBLE);
                seekBarFrequency.setProgress(task.getFrequency());
                String text = task.getFrequency() == 1 ? "Day 1" : "Day "+task.getFrequency();
                tvFrequencyValue.setText(text);
            } else {
                // Default to daily if nothing is set
                radioFrequencyType.check(R.id.radio_daily);
                task.setDaily(true);
                layoutDaysSelection.setVisibility(View.GONE);
                layoutFrequencyCustom.setVisibility(View.GONE);
            }
        }
    }

    private void loadSelectedDays() {
        if (task.getSchedule() != null) {
            List<Integer> selectedDays = task.getSchedule();
            cbMonday.setChecked(selectedDays.contains(1));
            cbTuesday.setChecked(selectedDays.contains(2));
            cbWednesday.setChecked(selectedDays.contains(3));
            cbThursday.setChecked(selectedDays.contains(4));
            cbFriday.setChecked(selectedDays.contains(5));
            cbSaturday.setChecked(selectedDays.contains(6));
            cbSunday.setChecked(selectedDays.contains(7));
        }
    }

    private void updateSelectedDays() {
        List<Integer> selectedDays = new ArrayList<>();

        if (cbMonday.isChecked()) selectedDays.add(1);
        if (cbTuesday.isChecked()) selectedDays.add(2);
        if (cbWednesday.isChecked()) selectedDays.add(3);
        if (cbThursday.isChecked()) selectedDays.add(4);
        if (cbFriday.isChecked()) selectedDays.add(5);
        if (cbSaturday.isChecked()) selectedDays.add(6);
        if (cbSunday.isChecked()) selectedDays.add(7);

        task.setSchedule(selectedDays);
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

        // Validate frequency settings
        if (task.isWeekly() && (task.getSchedule() == null || task.getSchedule().isEmpty())) {
            // Show error for weekly tasks without selected days
            return false;
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