package com.brenda.clexis.clientGatewayService.service.implementations;

import com.brenda.clexis.clientGatewayService.model.dto.RoleDto;
import com.brenda.clexis.clientGatewayService.model.dto.response.MainResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Role;
import com.brenda.clexis.clientGatewayService.repository.RoleRepository;
import com.brenda.clexis.clientGatewayService.service.interfaces.UserService;
import com.brenda.clexis.clientGatewayService.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final RoleRepository roleRepository;
    private final DateTimeUtils dateTimeUtils;
    private final OurUserDetailsService ourUserDetailsService;

    @Override
    public ResponseEntity<ResponseDto> addRole(RoleDto roleDto) {
        var role= roleRepository.findRoleByName(roleDto.getName().toUpperCase());
        if(role!=null){
            return MainResponse.responseAlreadyExist("Role Already Exist");
        }
        Role newRole = Role.builder()
                .name(roleDto.getName().toUpperCase())
                .createdAt(dateTimeUtils.currentDateTime())
                .updatedAt(dateTimeUtils.currentDateTime())
                .build();
        return MainResponse.responseOk(roleRepository.save(newRole));
    }

    @Override
    public ResponseEntity<ResponseDto> getRole(String name) {
        var role= roleRepository.findRoleByName(name);
        if(role==null){
            return MainResponse.responseNotFound("Role Does not Exist");
        }
        return MainResponse.responseOk(role);
    }

    @Override
    public ResponseEntity<ResponseDto> deleteRole(String name) {
        var role= roleRepository.findRoleByName(name);
        if(role==null){
            return MainResponse.responseNotFound("Role Does not Exist");
        }
        roleRepository.delete(role);
        return MainResponse.responseOk(null);
    }

    @Override
    public ResponseEntity<ResponseDto> getAllUsers() {
        return ourUserDetailsService.getAllUsers();
    }

    @Override
    public ResponseEntity<ResponseDto> getUserByUsername(String username) {
        return ourUserDetailsService.getUserByUsername(username);
    }

    @Override
    public ResponseEntity<ResponseDto> getUserByEmail(String email) {
        return ourUserDetailsService.getUserByEmail(email);
    }

    @Override
    public ResponseEntity<ResponseDto> getAllRoles() {
        return MainResponse.responseOk(roleRepository.findAll());
    }

    @Override
    public boolean existRole(String name) {
        return roleRepository.existsRoleByName(name);
    }


}
