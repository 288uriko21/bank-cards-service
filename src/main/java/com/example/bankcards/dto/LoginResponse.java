package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ на успешный логин с JWT токеном доступа")
public class LoginResponse {

    private String accessToken;
    private String tokenType = "Bearer";

    public LoginResponse() {
    }

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
