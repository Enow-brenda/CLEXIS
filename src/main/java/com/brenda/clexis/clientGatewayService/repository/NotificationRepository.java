package com.brenda.clexis.clientGatewayService.repository;

import com.brenda.clexis.clientGatewayService.model.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findNotificationsByUserId(String userId);
}
