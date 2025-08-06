package com.brenda.clexis.clientGatewayService.Config;



import com.brenda.clexis.clientGatewayService.model.dto.RoleDto;
import com.brenda.clexis.clientGatewayService.model.dto.request.AuthenticationRequest;
import com.brenda.clexis.clientGatewayService.service.interfaces.AuthService;
import com.brenda.clexis.clientGatewayService.service.interfaces.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultRoleCreation {

    private final UserService roleService;
    private final AuthService authService;


    @Value("${defaultUser.email}")
    private String defaultEmail;
    @Value("${defaultUser.username}")
    private String defaultUsername;
    @Value("${defaultUser.password}")
    private String defaultPassword;
    @Value("${defaultUser.role}")
    private String defaultRole;

    @PostConstruct
    public void createRole(){

        if (!roleService.existRole(defaultRole)){
            RoleDto roleDto = RoleDto.builder()
                    .name(defaultRole)
                    .build();
            roleService.addRole(roleDto);
        }
        if (!roleService.existRole("STUDENT")){
            RoleDto roleDto = RoleDto.builder()
                    .name("STUDENT")
                    .build();
            roleService.addRole(roleDto);
        }
    }

    @PostConstruct
    public void createAdminUser(){
        if (!authService.existUser(defaultEmail, defaultUsername)){
            AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                    .email(defaultEmail)
                    .username(defaultUsername)
                    .password(defaultPassword)
                    .role(defaultRole)
                    .build();
            authService.addUser(authenticationRequest);
        }
    }




}
