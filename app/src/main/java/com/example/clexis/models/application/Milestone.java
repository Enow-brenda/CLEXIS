package com.example.clexis.models.application;

import java.util.ArrayList;
import java.util.List;

public class Milestone {
    private String name;
    private String description;
    private boolean quiz;
    private String taskOrQuizCode;
    private List<Submission> submissionList= List.of();
}
