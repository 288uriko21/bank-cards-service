package com.example.bankcards.dto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на создание пользователя")
public class UserCreateRequest {

    private String username;
    private String password;
    private String role;

    public UserCreateRequest() {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

