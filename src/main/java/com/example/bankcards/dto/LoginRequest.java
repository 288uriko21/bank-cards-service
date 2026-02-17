package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на вход в аккаунт, получение токена")
public class LoginRequest {

    private String username;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
