package com.brenda.clexis.clientGatewayService.service.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.RejectResource;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<ResponseDto> markAsVerified(String resourceId);

    ResponseEntity<ResponseDto> rejectResource(RejectResource rejectResource);

    ResponseEntity<ResponseDto> blockUserAccount(String userId);

    ResponseEntity<ResponseDto> unblockUserAccount(String userId);
}
