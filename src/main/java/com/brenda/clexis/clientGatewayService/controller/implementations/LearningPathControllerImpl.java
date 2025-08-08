package com.brenda.clexis.clientGatewayService.controller.implementations;

import com.brenda.clexis.clientGatewayService.controller.interfaces.LearningPathController;
import com.brenda.clexis.clientGatewayService.model.dto.LearningPathDto;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.LearningPath;
import com.brenda.clexis.clientGatewayService.service.interfaces.LearningPathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LearningPathControllerImpl implements LearningPathController {
    private final LearningPathService learningPathService;

    @Override
    public ResponseEntity<ResponseDto> addLearningPath(LearningPathDto learningPathDto) {
        log.info("Adding learning path: {}", learningPathDto);
        return learningPathService.addLearningPath(learningPathDto);
    }

    @Override
    public ResponseEntity<ResponseDto> getActiveLearningPath() {
        log.info("getting active learning path");
        return learningPathService.getActiveLearningPath();
    }

    @Override
    public ResponseEntity<ResponseDto> editLearningPath(LearningPath learningPath) {
        log.info("updating learning path :`{}`", learningPath);
        return learningPathService.updateLearningPath(learningPath);
    }
}
