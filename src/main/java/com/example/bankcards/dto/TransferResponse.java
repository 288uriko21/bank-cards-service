package com.example.bankcards.dto;

import java.math.BigDecimal;

public class TransferResponse {

    private Long transactionId;
    private String status;
    private String message;
    private BigDecimal fromBalance;
    private BigDecimal toBalance;

    public TransferResponse() {
    }

    public TransferResponse(Long transactionId,
                            String status,
                            String message,
                            BigDecimal fromBalance,
                            BigDecimal toBalance) {
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
        this.fromBalance = fromBalance;
        this.toBalance = toBalance;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public BigDecimal getFromBalance() {
        return fromBalance;
    }

    public BigDecimal getToBalance() {
        return toBalance;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFromBalance(BigDecimal fromBalance) {
        this.fromBalance = fromBalance;
    }

    public void setToBalance(BigDecimal toBalance) {
        this.toBalance = toBalance;
    }
}
