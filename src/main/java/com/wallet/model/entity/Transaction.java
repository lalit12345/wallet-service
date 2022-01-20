package com.wallet.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "account_number", referencedColumnName = "account_number")
	private Account account;

	@Column(name = "transaction_id", nullable = false)
	private String transactionId;

	@Column(name = "transaction_type", nullable = false)
	private String transactionType;

	@Column(name = "transaction_amount", nullable = false)
	private BigDecimal transactionAmount;

	@Column(name = "transaction_date", nullable = false)
	private LocalDateTime transactionDate;
}
