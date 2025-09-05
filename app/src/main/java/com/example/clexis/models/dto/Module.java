package com.example.clexis.models.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// Module.java
public class Module {

    @Setter
    @Getter
    private String id ;
    private String moduleName;
    private String objective;
    private List<Task> tasks;



    public Module(String moduleName, String objective, List<Task> tasks) {
        this.moduleName = moduleName;
        this.objective = objective;
        this.tasks = tasks;
    }

    // Getters
    public String getTitle() {
        return moduleName;
    }

    public String getObjective() {
        return objective;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    // Setters
    public void setTitle(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}


