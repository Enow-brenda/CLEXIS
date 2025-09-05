package com.example.clexis.models.entity;


import com.example.clexis.models.application.BuddyScore;
import com.example.clexis.models.application.Milestone;
import com.example.clexis.models.dto.JoinRequest;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BuddyProgram {

    private String id ;
    private String title;
    private String description;
    private String deadline;
    private String startDate;
    private boolean opened;
    private String authorId;
    private String authorName;
    private List<Milestone> mileStoneList;
    private List<JoinRequest> requests;
    private List<BuddyScore> buddies;
}
