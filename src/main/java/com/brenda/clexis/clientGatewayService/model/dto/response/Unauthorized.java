package com.brenda.clexis.clientGatewayService.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Unauthorized {
    private String path;
    private String error;
    private String message;
    private int status;
}
