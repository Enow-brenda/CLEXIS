package com.brenda.clexis.clientGatewayService.service.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.LearningPathDto;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.LearningPath;
import org.springframework.http.ResponseEntity;

public interface LearningPathService {
    ResponseEntity<ResponseDto> getActiveLearningPath();

    ResponseEntity<ResponseDto> addLearningPath(LearningPathDto learningPathDto);

    ResponseEntity<ResponseDto> updateLearningPath(LearningPath learningPath);

    String getToken();
}
