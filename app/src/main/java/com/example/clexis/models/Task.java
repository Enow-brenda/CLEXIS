package com.example.clexis.models;

import java.util.List;

// Task.java
public class Task {
    private String title;
    private List<String> schedule; //can be null
    private String date; // Can be null
    private boolean isCompleted;

    public Task(String title, List<String> schedule, String date, boolean isCompleted) {
        this.title = title;
        this.schedule = schedule;
        this.date = date;
        this.isCompleted = isCompleted;
    }

    // Getter and Setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and Setter for schedule
    public List<String> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<String> schedule) {
        this.schedule = schedule;
    }

    // Getter and Setter for date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Getter and Setter for isCompleted
    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}

