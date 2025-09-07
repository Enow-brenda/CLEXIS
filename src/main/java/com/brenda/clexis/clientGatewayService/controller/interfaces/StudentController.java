package com.brenda.clexis.clientGatewayService.controller.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.NotificationRequest;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Student;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/gateway/student/students/")
public interface StudentController {

    //notify student
    @PostMapping("notify")
    ResponseEntity<ResponseDto> notifyStudent(@RequestBody NotificationRequest notificationRequest);
    //get the student info using userId
    @GetMapping("getInfo/{userId}")
    ResponseEntity<ResponseDto> getStudentInfo(@PathVariable String userId);

    @PutMapping("info/update")
    ResponseEntity<ResponseDto> updateStudentInfo(@RequestBody Student student);

    //AI TO get the matches of student like recommendations
    @GetMapping("getMatch")
    ResponseEntity<ResponseDto> getStudentMatch();

    @GetMapping("notifications")
    ResponseEntity<ResponseDto> getStudentNotifications();


}
