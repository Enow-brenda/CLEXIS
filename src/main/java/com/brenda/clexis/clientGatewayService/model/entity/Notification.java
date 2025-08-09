package com.brenda.clexis.clientGatewayService.model.entity;

import com.brenda.clexis.clientGatewayService.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id = UUID.randomUUID().toString();
    private String title;
    private String message;
    private NotificationType type;
    private LocalDateTime timestamp;
    private List<String> userIds;
}
