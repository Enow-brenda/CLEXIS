package com.brenda.clexis.clientGatewayService.controller.implementations;


import com.brenda.clexis.clientGatewayService.controller.interfaces.AuthController;
import com.brenda.clexis.clientGatewayService.model.dto.application.StudentDto;
import com.brenda.clexis.clientGatewayService.model.dto.request.ChangePasswordRequest;
import com.brenda.clexis.clientGatewayService.model.dto.request.LoginRequest;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {
    private final AuthService authService;

    @Override
    public ResponseEntity<ResponseDto> login(LoginRequest loginRequest) {
        log.info("login request : {} ...", loginRequest);
        return authService.login(loginRequest);
    }

    @Override
    public ResponseEntity<ResponseDto> registerStudent(StudentDto studentDto) {
        log.info("register student request : {} ...", studentDto);
        return authService.registerStudent(studentDto);
    }



}
