package com.brenda.clexis.clientGatewayService.interfaces.implementations;

import com.brenda.clexis.clientGatewayService.interfaces.interfaces.AiInterface;
import com.brenda.clexis.clientGatewayService.model.dto.LearningAssetDto;


import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class AiInterfaceImpl implements AiInterface {

    private final WebClient webClient;

    @Value("{api.ai.generateResource}")
    private String generateResourceUrl;

    @Override
    public Object generateResource(LearningAssetDto learningAssetDto) {
        var res = webClient
                .post()
                .uri(generateResourceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(learningAssetDto), LearningAssetDto.class)
                .retrieve()
                .bodyToMono(ResponseDto.class)
                .block();

        log.info("ai making resource :: response: {}", res);

        if (res != null && res.getMeta()!=null && res.getMeta().getStatusCode() == 200){
            return res.getData();
        }
        return null;
    }
}
