package com.brenda.clexis.clientGatewayService.controller.implementations;

import com.brenda.clexis.clientGatewayService.controller.interfaces.ResourcesController;
import com.brenda.clexis.clientGatewayService.model.dto.ResourceDto;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Resource;
import com.brenda.clexis.clientGatewayService.service.interfaces.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ResourceControllerImpl implements ResourcesController {
    private final ResourceService resourceService;

    @Override
    public ResponseEntity<ResponseDto> getAllResources() {
        log.info("getting all resources");
        return resourceService.getAllResources();
    }

    @Override
    public ResponseEntity<ResponseDto> getValidResources() {
        log.info("getting all valid resources");
        return resourceService.getValidResources();
    }

    @Override
    public ResponseEntity<ResponseDto> addResource(ResourceDto resourceDto) {
        log.info("request adding resources {}", resourceDto);
        return resourceService.addResource(resourceDto);
    }

    @Override
    public ResponseEntity<ResponseDto> getResource(String id) {
        log.info("request getting resource with id {}", id);
        return resourceService.getResourceById(id);
    }

    @Override
    public ResponseEntity<ResponseDto> updateResource(Resource resource) {
        log.info("request updating resource : {}", resource);
        return resourceService.updateResource(resource);
    }

    @Override
    public ResponseEntity<ResponseDto> getUserResource(String userId) {
        log.info("request getting user resource ,userId: {}", userId);
        return resourceService.getUserResource(userId);
    }
}
