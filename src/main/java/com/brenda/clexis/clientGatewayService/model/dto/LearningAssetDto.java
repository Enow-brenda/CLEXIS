package com.brenda.clexis.clientGatewayService.model.dto;

import com.brenda.clexis.clientGatewayService.model.dto.application.Flashcard;
import com.brenda.clexis.clientGatewayService.model.dto.application.Quiz;
import com.brenda.clexis.clientGatewayService.model.enums.ToolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LearningAssetDto {
    private String title;
    private String originalFilename;
    private String originalFileUrl;
    private ToolType type;
    private int count;
    private int difficulty; //only for quiz 0 for easy,1 for medium,2 for hard
}
