package com.wallet.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.wallet.model.dto.response.TransactionDto;
import com.wallet.model.entity.Transaction;

@Component
public class TransactionUtil {

	private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

	public String getTimeString(LocalDateTime now) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

		return now.format(formatter);
	}

	public TransactionDto mapToTransactionDto(Transaction transaction) {

		return TransactionDto.builder().accountNumber(transaction.getAccount().getAccountNumber())
				.transactionAmount(transaction.getTransactionAmount().toPlainString())
				.transactionType(transaction.getTransactionType()).transactionId(transaction.getTransactionId())
				.transactionDate(getTimeString(transaction.getTransactionDate())).build();
	}
}
