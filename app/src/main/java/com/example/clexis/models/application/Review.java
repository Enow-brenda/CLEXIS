package com.example.clexis.models.application;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private String userId;

    private String authorName;
    private String description;
    private String date;
    private int score;
    private boolean submissionReview;
}
