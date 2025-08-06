package com.brenda.clexis.clientGatewayService.controller.implementations;
import com.brenda.clexis.clientGatewayService.controller.interfaces.UserController;
import com.brenda.clexis.clientGatewayService.model.dto.RoleDto;
import com.brenda.clexis.clientGatewayService.model.dto.request.AuthenticationRequest;
import com.brenda.clexis.clientGatewayService.model.dto.response.MainResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.service.implementations.OurUserDetailsService;
import com.brenda.clexis.clientGatewayService.service.interfaces.AuthService;
import com.brenda.clexis.clientGatewayService.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    private final UserService userService;
    private final AuthService authService;
    private final OurUserDetailsService ourUserDetailsService;
    @Override
    public ResponseEntity<ResponseDto> addUser(AuthenticationRequest user) {
        log.info("request adding user  : {} ...", user);
        return authService.addUser(user);
    }




    @Override
    public ResponseEntity<ResponseDto> getUserByUsername(String username) {
        log.info("request getting user by username : {} ...  ", username);
        return userService.getUserByUsername(username);
    }

    @Override
    public ResponseEntity<ResponseDto> getUserByEmail(String email) {
        log.info("request getting user by email : {} ...  ", email);
        return userService.getUserByEmail(email);
    }

    @Override
    public ResponseEntity<ResponseDto> deleteUser(String usernameOrEmail) {
        log.info("request deleting  user by email or username : {} ...  ", usernameOrEmail);
        try{
            return ourUserDetailsService.deleteUser(usernameOrEmail);
        }catch (AuthenticationException e){
            return MainResponse.Unauthorized(null);
        }
        catch(Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }


    @Override
    public ResponseEntity<ResponseDto> getRole(String name) {
        log.info("request getting role with name : {} ...  ", name);
        return userService.getRole(name);
    }

    @Override
    public ResponseEntity<ResponseDto> getAllRoles() {
        log.info("request getting all roles");
        return userService.getAllRoles();
    }

    @Override
    public ResponseEntity<ResponseDto> addRole(RoleDto role) {
        log.info("request adding role with name : {} ...  ", role);
        return userService.addRole(role);
    }

    @Override
    public ResponseEntity<ResponseDto> deleteRoleByName(String roleName) {
        return userService.deleteRole(roleName);
    }
}
