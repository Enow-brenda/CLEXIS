package com.brenda.clexis.clientGatewayService.interfaces.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.NotificationRequest;
import com.brenda.clexis.clientGatewayService.model.dto.request.EmailRequestDto;

public interface NotificationInterface {

    void sendCustomEmail(NotificationRequest notificationRequest);

    void sendDefinedEmail(EmailRequestDto emailRequestDto);
}
