package com.brenda.clexis.clientGatewayService.repository;

import com.brenda.clexis.clientGatewayService.model.entity.Chatroom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatroomRepository extends MongoRepository<Chatroom, String> {
    List<Chatroom> findChatroomsByUsersContaining(String userId);
}
