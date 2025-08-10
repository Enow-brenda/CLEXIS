package com.brenda.clexis.notificationService.controllers;


import com.brenda.clexis.notificationService.models.EmailRequestDto;
import com.brenda.clexis.notificationService.models.dto.CustomEmailRequestDto;
import com.brenda.clexis.notificationService.models.response.MainResponse;
import com.brenda.clexis.notificationService.models.response.ResponseDto;
import com.brenda.clexis.notificationService.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/v1/notification/")
public class NotificationController {
    private final EmailService emailService;

    @PostMapping("sendEmail/custom")
    public ResponseEntity<ResponseDto> sendCustomNotification(@RequestBody CustomEmailRequestDto customEmailRequestDto) {
        log.info("custom request email is: {}", customEmailRequestDto);
        try {
            emailService.sendCustomNotification(customEmailRequestDto);
            return MainResponse.responseOk(null);
        }catch (Exception e){
            log.error("exception error: {}", e.getMessage());
            return MainResponse.responseError(e.getMessage());
        }
    }

    @PostMapping("sendEmail/defined")
    public ResponseEntity<ResponseDto> sendDefinedEmail(@RequestBody  EmailRequestDto emailRequestDto) {
        log.info("request defined email is: {}", emailRequestDto);
        try {
            emailService.sendDefinedEmail(emailRequestDto);
            return MainResponse.responseOk(null);
        }catch (Exception e){
            log.error("exception error: {}", e.getMessage());
            return MainResponse.responseError(e.getMessage());
        }
    }

}
