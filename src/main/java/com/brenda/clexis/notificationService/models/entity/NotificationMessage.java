package com.brenda.clexis.notificationService.models.entity;


import com.brenda.clexis.notificationService.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection = "messages")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class NotificationMessage {
    @Id
    private NotificationType id;
    private String headingEn;
    private String headingFr;
    private String messageEn;
    private String messageFr;
}
