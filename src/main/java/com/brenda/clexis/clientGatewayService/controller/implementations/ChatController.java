package com.brenda.clexis.clientGatewayService.controller.implementations;

import com.brenda.clexis.clientGatewayService.model.dto.application.Message;
import com.brenda.clexis.clientGatewayService.model.dto.response.MainResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Chatroom;
import com.brenda.clexis.clientGatewayService.repository.ChatroomRepository;
import com.brenda.clexis.clientGatewayService.service.interfaces.LearningPathService;
import com.brenda.clexis.clientGatewayService.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/chats")
public class ChatController {

    @Autowired
    private ChatroomRepository chatroomRepository;
    @Autowired
    private LearningPathService learningPathService;
    @Autowired
    private JWTUtils jWTUtils;

    @MessageMapping("/chat.send/{chatroomId}")
    @SendTo("/topic/messages/{chatroomId}")
    public Message sendMessage(@DestinationVariable String chatroomId, Message message) {
        message.setTimeSent(System.currentTimeMillis());

        //chatroomId is the receiverId when its a new chatroom
        Chatroom chatroom = chatroomRepository.findById(chatroomId).orElseGet(() -> {
            String userId = jWTUtils.extractUserId(learningPathService.getToken());
            String id = userId+"_"+chatroomId;
            List<Message> messages = new ArrayList<>();
            messages.add(message);
            List<String> userIds = new ArrayList<>();
            userIds.add(userId);
            userIds.add(chatroomId);

            Chatroom newRoom = new Chatroom();
            newRoom.setId(id);
            newRoom.setLastMessage(message);
            newRoom.setUsers(userIds);
            newRoom.setMessages(messages);
            return chatroomRepository.save(newRoom);
        });

        chatroom.getMessages().add(message);
        chatroom.setLastMessage(message);
        chatroomRepository.save(chatroom);

        return message; // sent to subscribers
    }


    @GetMapping("/chatrooms")
    public ResponseEntity<ResponseDto> getChatrooms() {
        try{
            String userId = jWTUtils.extractUserId(learningPathService.getToken());
            return MainResponse.responseOk(chatroomRepository.findChatroomsByUsersContaining(userId));
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }

    }

    @GetMapping("/chatrooms/{id}")
    public ResponseEntity<ResponseDto> getChatroom(@PathVariable String id) {
        try{
            if(chatroomRepository.findById(id).isPresent()){
                return MainResponse.responseOk(chatroomRepository.findById(id).get());
            }
            return MainResponse.responseNotFound("Chatroom not found");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }

    }

    @DeleteMapping("/chatrooms/{id}")
    public ResponseEntity<ResponseDto> deleteChatroom(@PathVariable String id) {
        try{
            if(chatroomRepository.findById(id).isPresent()){
                chatroomRepository.deleteById(id);
                return MainResponse.responseOk("Chatroom deleted successfully");
            }
            return MainResponse.responseNotFound("Chatroom not found");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }

    }


}
