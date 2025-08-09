package com.brenda.clexis.clientGatewayService.controller.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.RejectResource;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/gateway/admin/")
public interface AdminController {

    @GetMapping("resource/verify/{resourceId}")
    ResponseEntity<ResponseDto> markVerified(@PathVariable String resourceId);

    @PostMapping("resource/reject")
    ResponseEntity<ResponseDto> rejectResource(@RequestBody RejectResource rejectResource);

    @GetMapping("accounts/block/{userId}")
    ResponseEntity<ResponseDto> blockAccount(@PathVariable String userId);

    @GetMapping("accounts/unblock/{userId}")
    ResponseEntity<ResponseDto> unblockAccount(@PathVariable String userId);
}
