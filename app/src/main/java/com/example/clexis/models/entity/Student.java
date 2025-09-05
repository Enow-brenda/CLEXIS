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

    public static Student defaultStudent() {
        Student student = new Student();
        student.id = "STU12345";
        student.userId = "USR67890";
        student.fullName = "John Doe";
        student.academicLevel = AcademicLevel.UNDERGRADUATE; // example enum
        student.bioOrInterest = "Passionate about AI, programming, and open source.";
        student.phoneNumber = "+237650000000";
        student.profession = "Software Developer";
        student.language = "English";
        student.points = 100;
        student.resourcePoints = 50;
        student.communityPoints = 30;
        student.dateCreated = "2025-09-02T10:00:00Z";
        return student;
    }

}
