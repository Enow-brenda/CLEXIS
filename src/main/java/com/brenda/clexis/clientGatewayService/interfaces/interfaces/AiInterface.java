package com.brenda.clexis.clientGatewayService.interfaces.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.LearningAssetDto;


public interface AiInterface {
    Object generateResource(LearningAssetDto learningAssetDto);
}
