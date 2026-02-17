package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Элемент истории переводов по карте или пользователю")
public class TransactionHistoryItem {

    private Long id;
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private String status;
    private String message;
    private LocalDateTime createdAt;

    public TransactionHistoryItem() {
    }

    public TransactionHistoryItem(Long id,
                                  Long fromCardId,
                                  Long toCardId,
                                  BigDecimal amount,
                                  String status,
                                  String message,
                                  LocalDateTime createdAt) {
        this.id = id;
        this.fromCardId = fromCardId;
        this.toCardId = toCardId;
        this.amount = amount;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getFromCardId() {
        return fromCardId;
    }

    public Long getToCardId() {
        return toCardId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFromCardId(Long fromCardId) {
        this.fromCardId = fromCardId;
    }

    public void setToCardId(Long toCardId) {
        this.toCardId = toCardId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
