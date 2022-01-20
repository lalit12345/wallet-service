package com.wallet.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDto {

	private String message;

	private String accountNumber;

	private String accountBalance;

	private String transactionId;

	private String transactionType;

	private String transactionDate;

	private String transactionAmount;
}
