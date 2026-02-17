package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание банковской карты")
public class CardCreateRequest {
	
	@Schema(
		    description = "Номер карты (ровно 16 цифр)",
		    example = "0000000000000000"
		)
    @NotBlank(message = "Card number is required")
    @Size(min = 16, max = 16, message = "Card number must be exactly 16 digits")
    @Pattern(regexp = "\\d{16}", message = "Card number must be string whith 16 digits")
    private String cardNumber;
	
    @Schema(
            description = "Срок действия карты",
            example = "2028-12-31"
        )
    private LocalDate expiryDate;
    
    @Schema(
            description = "Начальный баланс карты",
            example = "1.00"
        )
    private BigDecimal initialBalance;
    
    @Schema(
            description = "ID владельца карты",
            example = "1"
        )
    private Long userId;

    public CardCreateRequest() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
