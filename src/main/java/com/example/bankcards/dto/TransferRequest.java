package com.example.bankcards.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на перевод на другую свою карту")
public class TransferRequest {

    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;

    public TransferRequest() {
    }

    public Long getFromCardId() {
        return fromCardId;
    }

    public void setFromCardId(Long fromCardId) {
        this.fromCardId = fromCardId;
    }

    public Long getToCardId() {
        return toCardId;
    }

    public void setToCardId(Long toCardId) {
        this.toCardId = toCardId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
