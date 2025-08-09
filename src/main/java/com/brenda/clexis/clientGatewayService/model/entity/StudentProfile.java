package com.brenda.clexis.clientGatewayService.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "student_profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentProfile {

    @Id
    private String studentId;

    @Indexed(unique = true)
    private String userId;

    private int academicLevel;
    private String profession;
    private String languageCode;

    private String bioOrInterest;

    private double studyBuddyActivityScore;
    private double forumEngagementScore;
    private double sharingScore;
    private double personalScore;

    private List<String> preferredStudyTopics;
    private List<String> learningPathSubjects;
    private List<String> resourceCategoriesShared;


}
