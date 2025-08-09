package com.brenda.clexis.clientGatewayService.service.implementations;

import com.brenda.clexis.clientGatewayService.model.dto.RejectResource;
import com.brenda.clexis.clientGatewayService.model.dto.response.MainResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Resource;
import com.brenda.clexis.clientGatewayService.model.entity.Student;
import com.brenda.clexis.clientGatewayService.model.entity.User;
import com.brenda.clexis.clientGatewayService.repository.ResourceRepository;
import com.brenda.clexis.clientGatewayService.repository.StudentRepository;
import com.brenda.clexis.clientGatewayService.repository.UserRepository;
import com.brenda.clexis.clientGatewayService.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Override
    public ResponseEntity<ResponseDto> markAsVerified(String resourceId) {
        try{
            if(resourceRepository.existsById(resourceId)){
                 Resource resource = resourceRepository.findResourceById(resourceId);
                 resource.setVerified(true);
                 resourceRepository.save(resource);
                 //notify
                Student student = studentRepository.findStudentByUserId(resource.getUserId());
                student.setResourcePoints(student.getResourcePoints() + 10);
                return MainResponse.responseOk(null,"Resource "+resource.getName()+ " marked as verified");
            }
            return MainResponse.responseNotFound("Resource not found");

        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> rejectResource(RejectResource rejectResource) {
        try{
            if(resourceRepository.existsById(rejectResource.getResourceId())){
                if(rejectResource.getReason() == null || rejectResource.getReason().isEmpty()){
                    return MainResponse.responseBadCredentials("Reason for rejection not found");
                }
                Resource resource = resourceRepository.findResourceById(rejectResource.getResourceId());
                resource.setVerified(false);
                resource.setRejected(true);
                resource.setReasonForRejection(rejectResource.getReason());
                resourceRepository.save(resource);
                Student student = studentRepository.findStudentByUserId(resource.getUserId());
                student.setResourcePoints(student.getResourcePoints() - 10);
                //notify
                return MainResponse.responseOk(null,"Resource "+resource.getName()+ " resource rejected");
            }
            return MainResponse.responseNotFound("Resource not found");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> blockUserAccount(String userId) {
        try{
            User user = userRepository.findUserById(userId);
            if(user != null){
                user.setBlocked(true);
                userRepository.save(user);
                Student student = studentRepository.findStudentByUserId(userId);
                if(student != null){
                    student.setPoints(student.getPoints() - 50);
                }
                //notify
                return MainResponse.responseOk(null,"User "+user.getUsername()+ " account blocked");
            }
            return MainResponse.responseNotFound("User not found");

        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> unblockUserAccount(String userId) {
        try{
            User user = userRepository.findUserById(userId);
            if(user != null){
                user.setBlocked(false);
                userRepository.save(user);

                //notify
                return MainResponse.responseOk(null,"User "+user.getUsername()+ " account unblocked");
            }
            return MainResponse.responseNotFound("User not found");

        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }
}
