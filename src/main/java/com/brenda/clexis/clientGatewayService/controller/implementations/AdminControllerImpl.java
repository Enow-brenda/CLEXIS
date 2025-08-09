package com.brenda.clexis.clientGatewayService.controller.implementations;

import com.brenda.clexis.clientGatewayService.controller.interfaces.AdminController;
import com.brenda.clexis.clientGatewayService.model.dto.RejectResource;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class AdminControllerImpl implements AdminController {
    private final AdminService adminService;

    @Override
    public ResponseEntity<ResponseDto> markVerified(String resourceId) {
        log.info("Marking verified resource with id: {}",  resourceId);
        return adminService.markAsVerified(resourceId);
    }

    @Override
    public ResponseEntity<ResponseDto> rejectResource(RejectResource rejectResource) {
        log.info("Rejecting resource: {}", rejectResource);
        return adminService.rejectResource(rejectResource);
    }

    @Override
    public ResponseEntity<ResponseDto> blockAccount(String userId) {
        log.info("Blocking account with id: {}", userId);
        return adminService.blockUserAccount(userId);
    }

    @Override
    public ResponseEntity<ResponseDto> unblockAccount(String userId) {
        log.info("Blocking account with id: {}", userId);
        return adminService.unblockUserAccount(userId);
    }
}
