package com.brenda.clexis.clientGatewayService.controller.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.LearningAssetDto;
import com.brenda.clexis.clientGatewayService.model.dto.RegenerateLearningAsset;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/gateway/aiTools/")
public interface AIToolsController {

    @PostMapping("add")
    ResponseEntity<ResponseDto> generateResource(@RequestBody LearningAssetDto learningAssetDto);

    @GetMapping("user/get")
    ResponseEntity<ResponseDto> getUserLearningAsssets();

    @PostMapping("regenerate")
    ResponseEntity<ResponseDto> regenerateUserLearningAsssets(@RequestBody RegenerateLearningAsset regenerateLearningAsset);

    @DeleteMapping("delete/{id}")
    ResponseEntity<ResponseDto> deleteUserLearningAsssets(@PathVariable String id);
}
