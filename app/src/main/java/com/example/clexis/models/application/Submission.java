package com.example.clexis.models.application;



import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Submission {
    private String authorName;
    private String userId;
    private String submittedDate;
    private String description;
    private String submissionUrl;
    private String filename;
    private boolean fileSubmission;
    private int score;
    List<Review> reviews;
}
