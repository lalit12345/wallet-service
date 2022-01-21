package com.wallet.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.wallet.model.AccountStatus;
import com.wallet.model.AccountType;
import com.wallet.model.TransactionType;
import com.wallet.model.entity.Account;
import com.wallet.model.entity.Transaction;

@DataJpaTest
@TestInstance(Lifecycle.PER_CLASS)
public class TransactionRepositoryTest {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	private Account account;

	private Transaction transaction1;
	private Transaction transaction2;

	@BeforeAll
	public void initializeData() {

		account = Account.builder().accountNumber("1234").accountStatus(AccountStatus.ACTIVE.name())
				.accountType(AccountType.SAVINGS.name()).balanceAmount(new BigDecimal(100)).emailId("test3@test1.com")
				.fullName("Test1 Test1").mobileNumber("1111711111").build();

		List<Transaction> transactions = new ArrayList<>();
		transaction1 = Transaction.builder().account(account).transactionAmount(new BigDecimal(10))
				.transactionDate(LocalDateTime.now()).transactionId("TId3")
				.transactionType(TransactionType.DEBIT.name()).build();
		transaction2 = Transaction.builder().account(account).transactionAmount(new BigDecimal(10))
				.transactionDate(LocalDateTime.now()).transactionId("TId4")
				.transactionType(TransactionType.CREDIT.name()).build();
		transactions.add(transaction1);
		transactions.add(transaction2);

		account.setTransactions(transactions);
		accountRepository.save(account);
	}

	@Test
	public void shouldReturnTransactionEntityByTransactionId() {

		Optional<Transaction> optionalTransaction = transactionRepository.findByTransactionId("TId3");

		assertTrue(optionalTransaction.isPresent());
		assertEquals("10.00", optionalTransaction.get().getTransactionAmount().toPlainString());
		assertEquals("TId3", optionalTransaction.get().getTransactionId());
		assertEquals("DEBIT", optionalTransaction.get().getTransactionType());
		assertEquals("1234", optionalTransaction.get().getAccount().getAccountNumber());
	}

	@Test
	public void shouldReturnPageTransactionEntityByAccount() {

		Pageable pageable = PageRequest.of(0, 2);
		Page<Transaction> page = transactionRepository.findAllByAccount(account, pageable);

		assertEquals(2, page.getContent().size());
		assertEquals(1, page.getTotalPages());
	}
}
