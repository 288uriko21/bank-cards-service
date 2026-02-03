package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CardResponseDto {

    private Long id;
    private String maskedNumber; // **** **** **** 1234
    private LocalDate expiryDate;
    private String status;
    private BigDecimal balance;
    private Long ownerId;
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
