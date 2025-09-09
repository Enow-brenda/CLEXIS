package com.brenda.clexis.clientGatewayService.service.implementations;


import com.brenda.clexis.clientGatewayService.model.dto.application.StudentDto;
import com.brenda.clexis.clientGatewayService.model.dto.request.AuthenticationRequest;
import com.brenda.clexis.clientGatewayService.model.dto.request.ChangePasswordRequest;
import com.brenda.clexis.clientGatewayService.model.dto.request.LoginRequest;
import com.brenda.clexis.clientGatewayService.model.dto.response.AuthenticationResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.MainResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Student;
import com.brenda.clexis.clientGatewayService.model.entity.User;
import com.brenda.clexis.clientGatewayService.repository.RoleRepository;
import com.brenda.clexis.clientGatewayService.repository.StudentRepository;
import com.brenda.clexis.clientGatewayService.repository.UserRepository;
import com.brenda.clexis.clientGatewayService.service.interfaces.AuthService;
import com.brenda.clexis.clientGatewayService.service.interfaces.LearningPathService;
import com.brenda.clexis.clientGatewayService.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class AuthServiceImp implements AuthService {

    private final UserRepository ourUserRepository;
    private final RoleRepository roleRepository;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final StudentRepository studentRepository;
    private final StudentProfileService studentProfileService;
    private final LearningPathService learningPathService;

    @Override
    public ResponseEntity<ResponseDto> login(LoginRequest loginRequest) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(),loginRequest.getPassword()));
            var user= ourUserRepository.findUserByUsernameOrEmail(loginRequest.getUsernameOrEmail());
            if(user==null){
                return MainResponse.responseNotFound("User does not Exist");
            }
            var jwt= jwtUtils.generateToken(user);
            var expiration=jwtUtils.getExpirationFromToken(jwt);
            var refreshToken=jwtUtils.generateRefreshToken(new HashMap<>(),user);
            var res = AuthenticationResponse.builder()
                    .token(jwt)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .refreshToken(refreshToken)
                    .expirationTime(expiration)
                    .build();
            return MainResponse.responseOk(res);
        }catch (BadCredentialsException e) {
            return MainResponse.responseBadCredentials(null);
        }
        catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public boolean existUser(String email, String username) {
        return ourUserRepository.existsUserByEmail(email) || ourUserRepository.existsUserByUsername(username);
    }

    @Override
    public ResponseEntity<ResponseDto> addUser(AuthenticationRequest authenticationRequest) {
        try{
            var user=ourUserRepository.findUserByUsername(authenticationRequest.getUsername());
            if(user!=null || ourUserRepository.existsUserByEmail(authenticationRequest.getEmail())){
                return MainResponse.responseAlreadyExist("User Credentials Already Exist");
            }
            if(!roleRepository.existsRoleByName(authenticationRequest.getRole())){
                return MainResponse.responseNotFound("Role does not Exist");
            }
            User newUser= User.builder()
                    .email(authenticationRequest.getEmail())
                    .username(authenticationRequest.getUsername())
                    .role(authenticationRequest.getRole())
                    .password(passwordEncoder.encode(authenticationRequest.getPassword()))
                    .build();
            ourUserRepository.save(newUser);
            return MainResponse.responseOk(newUser);

        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }



    @Override
    public ResponseEntity<ResponseDto> changePassword(ChangePasswordRequest changePasswordRequest) {
        String id  = jwtUtils.extractUserId(learningPathService.getToken());
        var user =  ourUserRepository.findUserById(id);
        if(user!=null){
            if(passwordEncoder.matches(changePasswordRequest.getOldPassword(),user.getPassword())){
                user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
                ourUserRepository.save(user);
                return MainResponse.responseOk(null,"password changed successfully");
            }
        }
        return MainResponse.responseBadCredentials("Unable to change password");
    }

    @Override
    public ResponseEntity<ResponseDto> registerStudent(StudentDto studentDto) {
        User initialUser= ourUserRepository.findUserByEmail(studentDto.getEmail());
        if(initialUser!=null){
            return MainResponse.responseAlreadyExist("Student with Email already exist");
        }
        String fullName = studentDto.getFullName();
        String[] parts = fullName.trim().split("\\s+"); // split by spaces
        String lastName = parts[parts.length - 1]; // "Doe"

        String initials = "";
        for (int i = 0; i < parts.length - 1; i++) {
            initials += parts[i].charAt(0); // "JM"
        }

        String username = (initials + lastName).toLowerCase();

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(studentDto.getPassword()))
                .email(studentDto.getEmail())
                .role("STUDENT")
                .build();
        var newUser = ourUserRepository.save(user);
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        String formattedDate = sdf.format(now);
       Student newStudent= Student.builder()
               .userId(newUser.getId())
               .academicLevel(studentDto.getAcademicLevel())
               .bioOrInterest(studentDto.getBioOrInterest())
               .fullName(studentDto.getFullName())
               .phoneNumber(studentDto.getPhone())
               .language(studentDto.getLanguage())
               .profession(studentDto.getProfession())
               .dateCreated(formattedDate)
               .build();
        studentProfileService.createProfile(user.getId(), newStudent.getAcademicLevel().getCode(), newStudent.getProfession(), newStudent.getLanguage(), newStudent.getBioOrInterest());
        // notify
       return MainResponse.responseOk(studentRepository.save(newStudent));
    }


}
