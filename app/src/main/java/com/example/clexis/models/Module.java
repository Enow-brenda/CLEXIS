package com.example.clexis.models;

import java.util.List;

// Module.java
public class Module {

    private int id;
    private String title;
    private String objective;
    private List<Task> tasks;



    public Module(String title, String objective, List<Task> tasks) {
        this.title = title;
        this.objective = objective;
        this.tasks = tasks;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getObjective() {
        return objective;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}


