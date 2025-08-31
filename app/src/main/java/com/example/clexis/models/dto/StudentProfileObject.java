package com.example.clexis.models.dto;


import com.example.clexis.models.entity.BuddyProgram;
import com.example.clexis.models.entity.Resource;
import com.example.clexis.models.enums.AcademicLevel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StudentProfileObject {

    private String id;


    private String userId;
    private String fullName;
    private String academicLevel;
    private String bioOrInterest;
    private String phoneNumber;
    private String profession;
    private String language;
    private int points;
    private int learningPath;
    private int rank;
    private int discussions;
    private int resourceShared;
    private int tasks;
    private int completedTasks;
    private int programs;
    private List<BuddyProgram> recentPrograms;
    private List<Resource> recentResources;

    private String dateCreated;


}
