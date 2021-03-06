package com.wallet.model.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TransactionResponse {

	private int status;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<TransactionDto> transactions;
	private String message;
	private int totalNoOfPages;
	private int totalNoOfTransactions;
}
