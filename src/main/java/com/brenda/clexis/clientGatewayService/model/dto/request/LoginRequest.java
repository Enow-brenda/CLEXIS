package com.brenda.clexis.clientGatewayService.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoginRequest {
    private String usernameOrEmail;
    private String password;
}
