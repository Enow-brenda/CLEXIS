package com.example.clexis.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.clexis.R;
import com.example.clexis.models.ApiClient;
import com.example.clexis.models.ApiService;
import com.example.clexis.models.dto.Module;
import com.example.clexis.models.dto.Task;
import com.example.clexis.models.entity.LearningPath;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    private List<Task> tasks;

    private ApiService api;

    private LearningPath path;

    private Calendar today;
    private Calendar currentWeekStart;
    private Calendar selectedDay ;

    private TextView currentWeek;
    private TextView current;

    private ImageButton btn_previous_week;
    private ImageButton btn_next_week;
    private TextView dayString;

    private LinearLayout layout_monday;
    private LinearLayout layout_tuesday;
    private LinearLayout layout_wednesday;
    private LinearLayout layout_thursday;
    private LinearLayout layout_friday;
    private LinearLayout layout_saturday;
    private LinearLayout layout_sunday;

    private LinearLayout scheduleContainer;

    private List<Task> allTasks = new ArrayList<>();
    private TextView tv_mon_date, tv_tue_date, tv_wed_date, tv_thu_date, tv_fri_date, tv_sat_date, tv_sun_date;

    private int dayIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);

        api = ApiClient.getRetrofitInstance(this).create(ApiService.class);

        today = Calendar.getInstance(); // today
        currentWeekStart = (Calendar) today.clone();
        currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Monday of current week

        selectedDay = (Calendar) today.clone(); // default selected day = today

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.WHITE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }
        currentWeek = findViewById(R.id.tv_current_week);
        current = findViewById(R.id.current);

        btn_previous_week = findViewById(R.id.btn_previous_week);
        btn_next_week = findViewById(R.id.btn_next_week);


        layout_monday = findViewById(R.id.layout_monday);
        layout_tuesday = findViewById(R.id.layout_tuesday);
        layout_wednesday = findViewById(R.id.layout_wednesday);
        layout_thursday = findViewById(R.id.layout_thursday);
        layout_friday = findViewById(R.id.layout_friday);
        layout_saturday = findViewById(R.id.layout_saturday);
        layout_sunday = findViewById(R.id.layout_sunday);

        tv_mon_date = findViewById(R.id.tv_mon_date);
        tv_tue_date = findViewById(R.id.tv_tue_date);
        tv_wed_date = findViewById(R.id.tv_wed_date);
        tv_thu_date = findViewById(R.id.tv_thu_date);
        tv_fri_date = findViewById(R.id.tv_fri_date);
        tv_sat_date = findViewById(R.id.tv_sat_date);
        tv_sun_date = findViewById(R.id.tv_sun_date);

        scheduleContainer = findViewById(R.id.scheduleContainer);
        dayString = findViewById(R.id.selectedDay);



        setupActivity();
    }

    private void setupActivity() {
        getTasks();
       updateWeekLabel();
       showTasksForSelectedDay(selectedDay);
        highlightSelectedDay(selectedDay);

        btn_previous_week.setOnClickListener(v -> {
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, -1);
            updateWeekLabel();

            selectedDay = (Calendar) currentWeekStart.clone();
            selectedDay.add(Calendar.DAY_OF_MONTH, dayIndex); // keep same weekday
            highlightSelectedDay(selectedDay);
            showTasksForSelectedDay(selectedDay);
        });

        btn_next_week.setOnClickListener(v -> {
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, 1);
            updateWeekLabel();

            selectedDay = (Calendar) currentWeekStart.clone();
            selectedDay.add(Calendar.DAY_OF_MONTH, dayIndex); // keep same weekday
            highlightSelectedDay(selectedDay);
            showTasksForSelectedDay(selectedDay);
        });

        layout_monday.setOnClickListener(v -> {
            selectedDay = (Calendar) currentWeekStart.clone();
            highlightSelectedDay(selectedDay);
            showTasksForSelectedDay(selectedDay);
        });

        layout_tuesday.setOnClickListener(v -> {
            selectedDay = (Calendar) currentWeekStart.clone();
            selectedDay.add(Calendar.DAY_OF_MONTH, 1);
            highlightSelectedDay(selectedDay);
            showTasksForSelectedDay(selectedDay);
        });
        layout_wednesday.setOnClickListener(v -> {
            selectedDay = (Calendar) currentWeekStart.clone();
            selectedDay.add(Calendar.DAY_OF_MONTH, 2);
            highlightSelectedDay(selectedDay);
            showTasksForSelectedDay(selectedDay);
        });
        layout_thursday.setOnClickListener(v -> {
            selectedDay = (Calendar) currentWeekStart.clone();
            selectedDay.add(Calendar.DAY_OF_MONTH, 3);
            highlightSelectedDay(selectedDay);
            showTasksForSelectedDay(selectedDay);
        });
        layout_friday.setOnClickListener(v -> {
            selectedDay = (Calendar) currentWeekStart.clone();
            selectedDay.add(Calendar.DAY_OF_MONTH, 4);
            highlightSelectedDay(selectedDay);
            showTasksForSelectedDay(selectedDay);
        });
        layout_saturday.setOnClickListener(v -> {
            selectedDay = (Calendar) currentWeekStart.clone();
            selectedDay.add(Calendar.DAY_OF_MONTH, 5);
            highlightSelectedDay(selectedDay);
            showTasksForSelectedDay(selectedDay);
        });
        layout_sunday.setOnClickListener(v -> {
            selectedDay = (Calendar) currentWeekStart.clone();
            selectedDay.add(Calendar.DAY_OF_MONTH, 6);
            highlightSelectedDay(selectedDay);
            showTasksForSelectedDay(selectedDay);
        });

    }

    private void updateWeekLabel() {
        Calendar weekEnd = (Calendar) currentWeekStart.clone();
        weekEnd.add(Calendar.DAY_OF_MONTH, 6); // Sunday

        Calendar todayWeek = Calendar.getInstance();

        int currentWeekNumber = currentWeekStart.get(Calendar.WEEK_OF_YEAR);
        int todayWeekNumber = todayWeek.get(Calendar.WEEK_OF_YEAR);

        int currentYear = currentWeekStart.get(Calendar.YEAR);
        int todayYear = todayWeek.get(Calendar.YEAR);

        if (currentWeekNumber == todayWeekNumber && currentYear == todayYear) {
            current.setVisibility(View.VISIBLE);
        } else {
            current.setVisibility(View.GONE);
        }


        if(currentWeekStart == todayWeek){
            current.setVisibility(View.VISIBLE);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String weekText = "Week of " + sdf.format(currentWeekStart.getTime());
        currentWeek.setText(weekText);


        Calendar cal = (Calendar) currentWeekStart.clone();
        SimpleDateFormat dayNumberFormat = new SimpleDateFormat("d", Locale.getDefault());

        tv_mon_date.setText(dayNumberFormat.format(cal.getTime())); // Monday
        cal.add(Calendar.DAY_OF_MONTH, 1);
        tv_tue_date.setText(dayNumberFormat.format(cal.getTime())); // Tuesday
        cal.add(Calendar.DAY_OF_MONTH, 1);
        tv_wed_date.setText(dayNumberFormat.format(cal.getTime())); // Wednesday
        cal.add(Calendar.DAY_OF_MONTH, 1);
        tv_thu_date.setText(dayNumberFormat.format(cal.getTime())); // Thursday
        cal.add(Calendar.DAY_OF_MONTH, 1);
        tv_fri_date.setText(dayNumberFormat.format(cal.getTime())); // Friday
        cal.add(Calendar.DAY_OF_MONTH, 1);
        tv_sat_date.setText(dayNumberFormat.format(cal.getTime())); // Saturday
        cal.add(Calendar.DAY_OF_MONTH, 1);
        tv_sun_date.setText(dayNumberFormat.format(cal.getTime())); // Sunday
    }

    private void showTasksForSelectedDay(Calendar date) {
        scheduleContainer.removeAllViews(); // LinearLayout inside ScrollView

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String selectedDateStr = sdf.format(date.getTime());
        dayString.setText(sdf2.format(date.getTime()));
        try {
            Date sDate= sdf.parse(selectedDateStr); // task.getDate() is a String
            Calendar cal = Calendar.getInstance();
            cal.setTime(sDate);

            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1–7
            int dayOfWeekIndex = dayOfWeek - 1; // 0–6

            dayIndex = (dayOfWeek + 5) % 7;

            int count  = 0;

            for (Task task : allTasks) {
                boolean isDate = false;
                if(task.getDate()!=null && task.getDate().equals(selectedDateStr)){
                    isDate = true;
                }else if(task.isFrequentTask()){
                    if(task.isMonthly() && task.getFrequency()==dayOfMonth){
                        isDate = true;
                    }
                    else if(task.isDaily()){
                        isDate = true;
                    } else if (task.isWeekly() && task.getSchedule().contains(dayOfWeekIndex)) {
                        isDate = true;
                    }
                }
                if (isDate) {
                    View taskView = getLayoutInflater().inflate(R.layout.schedule_task_box, scheduleContainer, false);
                    TextView tvModule = taskView.findViewById(R.id.moduleName);
                    TextView tvTitle = taskView.findViewById(R.id.title);
                    TextView tvDesc = taskView.findViewById(R.id.description);
                    tvModule.setText(task.getModuleName());
                    tvTitle.setText(task.getTitle());
                    tvDesc.setText(task.getDescription());
                    if(task.isCompleted()){
                        View divider = taskView.findViewById(R.id.divider);
                        divider.setBackgroundColor(getResources().getColor(R.color.green));
                    }
                    scheduleContainer.addView(taskView);
                    count++;
                }
            }
            if(count==0){
                View empty = getLayoutInflater().inflate(R.layout.empty_state, scheduleContainer, false);
                TextView heading = empty.findViewById(R.id.heading);
                heading.setText("No Task Scheduled");
                TextView description = empty.findViewById(R.id.description);
                description.setText("You currently don’t have any tasks scheduled for this day. Add a task to keep your schedule on track.");
                scheduleContainer.addView(empty);

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void highlightSelectedDay(Calendar date) {
        // Reset all day layouts
        layout_monday.setBackground(null);
        layout_tuesday.setBackground(null);
        layout_wednesday.setBackground(null);
        layout_thursday.setBackground(null);
        layout_friday.setBackground(null);
        layout_saturday.setBackground(null);
        layout_sunday.setBackground(null);

        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        switch(dayOfWeek) {
            case Calendar.MONDAY: layout_monday.setBackground(ContextCompat.getDrawable(this, R.drawable.mcq_active)); break;
            case Calendar.TUESDAY: layout_tuesday.setBackground(ContextCompat.getDrawable(this, R.drawable.mcq_active)); break;
            case Calendar.WEDNESDAY: layout_wednesday.setBackground(ContextCompat.getDrawable(this, R.drawable.mcq_active)); break;
            case Calendar.THURSDAY: layout_thursday.setBackground(ContextCompat.getDrawable(this, R.drawable.mcq_active)); break;
            case Calendar.FRIDAY: layout_friday.setBackground(ContextCompat.getDrawable(this, R.drawable.mcq_active)); break;
            case Calendar.SATURDAY: layout_saturday.setBackground(ContextCompat.getDrawable(this, R.drawable.mcq_active)); break;
            case Calendar.SUNDAY: layout_sunday.setBackground(ContextCompat.getDrawable(this, R.drawable.mcq_active)); break;
        }
    }


    public void getTasks(){
        path = new LearningPath();
        path = path.getDefault();

        allTasks = new ArrayList<>(path.getFrequentTasks());

        for(Task task:allTasks){
            task.setModuleName("Frequent Task");
        }

        for(Module module: path.getModules()){
            module.getTasks().forEach(task -> task.setModuleName(module.getTitle()));
            allTasks.addAll(module.getTasks()); // now safe
        }

    }
}
