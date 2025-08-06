package com.example.clexis.models;

import com.example.clexis.GoalType;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class LearningPath {
    private String id;
    private String title;
    private GoalType goalType;
    private boolean active;

    private Date dueDate;

    private List<Module> modules;
    private List<Task> frequentTasks;

    private LocalDateTime createdAt;

    private User userId;

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    public Date getDueDate() {
        return dueDate;
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
        return createdAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public void setFrequentTasks(List<Task> frequentTasks) {
        this.frequentTasks = frequentTasks;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
