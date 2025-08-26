package com.example.clexis.models.response;

import java.util.Date;

public class LoginResponse {

    private String userId;
    private String username;
    private String email;
    private String token;
    private String refreshToken;
    private long loginCount;
    private Date expirationTime;
    private String role;

    public String getToken(){
        return this.token;
    }
    public String getUserId(){
        return this.userId;
    }
}
