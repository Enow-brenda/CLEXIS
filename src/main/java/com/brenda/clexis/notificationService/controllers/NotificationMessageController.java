package com.brenda.clexis.notificationService.controllers;

import com.brenda.clexis.notificationService.models.entity.NotificationMessage;
import com.brenda.clexis.notificationService.models.response.MainResponse;
import com.brenda.clexis.notificationService.models.response.ResponseDto;
import com.brenda.clexis.notificationService.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/v1/notification/messages/")
public class NotificationMessageController {

    private final MessageService messageService;

    @GetMapping("getAll")
    public ResponseEntity<ResponseDto> getMessages() {
        log.info("request getting all notification messages...");
        try{
            var messageMenus = messageService.getAllNotificationMessage();
            if (messageMenus != null){
                return MainResponse.responseOk(messageMenus);
            }
            return MainResponse.responseNotFound("problem went getting all notification messages");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }

    }

    @GetMapping("/getHistory")
    public ResponseEntity<ResponseDto> getMessagesHistory() {
        log.info("request getting all notification sent messages history...");
        try{
            var messageMenus = messageService.getNotificationHistory();
            if (messageMenus != null){
                return MainResponse.responseOk(messageMenus);
            }
            return MainResponse.responseNotFound("problem when getting all notification messages history log");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }

    }


    @GetMapping("get/{id}")
    public ResponseEntity<ResponseDto>  getMessageById(@PathVariable String id) {
        log.info("request getting notification message: {} ...", id);
        try{
            var messageMenu = messageService.getNotificationMessage(id);
            return MainResponse.responseOk(messageMenu);
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }

    }

    @PostMapping("add")
    public  ResponseEntity<ResponseDto> saveMessage(@RequestBody NotificationMessage message) {
        log.info("request saving  message {}...",message);
        try{
            var messageSave = messageService.saveNotificationMessage(message);
            if (messageSave != null){
                return MainResponse.responseOk(messageSave);
            }
            return MainResponse.responseNotFound("notification message  failed saving");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }

    }

    @PostMapping("addMany")
    public ResponseEntity<ResponseDto> saveMessages(@RequestBody List<NotificationMessage> notificationMessages) {
        log.info("request saving  messages {}...", notificationMessages);
        try{
            var messageMenuSave = messageService.saveManyNotificationMessage(notificationMessages);
            if (messageMenuSave != null){
                return MainResponse.responseOk(messageMenuSave);
            }
            return MainResponse.responseNotFound("notification messages  failed saving");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }

    }

    @PutMapping("update")
    public ResponseEntity<ResponseDto> updateMessage(@RequestBody NotificationMessage notificationMessage) {
        log.info("request updating notification message");
        try{
            var newMessage = messageService.saveNotificationMessage(notificationMessage);
            if (newMessage != null){
                return MainResponse.responseOk(newMessage);
            }
            return MainResponse.responseNotFound("notification message failed updating");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<ResponseDto> deleteMessage(@PathVariable String id) {
        log.info("request delete notification message id: {}", id);
        try{
            messageService.DeleteNotificationMessage(id);
            return MainResponse.responseOk("successful delete menu id: "+id);
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }
}
