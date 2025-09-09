package com.brenda.clexis.clientGatewayService.model.dto.application;


import java.util.List;

import com.brenda.clexis.clientGatewayService.model.entity.BuddyProgram;
import com.brenda.clexis.clientGatewayService.model.entity.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StudentProfileObject {
    private String userId;
    private String fullName;
    private String email;
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
