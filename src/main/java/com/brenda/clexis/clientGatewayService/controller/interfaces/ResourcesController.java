package com.brenda.clexis.clientGatewayService.controller.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.ResourceDto;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/gateway/student/resources/")
public interface ResourcesController {

    @GetMapping("getAll")
    ResponseEntity<ResponseDto> getAllResources();

    @GetMapping("getValid")
    ResponseEntity<ResponseDto> getValidResources();

    @PostMapping("add")
    ResponseEntity<ResponseDto> addResource(@RequestBody ResourceDto resourceDto);

    @GetMapping("get/{id}")
    ResponseEntity<ResponseDto> getResource(@PathVariable String id);

    @PutMapping("update")
    ResponseEntity<ResponseDto> updateResource(@RequestBody Resource resource);

    @GetMapping("get/user/{userId}")
    ResponseEntity<ResponseDto> getUserResource(@PathVariable String userId);

}
