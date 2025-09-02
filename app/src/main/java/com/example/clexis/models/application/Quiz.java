package com.example.clexis.models.application;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quiz {
    private String quizCode ;
    private List<Mcq> questions;
    private String difficulty;
    private int highestScore;
    private int attempts;

}
