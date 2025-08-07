package com.brenda.clexis.clientGatewayService.model.entity;

import com.brenda.clexis.clientGatewayService.model.dto.application.Flashcard;
import com.brenda.clexis.clientGatewayService.model.dto.application.Quiz;
import com.brenda.clexis.clientGatewayService.model.enums.ToolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "learningAssets")
public class LearningAssets {
    @Id
    private String id = UUID.randomUUID().toString();
    private String title;
    private String originalFilename;
    private String originalFileUrl;
    private ToolType type;
    private String summary;
    private String transcription;
    private Flashcard flashcards;
    private Quiz quiz;
    private String userId;
}
