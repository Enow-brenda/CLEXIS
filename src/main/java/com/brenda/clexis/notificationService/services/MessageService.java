package com.brenda.clexis.notificationService.services;


import com.brenda.clexis.notificationService.models.EmailRequestDto;
import com.brenda.clexis.notificationService.models.entity.Notification;
import com.brenda.clexis.notificationService.models.entity.NotificationMessage;
import com.brenda.clexis.notificationService.repository.NotificationMessageRepository;
import com.brenda.clexis.notificationService.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final NotificationMessageRepository notificationMessageRepository;
    private final NotificationRepository notificationRepository;


    public NotificationMessage saveNotificationMessage(NotificationMessage smsMessage){
        var message = notificationMessageRepository.save(smsMessage);
        log.info("message menu successful save+++++++");
        return message;
    }
    
    public Iterable<NotificationMessage> saveManyNotificationMessage(List<NotificationMessage> messages){
        var message = notificationMessageRepository.saveAll(messages);
        log.info("messages menu successful save+++++++");
        return message;
    }


    
    public void DeleteNotificationMessage(String smsId) {
        notificationMessageRepository.deleteById(smsId);
        log.info("sms message  id {} successful delete+++++++", smsId);

    }


    public List<NotificationMessage> getAllNotificationMessage() {
        var messages = notificationMessageRepository.findAll();
        List<NotificationMessage> result = new ArrayList<>(messages);
        log.info("datas: {}", result);
        return result;

    }


    public NotificationMessage getNotificationMessage(String id) {
        System.out.println(id);
        var message =  notificationMessageRepository.findById(id).orElse(null);
        log.info("message menu get is : {}", message);
        return message;
    }



    



    public List<Notification> getNotificationHistory() {
        return notificationRepository.findAll();
    }
}
