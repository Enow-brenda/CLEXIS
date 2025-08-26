package com.example.clexis.models.entity;


import com.example.clexis.models.application.BuddyScore;
import com.example.clexis.models.application.Milestone;

import java.util.List;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BuddyProgram extends RealmObject {

    private String id ;
    private String title;
    private String description;
    private String deadline;
    private String startDate;
    private boolean opened;
    private String authorId;
    private String authorName;
    private List<Milestone> mileStoneList;
    private List<BuddyScore> buddies;
}
