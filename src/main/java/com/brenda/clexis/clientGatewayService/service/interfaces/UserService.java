package com.brenda.clexis.clientGatewayService.service.interfaces;


import com.brenda.clexis.clientGatewayService.model.dto.RoleDto;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;


public interface UserService {
    ResponseEntity<ResponseDto> addRole(RoleDto role);

    ResponseEntity<ResponseDto> getRole(String name);

    ResponseEntity<ResponseDto> deleteRole(String name);

    ResponseEntity<ResponseDto> getAllUsers();

    ResponseEntity<ResponseDto> getUserByUsername(String username);

    ResponseEntity<ResponseDto> getUserByEmail(String email);

    ResponseEntity<ResponseDto> getAllRoles();

    boolean existRole(String name);

}
