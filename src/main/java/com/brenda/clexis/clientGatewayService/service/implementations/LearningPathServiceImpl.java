package com.brenda.clexis.clientGatewayService.service.implementations;

import com.brenda.clexis.clientGatewayService.model.dto.LearningPathDto;
import com.brenda.clexis.clientGatewayService.model.dto.response.MainResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.LearningPath;
import com.brenda.clexis.clientGatewayService.repository.LearningPathRepository;
import com.brenda.clexis.clientGatewayService.service.interfaces.LearningPathService;
import com.brenda.clexis.clientGatewayService.utils.JWTUtils;
import com.brenda.clexis.clientGatewayService.utils.Mappers;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class LearningPathServiceImpl implements LearningPathService {

    private final JWTUtils jWTUtils;
    private final HttpServletRequest request;
    private final LearningPathRepository learningPathRepository;
    private final Mappers mappers;
    private final StudentProfileService studentProfileService;


    @Override
    public ResponseEntity<ResponseDto> getActiveLearningPath() {
        try{
            var userLearningPath = learningPathRepository.findLearningPathByActiveIsTrueAndUserId(jWTUtils.extractUserId(getToken()));
            if(userLearningPath == null){
                return MainResponse.responseNotFound("No active Learning Path. Proceed in creating One");
            }
            return MainResponse.responseOk(userLearningPath);

        } catch (Exception e) {
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> addLearningPath(LearningPathDto learningPathDto) {
        try{
            if(learningPathRepository.existsLearningPathByActiveIsTrue()){
                return MainResponse.responseAlreadyExist("Only one learning path can be active at a time. Complete this first");
            }
            // Define the formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Parse the strings
            LocalDate sDate = LocalDate.parse(learningPathDto.getStartDate(), formatter);
            LocalDate eDate = LocalDate.parse(learningPathDto.getEndDate(), formatter);
            if(eDate.isBefore(sDate)){
                return MainResponse.responseError("Start date cannot be after end date ");
            }

            LearningPath learningPath = LearningPath.builder()
                    .description(learningPathDto.getDescription())
                    .name(learningPathDto.getName())
                    .endDate(learningPathDto.getEndDate())
                    .startDate(learningPathDto.getStartDate())
                    .frequentTasks(mappers.convertFrequentTasks(learningPathDto.getFrequentTasks()))
                    .modules(mappers.convertModules(learningPathDto.getModules()))
                    .creationDate(LocalDateTime.now())
                    .goalType(learningPathDto.getGoalType())
                    .userId(jWTUtils.extractUserId(getToken()))
                    .build();
            //notify
            studentProfileService.recordLearningPathProgress(learningPath.getUserId(), learningPath.getName());
            return MainResponse.responseOk(learningPathRepository.save(learningPath));

        } catch (Exception e) {
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> updateLearningPath(LearningPath learningPath) {
        try{

            if(learningPathRepository.existsById(learningPath.getId())){
                return MainResponse.responseNotFound("Learning path not found");
            }
            // Define the formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Parse the strings
            LocalDate sDate = LocalDate.parse(learningPath.getStartDate(), formatter);
            LocalDate eDate = LocalDate.parse(learningPath.getEndDate(), formatter);
            if(eDate.isBefore(sDate)){
                return MainResponse.responseError("Start date cannot be after end date ");
            }

           learningPath.setUserId(jWTUtils.extractUserId(getToken()));
            return MainResponse.responseOk(learningPathRepository.save(learningPath));

        } catch (Exception e) {
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public String getToken() {
        return request.getHeader("Authorization");
    }
}
