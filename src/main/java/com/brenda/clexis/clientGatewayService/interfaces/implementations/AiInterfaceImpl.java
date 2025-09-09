package com.brenda.clexis.clientGatewayService.interfaces.implementations;

import com.brenda.clexis.clientGatewayService.interfaces.interfaces.AiInterface;
import com.brenda.clexis.clientGatewayService.model.dto.LearningAssetDto;


import com.brenda.clexis.clientGatewayService.model.dto.response.AIResponseDto;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AiInterfaceImpl implements AiInterface {

    private final WebClient webClient;
    private final Utils utils;

    @Value("${api.ai.generateResource}")
    private String generateResourceUrl;

    @Value("${api.ai.getStudentMatches}")
    private String findMatchUrl;

    @Override
    public Object generateResource(LearningAssetDto learningAssetDto) {
        int count = 0;
        int code = 500;
        AIResponseDto res = null;

        while (count < 2 && code != 200) {
            res = webClient
                    .post()
                    .uri(generateResourceUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(learningAssetDto), LearningAssetDto.class)
                    .retrieve()
                    .bodyToMono(AIResponseDto.class)
                    .block();

            log.info("AI making resource :: response: {}", res);

            if (res != null) {
                code = res.getCode();
            }

            count++;
        }

        if (res != null && res.getCode() == 200) {
            return res.getData();
        }

        return null;

    }

    @Override
    public List<String> findMatches(String userId) {
        var res = webClient
                .get()
                .uri(findMatchUrl)
                .retrieve()
                .bodyToMono(ResponseDto.class)
                .block();

        log.info("ai making resource :: response: {}", res);

        if (res != null && res.getMeta()!=null && res.getMeta().getStatusCode() == 200){
            return utils.convertListDataObjectFromHashMap(res.data, String.class);
        }
        return List.of();
    }
}
