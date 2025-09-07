package com.brenda.clexis.clientGatewayService.service.implementations;




import com.brenda.clexis.clientGatewayService.interfaces.interfaces.AiInterface;
import com.brenda.clexis.clientGatewayService.interfaces.interfaces.NotificationInterface;
import com.brenda.clexis.clientGatewayService.model.dto.NotificationRequest;
import com.brenda.clexis.clientGatewayService.model.dto.response.MainResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Notification;
import com.brenda.clexis.clientGatewayService.model.entity.Student;
import com.brenda.clexis.clientGatewayService.repository.NotificationRepository;
import com.brenda.clexis.clientGatewayService.repository.StudentRepository;
import com.brenda.clexis.clientGatewayService.service.interfaces.LearningPathService;
import com.brenda.clexis.clientGatewayService.service.interfaces.StudentService;
import com.brenda.clexis.clientGatewayService.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {


    private final NotificationInterface notificationInterface;
    private final JWTUtils jWTUtils;
    private final LearningPathService learningPathService;
    private final StudentRepository studentRepository;
    private final AiInterface aiInterface;
    private final NotificationRepository notificationRepository;
    private final StudentProfileService studentProfileService;

    @Override
    public ResponseEntity<ResponseDto> notifyStudents(NotificationRequest notificationRequest) {
        try{
            notificationInterface.sendCustomEmail(notificationRequest);
            return MainResponse.responseOk(null,"Students Successfully Notified");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getStudentInfo(String userId) {
        try{
            var student = studentRepository.findStudentByUserId(userId);
            if(student == null){
                return MainResponse.responseNotFound("Student Not Found");
            }
            return MainResponse.responseOk(student);
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> updateStudent(Student student) {
        try{

            student.setUserId(jWTUtils.extractUserId(learningPathService.getToken()));
            String userId = student.getUserId();
            if(studentRepository.existsById(student.getId())){
                studentRepository.save(student);

                studentProfileService.updateProfile(userId,student.getBioOrInterest(),student.getAcademicLevel().getCode(), student.getProfession(), student.getLanguage());
                studentProfileService.updateScore(userId,student.getPoints(),student.getCommunityPoints(), student.getResourcePoints());
                return MainResponse.responseOk(student,"Student Successfully Updated");
            }
            return MainResponse.responseNotFound("Student Not Found");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> findMatches() {
        try{
            var userId = jWTUtils.extractUserId(learningPathService.getToken());
            if(studentRepository.findStudentByUserId(userId)!=null){
                List<String> matches =  aiInterface.findMatches(userId);
                List<Student> students = new ArrayList<>();
                for(String match : matches){
                    Student student = studentRepository.findStudentByUserId(match);
                    students.add(student);
                }
                return MainResponse.responseOk(students,"Matches Found");

            }
            return MainResponse.responseNotFound("Student Not Found");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getNotifications() {
        try{
            var userId = jWTUtils.extractUserId(learningPathService.getToken());
            List<Notification> notifications= notificationRepository.findNotificationsByUserId(userId);
            return MainResponse.responseOk(notifications,"Notifications Found");

        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }




}
