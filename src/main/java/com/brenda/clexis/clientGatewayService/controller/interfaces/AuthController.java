package com.brenda.clexis.clientGatewayService.controller.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.application.StudentDto;
import com.brenda.clexis.clientGatewayService.model.dto.request.ChangePasswordRequest;
import com.brenda.clexis.clientGatewayService.model.dto.request.LoginRequest;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/v1/gateway/authentication/")
public interface AuthController {

    @PostMapping("login")
    ResponseEntity<ResponseDto> login(@RequestBody LoginRequest loginRequest);

    @PostMapping("student/register")
    ResponseEntity<ResponseDto> registerStudent(@RequestBody StudentDto studentDto);

    @PostMapping("changePassword")
    ResponseEntity<ResponseDto> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest);
}
