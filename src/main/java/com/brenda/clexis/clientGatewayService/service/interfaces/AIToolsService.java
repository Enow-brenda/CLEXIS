package com.brenda.clexis.clientGatewayService.service.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.LearningAssetDto;
import com.brenda.clexis.clientGatewayService.model.dto.RegenerateLearningAsset;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;

public interface AIToolsService {
    ResponseEntity<ResponseDto> generateResource(LearningAssetDto learningAssetDto);

    ResponseEntity<ResponseDto> getLearningResource();

    ResponseEntity<ResponseDto> regenerateUserLearningAssets(RegenerateLearningAsset regenerateLearningAsset);

    ResponseEntity<ResponseDto> deleteAsset(String id);
}
