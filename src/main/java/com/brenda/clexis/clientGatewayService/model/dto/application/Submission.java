package com.brenda.clexis.clientGatewayService.model.dto.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Submission {
    private String userId;
    private String submittedDate;
    private String description;
    private String submissionUrl;
    private boolean fileSubmission;
    private int score;
    List<Review> reviews;
}
