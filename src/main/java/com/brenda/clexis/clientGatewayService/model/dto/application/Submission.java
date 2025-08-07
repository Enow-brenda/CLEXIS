package com.brenda.clexis.clientGatewayService.model.dto.application;

import java.util.List;

public class Submission {
    private String userId;
    private String submittedDate;
    private String description;
    private String submissionUrl;
    private boolean fileSubmission;
    private int score;
    List<Review> reviews;
}
