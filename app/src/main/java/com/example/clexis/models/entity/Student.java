package com.example.clexis.models.entity;


import com.example.clexis.models.enums.AcademicLevel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Student {

    private String id;


    private String userId;
    private String fullName;
    private AcademicLevel academicLevel;
    private String bioOrInterest;
    private String phoneNumber;
    private String profession;
    private String language;
    private int points;
    private int resourcePoints;
    private int communityPoints;
    private String dateCreated;


}
