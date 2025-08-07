package com.brenda.clexis.clientGatewayService.service.interfaces;


import com.brenda.clexis.clientGatewayService.model.dto.application.StudentDto;
import com.brenda.clexis.clientGatewayService.model.dto.request.AuthenticationRequest;
import com.brenda.clexis.clientGatewayService.model.dto.request.ChangePasswordRequest;
import com.brenda.clexis.clientGatewayService.model.dto.request.LoginRequest;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<ResponseDto> login(LoginRequest loginRequest);

    boolean existUser(String email, String username);

    ResponseEntity<ResponseDto> addUser(AuthenticationRequest authenticationRequest);


    ResponseEntity<ResponseDto> changePassword(ChangePasswordRequest changePasswordRequest);


    ResponseEntity<ResponseDto> registerStudent(StudentDto studentDto);
}
