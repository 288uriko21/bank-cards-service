package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о банковской карте")
public class CardResponseDto {
	
	@Schema(description = "Идентификатор карты", example = "1")
    private Long id;
	
    @Schema(
    	    description = "Маскированный номер сгенерированной карты",
    	    example = "**** **** **** 0000"
    	)
    private String maskedNumber; 
    
    @Schema(
            description = "Срок действия карты",
            example = "2026-12-31"
        )
    private LocalDate expiryDate;
    
    @Schema(
            description = "Статус карты (ACTIVE, BLOCKED, EXPIRED, DELETED)",
            example = "ACTIVE"
        )
    private String status;
    
    @Schema(
            description = "Текущий баланс карты",
            example = "950.50"
        )
    private BigDecimal balance;
    
    @Schema(
            description = "ID владельца карты",
            example = "1"
        )
    private Long ownerId;
    
    @Schema(
            description = "Логин владельца карты",
            example = "alice"
        )
    private String ownerUsername;

    public CardResponseDto() {
    }

    public CardResponseDto(Long id,
                           String maskedNumber,
                           LocalDate expiryDate,
                           String status,
                           BigDecimal balance,
                           Long ownerId,
                           String ownerUsername) {
        this.id = id;
        this.maskedNumber = maskedNumber;
        this.expiryDate = expiryDate;
        this.status = status;
        this.balance = balance;
        this.ownerId = ownerId;
        this.ownerUsername = ownerUsername;
    }

    public Long getId() {
        return id;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
}
