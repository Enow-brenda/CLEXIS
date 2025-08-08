package com.brenda.clexis.clientGatewayService.controller.implementations;

import com.brenda.clexis.clientGatewayService.controller.interfaces.AIToolsController;
import com.brenda.clexis.clientGatewayService.model.dto.LearningAssetDto;
import com.brenda.clexis.clientGatewayService.model.dto.RegenerateLearningAsset;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.service.interfaces.AIToolsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class AIToolsControllerImpl implements AIToolsController {

    private final AIToolsService aIToolsService;

    @Override
    public ResponseEntity<ResponseDto> generateResource(LearningAssetDto learningAssetDto) {
       log.info("Generating resource : {}", learningAssetDto);
        return aIToolsService.generateResource(learningAssetDto);
    }

    @Override
    public ResponseEntity<ResponseDto> getUserLearningAsssets() {
        log.info("getting user learning assets...");
        return aIToolsService.getLearningResource();
    }

    @Override
    public ResponseEntity<ResponseDto> regenerateUserLearningAsssets(RegenerateLearningAsset regenerateLearningAsset) {
        log.info("regenerating user's  learning assets: {}",regenerateLearningAsset);
        return aIToolsService.regenerateUserLearningAssets(regenerateLearningAsset);
    }

    @Override
    public ResponseEntity<ResponseDto> deleteUserLearningAsssets(String id) {
        log.info("deleting learning assets: {}",id);
        return aIToolsService.deleteAsset(id);
    }
}
