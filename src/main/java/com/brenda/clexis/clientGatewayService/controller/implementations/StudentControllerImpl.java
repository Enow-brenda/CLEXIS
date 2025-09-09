package com.brenda.clexis.clientGatewayService.controller.implementations;

import com.brenda.clexis.clientGatewayService.controller.interfaces.StudentController;
import com.brenda.clexis.clientGatewayService.model.dto.NotificationRequest;
import com.brenda.clexis.clientGatewayService.model.dto.request.ChangePasswordRequest;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Student;
import com.brenda.clexis.clientGatewayService.service.interfaces.AuthService;
import com.brenda.clexis.clientGatewayService.service.interfaces.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class StudentControllerImpl implements StudentController {

    private final StudentService studentService;
    private final AuthService authService;

    @Override
    public ResponseEntity<ResponseDto> notifyStudent(NotificationRequest notificationRequest) {
        log.info("notifying students: {}", notificationRequest);
        return studentService.notifyStudents(notificationRequest);
    }

    @Override
    public ResponseEntity<ResponseDto> getStudentInfo(String userId) {
        log.info("getting student info: {}", userId);
        return studentService.getStudentInfo(userId);
    }

    @Override
    public ResponseEntity<ResponseDto> getStudentProfile(String userId) {
        log.info("getting student profile: {}", userId);
        return studentService.getStudentProfile(userId);
    }

    @Override
    public ResponseEntity<ResponseDto> updateStudentInfo(Student student) {
        log.info("updating student info: {}", student);
        return studentService.updateStudent(student);
    }

    @Override
    public ResponseEntity<ResponseDto> getStudentMatch() {
        log.info("getting student match");
        return studentService.findMatches();
    }

    @Override
    public ResponseEntity<ResponseDto> changePassword(ChangePasswordRequest changePasswordRequest) {
        log.info("change password request : {} ...", changePasswordRequest);
        return authService.changePassword(changePasswordRequest);

    }

    @Override
    public ResponseEntity<ResponseDto> getStudentNotifications() {
        log.info("getting student notification");
        return studentService.getNotifications();
    }
}
