package com.brenda.clexis.clientGatewayService.interfaces.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.LearningAssetDto;

import java.util.List;


public interface AiInterface {
    Object generateResource(LearningAssetDto learningAssetDto);

    List<String> findMatches(String userId);
}
