package com.example.clexis.models.entity;


import com.example.clexis.models.enums.AcademicLevel;

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


    public String getFullName(){
        return fullName;
    }
}
