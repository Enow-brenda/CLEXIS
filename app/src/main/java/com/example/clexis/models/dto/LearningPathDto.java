package com.example.clexis.models.dto;

import com.example.clexis.models.enums.GoalType;

import java.util.List;

public class LearningPathDto {

    private String name;
    private String description;
    private GoalType goalType;
    private List<FrequentTaskDto> frequentTasks;
    private List<ModuleDto> modules;
    private String endDate;
    private String startDate;
}
