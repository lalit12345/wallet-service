package com.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.wallet.exception.DuplicateTransactionException;
import com.wallet.exception.InvalidRequestDataException;
import com.wallet.model.dto.request.TransactionRequest;
import com.wallet.model.dto.response.TransactionDto;
import com.wallet.model.dto.response.TransactionResponse;
import com.wallet.model.entity.Account;
import com.wallet.model.entity.Transaction;
import com.wallet.repository.AccountRepository;
import com.wallet.repository.TransactionRepository;
import com.wallet.service.impl.TransactionServiceImpl;
import com.wallet.util.Util;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

	@InjectMocks
	private TransactionServiceImpl transactionServiceImpl;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private Util util;

	@BeforeEach
	public void init() {

		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void shouldThrowAnAccountDoesNotExistExceptionForInvalidAccountNumberForDebit() {

		TransactionRequest transactionRequest = TransactionRequest.builder().transactionId("TId10")
				.amount(new BigDecimal("100")).build();

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.empty());

		InvalidRequestDataException accountDoesNotExistException = assertThrows(InvalidRequestDataException.class, () -> {
			transactionServiceImpl.performDebit("123456", transactionRequest);
		});

		assertEquals("Account does not exist with accountNumber: 123456", accountDoesNotExistException.getMessage());
	}

	@Test
	public void shouldThrowDuplicateTransactionExceptionForSameTransactionIdForDebit() {

		TransactionRequest transactionRequest = TransactionRequest.builder().transactionId("TId10")
				.amount(new BigDecimal("100")).build();

		Account account = Account.builder().accountNumber("123456").emailId("test1@test1.com")
				.balanceAmount(new BigDecimal(100)).fullName("Test1 Test1").mobileNumber("1111111111")
				.accountType("SAVINGS").accountStatus("ACTIVE").build();
		Transaction transaction = Transaction.builder().transactionId("TId10").build();

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.of(account));
		when(transactionRepository.findByTransactionId(anyString())).thenReturn(Optional.of(transaction));

		DuplicateTransactionException duplicateTransactionException = assertThrows(DuplicateTransactionException.class,
				() -> {
					transactionServiceImpl.performDebit("123456", transactionRequest);
				});

		assertEquals("Transaction was already performed with transactionId: TId10",
				duplicateTransactionException.getMessage());
	}

	@Test
	public void shouldThrowInsufficientFundsExceptionWhenThereAreNoEnoughFundsAvailable() {

		TransactionRequest transactionRequest = TransactionRequest.builder().transactionId("TId10")
				.amount(new BigDecimal("110")).build();

		Account account = Account.builder().accountNumber("123456").emailId("test1@test1.com")
				.balanceAmount(new BigDecimal(100)).fullName("Test1 Test1").mobileNumber("1111111111")
				.accountType("SAVINGS").accountStatus("ACTIVE").build();

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.of(account));
		when(transactionRepository.findByTransactionId(anyString())).thenReturn(Optional.empty());

		InvalidRequestDataException insufficientFundException = assertThrows(InvalidRequestDataException.class, () -> {
			transactionServiceImpl.performDebit("123456", transactionRequest);
		});

		assertEquals("There are insufficient funds in your account. Please provide different amount.",
				insufficientFundException.getMessage());

	}

	@Test
	public void shouldDebitTheAmountSuccessfully() {

		TransactionRequest transactionRequest = TransactionRequest.builder().transactionId("TId10")
				.amount(new BigDecimal("10")).build();

		Account account = Account.builder().accountNumber("123456").emailId("test1@test1.com")
				.balanceAmount(new BigDecimal(100)).fullName("Test1 Test1").mobileNumber("1111111111")
				.accountType("SAVINGS").accountStatus("ACTIVE").build();

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenReturn(account);
		when(transactionRepository.findByTransactionId(anyString())).thenReturn(Optional.empty());

		TransactionDto transactionDto = transactionServiceImpl.performDebit("123456", transactionRequest);

		assertEquals("Account debited successfully", transactionDto.getMessage());
		assertEquals("90", transactionDto.getAccountBalance());
		assertEquals("123456", transactionDto.getAccountNumber());
		assertEquals("DEBIT", transactionDto.getTransactionType());
	}

	@Test
	public void shouldThrowAnAccountDoesNotExistExceptionForInvalidAccountNumberForCredit() {

		TransactionRequest transactionRequest = TransactionRequest.builder().transactionId("TId10")
				.amount(new BigDecimal("100")).build();

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.empty());

		InvalidRequestDataException accountDoesNotExistException = assertThrows(InvalidRequestDataException.class, () -> {
			transactionServiceImpl.performCredit("123456", transactionRequest);
		});

		assertEquals("Account does not exist with accountNumber: 123456", accountDoesNotExistException.getMessage());
	}

	@Test
	public void shouldThrowDuplicateTransactionExceptionForSameTransactionIdForCredit() {

		TransactionRequest transactionRequest = TransactionRequest.builder().transactionId("TId10")
				.amount(new BigDecimal("100")).build();

		Account account = Account.builder().accountNumber("123456").emailId("test1@test1.com")
				.balanceAmount(new BigDecimal(100)).fullName("Test1 Test1").mobileNumber("1111111111")
				.accountType("SAVINGS").accountStatus("ACTIVE").build();
		Transaction transaction = Transaction.builder().transactionId("TId10").build();

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.of(account));
		when(transactionRepository.findByTransactionId(anyString())).thenReturn(Optional.of(transaction));

		DuplicateTransactionException duplicateTransactionException = assertThrows(DuplicateTransactionException.class,
				() -> {
					transactionServiceImpl.performCredit("123456", transactionRequest);
				});

		assertEquals("Transaction was already performed with transactionId: TId10",
				duplicateTransactionException.getMessage());
	}

	@Test
	public void shouldCreditTheAmountSuccessfully() {

		TransactionRequest transactionRequest = TransactionRequest.builder().transactionId("TId10")
				.amount(new BigDecimal("10")).build();

		Account account = Account.builder().accountNumber("123456").emailId("test1@test1.com")
				.balanceAmount(new BigDecimal(100)).fullName("Test1 Test1").mobileNumber("1111111111")
				.accountType("SAVINGS").accountStatus("ACTIVE").build();

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenReturn(account);
		when(transactionRepository.findByTransactionId(anyString())).thenReturn(Optional.empty());

		TransactionDto transactionDto = transactionServiceImpl.performCredit("123456", transactionRequest);

		assertEquals("Account credited successfully", transactionDto.getMessage());
		assertEquals("110", transactionDto.getAccountBalance());
		assertEquals("123456", transactionDto.getAccountNumber());
		assertEquals("CREDIT", transactionDto.getTransactionType());
	}

	@Test
	public void shouldThrowAnAccountDoesNotExistExceptionForInvalidAccountNumberWhenGettingAllTransactions() {

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.empty());

		InvalidRequestDataException accountDoesNotExistException = assertThrows(InvalidRequestDataException.class, () -> {
			transactionServiceImpl.getAllTransactions("123456", 0, 10);
		});

		assertEquals("Account does not exist with accountNumber: 123456", accountDoesNotExistException.getMessage());
	}

	@Test
	public void shouldReturnPageNumberExceededResponseWhenRequestedPageNumberExceedsTotalPages() {

		Account account = Account.builder().accountNumber("123456").emailId("test1@test1.com")
				.balanceAmount(new BigDecimal(100)).fullName("Test1 Test1").mobileNumber("1111111111")
				.accountType("SAVINGS").accountStatus("ACTIVE").build();

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.of(account));

		List<Transaction> transactions = new ArrayList<>();
		Transaction transaction1 = Transaction.builder().transactionId("TId10").account(account)
				.transactionAmount(new BigDecimal(10)).transactionType("DEBIT").build();
		Transaction transaction2 = Transaction.builder().transactionId("TId11").account(account)
				.transactionAmount(new BigDecimal(20)).transactionType("CREDIT").build();
		transactions.add(transaction1);
		transactions.add(transaction2);

		Page<Transaction> page = new PageImpl<>(transactions);
		when(transactionRepository.findAllByAccount(any(), any())).thenReturn(page);

		TransactionResponse transactionResponse = transactionServiceImpl.getAllTransactions("123456", 2, 1);

		assertEquals("Requested page exceeds total number of pages: 1", transactionResponse.getMessage());
		assertEquals(1, transactionResponse.getTotalNoOfPages());
		assertEquals(2, transactionResponse.getTotalNoOfTransactions());
		assertEquals(0, transactionResponse.getTransactions().size());
	}

	@Test
	public void shouldReturnEmptyTransactionResponseWhenThereAreNoTransactions() {

		Account account = Account.builder().accountNumber("123456").emailId("test1@test1.com")
				.balanceAmount(new BigDecimal(100)).fullName("Test1 Test1").mobileNumber("1111111111")
				.accountType("SAVINGS").accountStatus("ACTIVE").build();

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.of(account));

		Page<Transaction> page = new PageImpl<>(Collections.emptyList());
		when(transactionRepository.findAllByAccount(any(), any())).thenReturn(page);

		TransactionResponse transactionResponse = transactionServiceImpl.getAllTransactions("123456", 0, 1);

		assertEquals("There were no transations performed on accountNumber: 123456", transactionResponse.getMessage());
		assertEquals(1, transactionResponse.getTotalNoOfPages());
		assertEquals(0, transactionResponse.getTotalNoOfTransactions());
		assertEquals(0, transactionResponse.getTransactions().size());
	}

	@Test
	public void shouldReturnSuccessResponseWhenRequestIsValid() {

		Account account = Account.builder().accountNumber("123456").emailId("test1@test1.com")
				.balanceAmount(new BigDecimal(100)).fullName("Test1 Test1").mobileNumber("1111111111")
				.accountType("SAVINGS").accountStatus("ACTIVE").build();

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.of(account));

		List<Transaction> transactions = new ArrayList<>();
		Transaction transaction1 = Transaction.builder().transactionId("TId10").account(account)
				.transactionAmount(new BigDecimal(10)).transactionType("DEBIT").build();
		Transaction transaction2 = Transaction.builder().transactionId("TId11").account(account)
				.transactionAmount(new BigDecimal(20)).transactionType("CREDIT").build();
		transactions.add(transaction1);
		transactions.add(transaction2);

		Page<Transaction> page = new PageImpl<>(transactions);
		when(transactionRepository.findAllByAccount(any(), any())).thenReturn(page);

		TransactionResponse transactionResponse = transactionServiceImpl.getAllTransactions("123456", 0, 1);

		assertEquals("Success", transactionResponse.getMessage());
		assertEquals(1, transactionResponse.getTotalNoOfPages());
		assertEquals(2, transactionResponse.getTotalNoOfTransactions());
		assertEquals(2, transactionResponse.getTransactions().size());
	}
}
