package com.brenda.clexis.clientGatewayService.repository;

import com.brenda.clexis.clientGatewayService.model.entity.Chatroom;
import com.brenda.clexis.clientGatewayService.model.entity.Resource;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatroomRepository extends MongoRepository<Chatroom, String> {
}
