package com.brenda.clexis.clientGatewayService.interfaces.implementations;

import com.brenda.clexis.clientGatewayService.interfaces.interfaces.NotificationInterface;
import com.brenda.clexis.clientGatewayService.model.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationInterfaceImpl implements NotificationInterface {
    @Override
    public void sendEmail(NotificationRequest notificationRequest) {

    }
}
