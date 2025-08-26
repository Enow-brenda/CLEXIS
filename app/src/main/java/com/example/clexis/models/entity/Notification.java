package com.example.clexis.models.entity;


import com.example.clexis.models.enums.NotificationType;

import java.time.LocalDateTime;

public class Notification {

    private String id;
    private String title;
    private String message;
    private NotificationType type;
    private String receiverEmail;
    private LocalDateTime timestamp;
    private String userId;
}
