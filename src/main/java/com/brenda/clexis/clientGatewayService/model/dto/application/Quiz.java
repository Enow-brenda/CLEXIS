package com.brenda.clexis.clientGatewayService.model.dto.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quiz {
    private String quizCode = UUID.randomUUID().toString();
    private List<Mcq> questions;
    private String difficulty;
    private int highestScore;
    private List<String> users;
    private List<Integer> scores;

}
