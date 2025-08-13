package com.brenda.clexis.clientGatewayService.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AIResponseDto {
    private int code;
    private String message;
    private String error;
    private Object data;
}
