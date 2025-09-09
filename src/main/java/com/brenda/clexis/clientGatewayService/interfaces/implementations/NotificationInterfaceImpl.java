package com.brenda.clexis.clientGatewayService.interfaces.implementations;

import com.brenda.clexis.clientGatewayService.interfaces.interfaces.NotificationInterface;
import com.brenda.clexis.clientGatewayService.model.dto.NotificationRequest;
import com.brenda.clexis.clientGatewayService.model.dto.request.EmailRequestDto;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationInterfaceImpl implements NotificationInterface {

    private final WebClient webClient;
    private final Utils utils;

    @Value("${api.notification.sendCustomEmail")
    private String sendCustomEmailUrl;

    @Value("${api.notification.sendDefinedEmail}")
    private String sendDefinedEmail;

    @Override
    public void sendCustomEmail(NotificationRequest notificationRequest) {
        log.info("sending custom email  :: request: {}", notificationRequest);
            var res = webClient
                    .post()
                    .uri(sendCustomEmailUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(notificationRequest), NotificationRequest.class)
                    .retrieve()
                    .bodyToMono(ResponseDto.class)
                    .block();

            log.info("sending custom email :: response: {}", res);
    }

    @Override
    public void sendDefinedEmail(EmailRequestDto emailRequestDto) {
        log.info("sending defined email  :: request: {}", emailRequestDto);
        var res = webClient
                .post()
                .uri(sendCustomEmailUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(emailRequestDto), EmailRequestDto.class)
                .retrieve()
                .bodyToMono(ResponseDto.class)
                .block();

        log.info("sending defined email  :: response: {}", res);
    }

}
