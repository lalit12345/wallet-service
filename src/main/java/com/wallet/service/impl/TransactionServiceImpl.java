package com.wallet.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wallet.exception.AccountNotFoundException;
import com.wallet.exception.DuplicateTransactionException;
import com.wallet.exception.InsufficientFundException;
import com.wallet.model.TransactionType;
import com.wallet.model.dto.request.TransactionRequest;
import com.wallet.model.dto.response.TransactionResponse;
import com.wallet.model.entity.Account;
import com.wallet.model.entity.Transaction;
import com.wallet.repository.AccountRepository;
import com.wallet.repository.TransactionRepository;
import com.wallet.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public TransactionResponse performDebit(String accountNumber, TransactionRequest transactionRequest) {

		// Check account exists
		Optional<Account> optionalAccount = checkAccountExists(accountNumber);

		// Check duplicate transaction
		checkDuplicateTransaction(transactionRequest.getTransactionId());

		Account account = optionalAccount.get();

		// Check if sufficient funds are available
		BigDecimal remainingBalance = validateBalance(transactionRequest.getAmount(), account.getBalanceAmount());
		account.setBalanceAmount(remainingBalance);

		// Create transaction record
		LocalDateTime now = LocalDateTime.now();
		List<Transaction> transactions = createTransaction(transactionRequest.getTransactionId(), now,
				TransactionType.DEBIT.name(), account);

		account.setTransactions(transactions);
		Account savedAccount = accountRepository.save(account);

		return TransactionResponse.builder().message("Account debited successfully")
				.accountNumber(savedAccount.getAccountNumber())
				.accountBalance(savedAccount.getBalanceAmount().toPlainString())
				.transactionDate(getCurrentTimeString(now)).build();
	}

	@Override
	public TransactionResponse performCredit(String accountNumber, TransactionRequest transactionRequest) {

		// Check account exists
		Optional<Account> optionalAccount = checkAccountExists(accountNumber);

		// Check duplicate transaction
		checkDuplicateTransaction(transactionRequest.getTransactionId());

		Account account = optionalAccount.get();
		account.setBalanceAmount(account.getBalanceAmount().add(transactionRequest.getAmount()));

		// Create transaction record
		LocalDateTime now = LocalDateTime.now();
		List<Transaction> transactions = createTransaction(transactionRequest.getTransactionId(), now,
				TransactionType.CREDIT.name(), account);

		account.setTransactions(transactions);
		Account savedAccount = accountRepository.save(account);

		return TransactionResponse.builder().message("Account credited successfully")
				.accountNumber(savedAccount.getAccountNumber())
				.accountBalance(savedAccount.getBalanceAmount().toPlainString())
				.transactionDate(getCurrentTimeString(now)).build();
	}

	private Optional<Account> checkAccountExists(String accountNumber) {

		Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);

		if (optionalAccount.isEmpty()) {

			log.error("Account not found with accountNumber: {}", accountNumber);
			throw new AccountNotFoundException(
					String.format("Account not found with accountNumber: %s", accountNumber));
		}
		return optionalAccount;
	}

	private void checkDuplicateTransaction(String transactionId) {

		Optional<Transaction> optionalTransaction = transactionRepository.findByTransactionId(transactionId);

		if (optionalTransaction.isPresent()) {

			log.error("Transaction was already performed with transactionId: {}", transactionId);
			throw new DuplicateTransactionException(
					String.format("Transaction was already performed with transactionId: %s", transactionId));
		}
	}

	private BigDecimal validateBalance(BigDecimal requestedAmount, BigDecimal accountBalance) {

		BigDecimal remainingBalance = accountBalance.subtract(requestedAmount);

		if (remainingBalance.compareTo(BigDecimal.ZERO) < 0) {

			log.error("There are insufficient funds in your account. Please provide different amount.");
			throw new InsufficientFundException(
					"There are insufficient funds in your account. Please provide different amount.");
		}

		return remainingBalance;
	}

	private List<Transaction> createTransaction(String transactionId, LocalDateTime now, String transactionType,
			Account account) {

		List<Transaction> transactions = new ArrayList<>();

		Transaction transaction = Transaction.builder().transactionType(transactionType).transactionId(transactionId)
				.transactionDate(now).account(account).build();
		transactions.add(transaction);

		return transactions;
	}

	private String getCurrentTimeString(LocalDateTime now) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

		return now.format(formatter);
	}
}
