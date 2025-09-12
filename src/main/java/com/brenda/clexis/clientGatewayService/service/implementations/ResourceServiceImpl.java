package com.brenda.clexis.clientGatewayService.service.implementations;

import com.brenda.clexis.clientGatewayService.model.dto.ResourceDto;
import com.brenda.clexis.clientGatewayService.model.dto.response.MainResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Resource;
import com.brenda.clexis.clientGatewayService.model.entity.Student;
import com.brenda.clexis.clientGatewayService.model.entity.User;
import com.brenda.clexis.clientGatewayService.repository.ResourceRepository;
import com.brenda.clexis.clientGatewayService.repository.StudentRepository;
import com.brenda.clexis.clientGatewayService.repository.UserRepository;
import com.brenda.clexis.clientGatewayService.service.interfaces.LearningPathService;
import com.brenda.clexis.clientGatewayService.service.interfaces.ResourceService;
import com.brenda.clexis.clientGatewayService.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final JWTUtils jWTUtils;
    private final LearningPathService learningPathService;
    private final UserRepository userRepository;
    private final StudentProfileService studentProfileService;
    private final StudentRepository studentRepository;


    @Override
    public ResponseEntity<ResponseDto> getAllResources() {
       try{
           return MainResponse.responseOk(resourceRepository.findAll());
       } catch (Exception e) {
           return MainResponse.responseError(e.getMessage());
       }
    }

    @Override
    public ResponseEntity<ResponseDto> getValidResources() {
        try{
            List<Resource> resources = resourceRepository.findAll();
            List<Resource> filtered = resources.stream()
                    .filter(resource -> resource.isFree() || resource.isVerified())
                    .toList();
            return MainResponse.responseOk(filtered);

        } catch (Exception e) {
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> addResource(ResourceDto resourceDto) {
        try{
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String dateString = currentDate.format(formatter);
            String userId = jWTUtils.extractUserId(learningPathService.getToken());
            User user = userRepository.findUserById(userId);
            Resource resource = Resource.builder()
                    .name(resourceDto.getName())
                    .description(resourceDto.getDescription())
                    .type(resourceDto.getType())
                    .free(resourceDto.isFree())
                    .verified(false)
                    .creationDate(dateString)
                    .fileUrl(resourceDto.getFileUrl())
                    .imageUrl(resourceDto.getImageUrl())
                    .price(resourceDto.getPrice())
                    .merchantNumber(resourceDto.getMerchantNumber())
                    .userId(userId)
                    .username(user.getUsername())
                    .build();


            if(resource.isFree()){
                //notify if resource is free
                System.out.println("Free resource");
            }

            Student studen = studentRepository.findStudentByUserId(jWTUtils.extractUserId(learningPathService.getToken());
            studen.setPoints(studen.getPoints() + 1);
            studentRepository.save(studen);

//            studentProfileService.recordResourceShared(resource.getUserId(),resource.getType().name());
            return MainResponse.responseOk(resourceRepository.save(resource));
        } catch (Exception e) {
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getResourceById(String id) {
        try{
            if(resourceRepository.existsById(id)) {
                return MainResponse.responseOk(resourceRepository.findById(id));
            }
            return MainResponse.responseNotFound("Resource not found");

        } catch (Exception e) {
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> updateResource(Resource resource) {
        try{
            if(resourceRepository.existsById(resource.getId())) {
                resource.setUserId(jWTUtils.extractUserId(learningPathService.getToken()));
                return MainResponse.responseOk(resourceRepository.save(resource));
            }
            return MainResponse.responseNotFound("Resource not found");
        } catch (Exception e) {
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getUserResource(String userId) {
        try{
            if(!userRepository.existsById(userId)) {
                return MainResponse.responseNotFound("User not found");
            }
            var resources = resourceRepository.findResourcesByUserId(userId);
            if(userId.equals(jWTUtils.extractUserId(learningPathService.getToken()))){
                return MainResponse.responseOk(resources);
            }
            List<Resource> filtered = resources.stream()
                    .filter(resource -> resource.isFree() || resource.isVerified())
                    .toList();
            return MainResponse.responseOk(filtered);

        } catch (Exception e) {
            return MainResponse.responseError(e.getMessage());
        }
    }



}
