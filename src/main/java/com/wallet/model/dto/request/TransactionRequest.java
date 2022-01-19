package com.wallet.model.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

	@NotBlank(message = "{accountNumber.not-blank}")
	private String accountNumber;

	@NotBlank(message = "{transactionId.not-blank}")
	@Size(max = 20, message = "{transactionId.size}")
	private String transactionId;

	@DecimalMin(value = "1", message = "{amount.min}")
	private BigDecimal amount;
}
