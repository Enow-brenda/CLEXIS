package com.example.clexis.models.entity;


import com.example.clexis.models.application.Flashcard;
import com.example.clexis.models.application.Quiz;
import com.example.clexis.models.enums.ToolType;

import java.util.List;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningAssets extends RealmObject {

    private String id;
    private String title;
    private String originalFilename;
    private String originalFileUrl;
    private ToolType type;
    private String summary;
    private String transcription;
    private List<Flashcard> flashcards;
    private Quiz quiz;
    private String userId;
}
