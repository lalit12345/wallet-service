package com.wallet.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import com.wallet.model.AccountStatus;
import com.wallet.model.AccountType;
import com.wallet.model.TransactionType;
import com.wallet.model.entity.Account;
import com.wallet.model.entity.Transaction;

@DataJpaTest
@TestInstance(Lifecycle.PER_CLASS)
public class AccountRepositoryTest {

	@Autowired
	private AccountRepository accountRepository;

	private Account account;

	private Transaction transaction;

	@BeforeAll
	public void initializeData() {

		account = Account.builder().accountNumber("12345").accountStatus(AccountStatus.ACTIVE.name())
				.accountType(AccountType.SAVINGS.name()).balanceAmount(new BigDecimal(100)).emailId("test1@test1.com")
				.fullName("Test1 Test1").mobileNumber("1111111111").build();

		List<Transaction> transactions = new ArrayList<>();
		transaction = Transaction.builder().account(account).transactionAmount(new BigDecimal(10))
				.transactionDate(LocalDateTime.now()).transactionId("TId1")
				.transactionType(TransactionType.DEBIT.name()).build();
		transactions.add(transaction);

		account.setTransactions(transactions);
		accountRepository.save(account);
	}

	@Test
	public void shouldReturnAnAccountByAccountNumberIfStatusIsActive() {

		Optional<Account> optionalAccount = accountRepository.findByAccountNumberAndAccountStatus("12345",
				AccountStatus.ACTIVE.name());

		Account createdAccount = optionalAccount.get();

		assertTrue(optionalAccount.isPresent());
		assertEquals("test1@test1.com", createdAccount.getEmailId());
		assertEquals("Test1 Test1", createdAccount.getFullName());
		assertEquals("1111111111", createdAccount.getMobileNumber());
		assertEquals("12345", createdAccount.getAccountNumber());
		assertEquals("ACTIVE", createdAccount.getAccountStatus());
		assertEquals("100.00", createdAccount.getBalanceAmount().toPlainString());
		assertEquals(1, createdAccount.getTransactions().size());
	}

	@Test
	public void shouldNotReturnAnAccountByAccountNumberIfStatusIsInactive() {

		account.setAccountStatus(AccountStatus.INACTIVE.name());
		accountRepository.save(account);

		Optional<Account> optionalAccount = accountRepository.findByAccountNumberAndAccountStatus("1234",
				AccountStatus.ACTIVE.name());

		assertFalse(optionalAccount.isPresent());
	}
}
