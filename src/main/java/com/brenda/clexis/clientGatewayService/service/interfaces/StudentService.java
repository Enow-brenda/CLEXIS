package com.brenda.clexis.clientGatewayService.service.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.NotificationRequest;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Student;
import org.springframework.http.ResponseEntity;

public interface StudentService {
    ResponseEntity<ResponseDto> notifyStudents(NotificationRequest notificationRequest);

    ResponseEntity<ResponseDto> getStudentInfo(String userId);

    ResponseEntity<ResponseDto> updateStudent(Student student);


    ResponseEntity<ResponseDto> findMatches();

    ResponseEntity<ResponseDto> getNotifications();

    ResponseEntity<ResponseDto> getStudentProfile(String userId);

    ResponseEntity<ResponseDto> getALLStudentProfile();
}
