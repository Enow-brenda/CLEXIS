package com.example.clexis.models.entity;



import com.example.clexis.models.application.Message;

import java.util.List;
import java.util.UUID;


public class Chatroom {

    private String id; //formed by concatenating the userIds
    private List<String> users;
    private Message lastMessage;
    private List<Message> messages;
}
