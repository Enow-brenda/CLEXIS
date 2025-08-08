package com.brenda.clexis.clientGatewayService.service.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.ResourceDto;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Resource;
import org.springframework.http.ResponseEntity;

public interface ResourceService {
    ResponseEntity<ResponseDto> getAllResources();

    ResponseEntity<ResponseDto> getValidResources();

    ResponseEntity<ResponseDto> addResource(ResourceDto resourceDto);

    ResponseEntity<ResponseDto> getResourceById(String id);

    ResponseEntity<ResponseDto> updateResource(Resource resource);

    ResponseEntity<ResponseDto> getUserResource(String userId);
}
