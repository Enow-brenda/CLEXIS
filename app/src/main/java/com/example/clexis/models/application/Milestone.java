package com.example.clexis.models.application;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Milestone {
    private String name;
    private String description;
    private boolean quiz;
    private String taskOrQuizCode;
    private List<Submission> submissionList= List.of();
}
