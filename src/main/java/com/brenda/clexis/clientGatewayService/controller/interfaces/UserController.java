package com.brenda.clexis.clientGatewayService.controller.interfaces;


import com.brenda.clexis.clientGatewayService.model.dto.RoleDto;
import com.brenda.clexis.clientGatewayService.model.dto.request.AuthenticationRequest;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/gateway/")
public interface UserController {
    @PostMapping("user/addOne")
    ResponseEntity<ResponseDto> addUser(@RequestBody AuthenticationRequest user);

//    @PostMapping("user/addMany")
//    ResponseEntity<ResponseDto> addUsers(@RequestBody List<AuthenticationRequest> users);

//    @GetMapping("user/getAll")
//    ResponseEntity<ResponseDto> getAllUsers();

    @GetMapping("user/getByUsername/{username}")
    ResponseEntity<ResponseDto> getUserByUsername(@PathVariable String username);

    @GetMapping("user/getByEmail/{email}")
    ResponseEntity<ResponseDto> getUserByEmail(@PathVariable String email);

    @DeleteMapping("deleteUser/{usernameOrEmail}")
    ResponseEntity<ResponseDto> deleteUser(@PathVariable String usernameOrEmail);

    @GetMapping("role/getOne/{name}")
    ResponseEntity<ResponseDto> getRole(@PathVariable String name);

    @GetMapping("role/getAll")
    ResponseEntity<ResponseDto> getAllRoles();

    @PostMapping("role/add")
    ResponseEntity<ResponseDto> addRole(@RequestBody RoleDto role);

    @DeleteMapping("role/delete/{roleName}")
    ResponseEntity<ResponseDto> deleteRoleByName(@PathVariable String roleName);


}
