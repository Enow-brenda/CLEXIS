package com.brenda.clexis.clientGatewayService.controller.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.LearningPathDto;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.LearningPath;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/gateway/learningPath/")
public interface LearningPathController {

    @PostMapping("add")
    ResponseEntity<ResponseDto> addLearningPath(@RequestBody LearningPathDto learningPath);

    @GetMapping("get")
    ResponseEntity<ResponseDto> getActiveLearningPath();

    @PutMapping("update")
    ResponseEntity<ResponseDto> editLearningPath(@RequestBody LearningPath learningPath);

}
