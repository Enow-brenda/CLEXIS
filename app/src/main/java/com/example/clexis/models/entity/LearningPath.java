package com.example.clexis.models.entity;

import com.example.clexis.models.Utils;
import com.example.clexis.models.dto.Module;
import com.example.clexis.models.dto.Task;
import com.example.clexis.models.enums.GoalType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.RealmObject;
import lombok.Data;
import lombok.Getter;

@Data
public class LearningPath extends RealmObject {

    private String id;
    private String name;
    private String description;
    private GoalType goalType;
    private List<Task> frequentTasks;
    private List<Module> modules;
    private String endDate;
    @Getter
    private String startDate;
    private boolean active = true;
    private String userId;
    private LocalDateTime creationDate;


    // Getters
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return name;
    }

    public String getGoalType() {
        return goalType == GoalType.EXAM ? "Exam" : "Skill" ;
    }

    public String getDueDate() {
        return endDate;
    }

    public boolean isActive() {
        return active;
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Task> getFrequentTasks() {
        return frequentTasks;
    }

    public LocalDateTime getCreatedAt() {
        return creationDate;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.name = title;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDueDate(String dueDate) {
        this.endDate = dueDate;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public void setFrequentTasks(List<Task> frequentTasks) {
        this.frequentTasks = frequentTasks;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.creationDate = createdAt;
    }


    public String getRemainingDays() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            Date finalD = sdf.parse(endDate);
            Date today = Calendar.getInstance().getTime();
            Utils util = new Utils();
            if(util.getDayStatus(startDate,endDate)==0){
                finalD = sdf.parse(startDate);
            }

            long diffInMillies = Math.abs(finalD.getTime() - today.getTime());
            long days = diffInMillies / (1000 * 60 * 60 * 24); // convert ms → days
            return String.valueOf(days) + " Days";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LearningPath getDefault(){
        Task task1 = new Task();
        task1.setTitle("Read Chapter 1");
        task1.setDescription("Introduction to Java");
        task1.setModuleName("Java Basics");
        task1.setDate("22-08-2025");

        Task task2 = new Task();
        task2.setTitle("Practice OOP");
        task2.setDescription("Work on classes and objects");
        task2.setModuleName("Java Basics");
        task2.setDate("23-08-2025");




        List<Task> module1Tasks = Arrays.asList(task1, task2);

        // Create a module
        Module module1 = new Module("Java Basics", "Learn Java fundamentals", module1Tasks);

        // Daily frequent task
        Task dailyTask = new Task();
        dailyTask.setTitle("Read Chapter 1");
        dailyTask.setDescription("Introduction to Java");
        dailyTask.setDaily(true);
        dailyTask.setFrequentTask(true);

// Weekly frequent task (Mon, Wed, Fri)
        Task weeklyTask = new Task();
        weeklyTask.setTitle("Practice OOP");
        weeklyTask.setDescription("Work on classes and objects");
        weeklyTask.setWeekly(true);
        weeklyTask.setFrequentTask(true);
        weeklyTask.setSchedule(Arrays.asList(1, 3, 5)); // 1=Monday, 3=Wednesday, 5=Friday

// Monthly frequent task (on 15th of every month)
        Task monthlyTask = new Task();
        monthlyTask.setTitle("Review Java Basics");
        monthlyTask.setDescription("Monthly review of core concepts");
        monthlyTask.setMonthly(true);
        monthlyTask.setFrequency(22);
        monthlyTask.setFrequentTask(true);

        // Create a LearningPath
        LearningPath path = new LearningPath();
        path.setName("Java Mastery");
        path.setDescription("A path to master core Java and OOP concepts");
        path.setGoalType(GoalType.SKill); // assuming GoalType is an enum
        path.setFrequentTasks(List.of(weeklyTask,monthlyTask,dailyTask)); // mark task1 as frequent
        path.setModules(List.of(module1));
        path.setStartDate("22-08-2025");
        path.setEndDate("22-09-2025");
        path.setActive(true);
        path.setUserId("user_123");
        return path;
    }
}
