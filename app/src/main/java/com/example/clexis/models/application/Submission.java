package com.example.clexis.models.application;



import java.util.List;
import java.util.UUID;


public class Submission {
    private String userId;
    private String submittedDate;
    private String description;
    private String submissionUrl;
    private boolean fileSubmission;
    private int score;
    List<Review> reviews;
}
