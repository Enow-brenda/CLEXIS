package com.brenda.clexis.clientGatewayService.model.dto.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Review {
    private String userId;
    private String description;
    private String date;
    private int score;
    private boolean submissionReview;
}
