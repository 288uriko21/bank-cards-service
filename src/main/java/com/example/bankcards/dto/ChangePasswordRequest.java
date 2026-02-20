package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на смену пароля текущего пользователя")
public class ChangePasswordRequest {

    @Schema(
        description = "Текущий пароль пользователя",
        example = "OldPassword",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @Schema(
        description = "Новый пароль пользователя",
        example = "NewPassword",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "New password is required")
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
