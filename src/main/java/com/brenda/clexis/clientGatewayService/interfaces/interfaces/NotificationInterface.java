package com.brenda.clexis.clientGatewayService.interfaces.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.NotificationRequest;

public interface NotificationInterface {

    void sendEmail(NotificationRequest notificationRequest);
}
