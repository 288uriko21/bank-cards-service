package com.example.bankcards.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "from_card_id")
	private CardEntity fromCard;

	@ManyToOne(optional = false)
	@JoinColumn(name = "to_card_id")
	private CardEntity toCard;

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false, length = 20)
	private String status; // SUCCESS, FAILED

	@Column(length = 255)
	private String message;

	@Column(nullable = false, length = 20)
	private String type; // INTERNAL, EXTERNAL

	public TransactionEntity() {
	}

	public TransactionEntity(CardEntity fromCard, CardEntity toCard, BigDecimal amount, String status, String message,
			String type) {
		this.fromCard = fromCard;
		this.toCard = toCard;
		this.amount = amount;
		this.status = status;
		this.message = message;
		this.type = type;
		this.createdAt = LocalDateTime.now();
	}



	public Long getId() {
		return id;
	}

	public CardEntity getFromCard() {
		return fromCard;
	}

	public void setFromCard(CardEntity fromCard) {
		this.fromCard = fromCard;
	}

	public CardEntity getToCard() {
		return toCard;
	}

	public void setToCard(CardEntity toCard) {
		this.toCard = toCard;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getType() {
	    return type;
	}

	public void setType(String type) {
	    this.type = type;
	}
}
