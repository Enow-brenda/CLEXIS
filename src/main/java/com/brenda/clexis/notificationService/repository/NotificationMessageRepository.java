package com.brenda.clexis.notificationService.repository;

import com.brenda.clexis.notificationService.models.entity.NotificationMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationMessageRepository extends MongoRepository<NotificationMessage,String>{
   NotificationMessage findNotificationMessageById(String id);
}
