package com.example.clexis.models.entity;


import com.example.clexis.models.enums.NotificationType;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Notification {

    private String id;
    private String title;
    private String message;
    private NotificationType type;
    private String receiverEmail;
    private LocalDateTime timestamp;
    private boolean read;
    private String userId;

    public Notification(String n1, String newTaskAssigned, String s, NotificationType notificationType, String mail, LocalDateTime of, String user123) {
        id = n1;
        title = newTaskAssigned;
        message = s;
        type = notificationType;
        timestamp= of;
        receiverEmail = mail;
        userId=user123;
        read = true;
    }
}
