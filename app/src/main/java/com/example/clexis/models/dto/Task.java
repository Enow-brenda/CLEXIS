package com.example.clexis.models.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Task {
    private String id = UUID.randomUUID().toString();
    private String title;
    private String description;
    private boolean completed;
    private boolean frequentTask;

    private String moduleName;
    private String date;
    private boolean daily = false;
    private boolean weekly = false;
    private boolean monthly = false;
    private int frequency;
    private List<Integer> scheduledDays;

    public void setId(String id){
        this.id = id;
    }

    public boolean isCompletedToday() {
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        if (isDaily() || isWeekly() || isMonthly() || isFrequentTask()) {
            return completedByDate.getOrDefault(today, false);
        } else {
            return completed; // for one-time tasks
        }
    }
    private Map<String, Boolean> completedByDate = new HashMap<>();
    public void markCompletedToday() {
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        completedByDate.put(today, true);
        this.completed = true; // keep for one-time tasks
    }

    // ----- Getters -----
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getDate() {
        return date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isFrequentTask() {
        return frequentTask;
    }

    public boolean isDaily() {
        return daily;
    }

    public boolean isWeekly() {
        return weekly;
    }

    public boolean isMonthly() {
        return monthly;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<Integer> getSchedule() {
        return scheduledDays;
    }

    // ----- Setters -----
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setFrequentTask(boolean frequentTask) {
        this.frequentTask = frequentTask;
    }

    public void setDaily(boolean daily) {
        this.daily = daily;
    }

    public void setWeekly(boolean weekly) {
        this.weekly = weekly;
    }

    public void setMonthly(boolean monthly) {
        this.monthly = monthly;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setSchedule(List<Integer> scheduledDays) {
        this.scheduledDays = scheduledDays;
    }
}
