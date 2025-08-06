package com.brenda.clexis.clientGatewayService.service.implementations;


import com.brenda.clexis.clientGatewayService.model.dto.response.MainResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OurUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        var user= userRepository.findUserByUsernameOrEmail(usernameOrEmail);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username nor email: " + usernameOrEmail);
        }
        return user;
    }

    public ResponseEntity<ResponseDto> getAllUsers() {
        return MainResponse.responseOk(userRepository.findAll());
    }

    public ResponseEntity<ResponseDto> getUserByUsername(String username) {
        var user= userRepository.findUserByUsername(username);
        if(user==null){
            return MainResponse.responseNotFound("User with this username Does not Exist");
        }
        return MainResponse.responseOk(user);
    }

    public ResponseEntity<ResponseDto> getUserByEmail(String email) {
        var user= userRepository.findUserByEmail(email);
        if(user==null){
            return MainResponse.responseNotFound("User with this email Does not Exist");
        }
        return MainResponse.responseOk(user);
    }

    public ResponseEntity<ResponseDto> deleteUser(String usernameOrEmail) {
        var user=userRepository.findUserByUsernameOrEmail(usernameOrEmail);
        if(user==null){
            return MainResponse.responseNotFound("User Does not Exist");
        }
        userRepository.delete(user);
        return MainResponse.responseOk(null);
    }


}
