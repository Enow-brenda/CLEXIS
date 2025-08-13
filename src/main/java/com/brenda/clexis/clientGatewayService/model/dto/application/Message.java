package com.brenda.clexis.clientGatewayService.model.dto.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    private String senderId;
    private String message;
    private long timeSent;
    private long timeRead;
    private boolean read;
}
