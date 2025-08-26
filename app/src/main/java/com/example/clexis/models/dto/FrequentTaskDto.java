package com.example.clexis.models.dto;


import java.util.List;


public class FrequentTaskDto {
    private String title;
    private String description;
    private boolean daily;
    private boolean weekly;
    private boolean monthly;
    private int frequency;
    private List<Integer> scheduledDays;
}
