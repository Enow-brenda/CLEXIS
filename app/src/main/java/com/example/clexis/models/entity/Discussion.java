package com.example.clexis.models.entity;



import com.example.clexis.models.application.Review;
import com.example.clexis.models.enums.DiscussionType;

import java.util.List;
import java.util.UUID;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class Discussion extends RealmObject {

    private String id ;
    private String discussionTitle;
    private String discussionBody;
    private String userId;
    private String authorName;
    private List<Review> responses;
    private String date;
    private DiscussionType type;
}
