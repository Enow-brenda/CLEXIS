package com.brenda.clexis.clientGatewayService.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class AuthenticationResponse {
    private String userId;
    private String username;
    private String email;
    private String token;
    private String refreshToken;
    private long loginCount;
    private Date expirationTime;
    private String role;
}
