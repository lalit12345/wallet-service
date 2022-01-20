package com.wallet.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.wallet.model.dto.response.TransactionDto;
import com.wallet.model.entity.Account;
import com.wallet.model.entity.Transaction;

public class UtilTest {

	@Test
	public void shouldReturnStringValueForGivenDateTime() {

		Util util = new Util();

		assertEquals("20-01-2021 19:00:00", util.getTimeString(LocalDateTime.of(2021, 01, 20, 19, 00, 00)));
	}

	@Test
	public void shouldMapToTransactionDtoForGivenTransaction() {

		Util util = new Util();

		Account account = Account.builder().accountNumber("123456").emailId("test1@test1.com")
				.balanceAmount(new BigDecimal(100)).fullName("Test1 Test1").mobileNumber("1111111111")
				.accountType("SAVINGS").accountStatus("ACTIVE").build();

		Transaction transaction = Transaction.builder().transactionId("TId10").transactionAmount(new BigDecimal(10))
				.transactionType("DEBIT").transactionDate(LocalDateTime.now()).account(account).build();

		TransactionDto transactionDto = util.mapToTransactionDto(transaction);

		assertEquals("DEBIT", transactionDto.getTransactionType());
		assertEquals("10", transactionDto.getTransactionAmount());
		assertEquals("TId10", transactionDto.getTransactionId());
		assertEquals("123456", transactionDto.getAccountNumber());
	}
}
