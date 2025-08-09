package com.brenda.clexis.notificationService.repository;


import com.brenda.clexis.notificationService.models.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findNotificationsByUserIdsContains(String userId);
}
