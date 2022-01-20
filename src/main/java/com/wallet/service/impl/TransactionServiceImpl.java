package com.wallet.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wallet.exception.AccountDoesNotExistException;
import com.wallet.exception.DuplicateTransactionException;
import com.wallet.exception.InsufficientFundException;
import com.wallet.model.AccountStatus;
import com.wallet.model.Constants;
import com.wallet.model.TransactionType;
import com.wallet.model.dto.request.TransactionRequest;
import com.wallet.model.dto.response.TransactionDto;
import com.wallet.model.dto.response.TransactionResponse;
import com.wallet.model.entity.Account;
import com.wallet.model.entity.Transaction;
import com.wallet.repository.AccountRepository;
import com.wallet.repository.TransactionRepository;
import com.wallet.service.TransactionService;
import com.wallet.util.Util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private Util transactionUtil;

	@Override
	public TransactionDto performDebit(String accountNumber, TransactionRequest transactionRequest) {

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
		List<Transaction> transactions = createTransaction(transactionRequest, now, TransactionType.DEBIT.name(),
				account);

		account.setTransactions(transactions);
		Account savedAccount = accountRepository.save(account);

		return TransactionDto.builder().message(Constants.DEBIT_SUCCESS_MESSAGE)
				.accountNumber(savedAccount.getAccountNumber())
				.accountBalance(savedAccount.getBalanceAmount().toPlainString())
				.transactionType(TransactionType.DEBIT.name()).transactionDate(transactionUtil.getTimeString(now))
				.build();
	}

	@Override
	public TransactionDto performCredit(String accountNumber, TransactionRequest transactionRequest) {

		// Check account exists
		Optional<Account> optionalAccount = checkAccountExists(accountNumber);

		// Check duplicate transaction
		checkDuplicateTransaction(transactionRequest.getTransactionId());

		Account account = optionalAccount.get();
		account.setBalanceAmount(account.getBalanceAmount().add(transactionRequest.getAmount()));

		// Create transaction record
		LocalDateTime now = LocalDateTime.now();
		List<Transaction> transactions = createTransaction(transactionRequest, now, TransactionType.CREDIT.name(),
				account);

		account.setTransactions(transactions);
		Account savedAccount = accountRepository.save(account);

		return TransactionDto.builder().message(Constants.CREDIT_SUCCESS_MESSAGE)
				.accountNumber(savedAccount.getAccountNumber())
				.accountBalance(savedAccount.getBalanceAmount().toPlainString())
				.transactionType(TransactionType.CREDIT.name()).transactionDate(transactionUtil.getTimeString(now))
				.build();
	}

	@Override
	public TransactionResponse getAllTransactions(String accountNumber, Integer page, Integer limit) {

		// Check account exists
		Optional<Account> optionalAccount = checkAccountExists(accountNumber);

		Pageable pageable = PageRequest.of(page, limit);

		Page<Transaction> transactionsPage = transactionRepository.findAllByAccount(optionalAccount.get(), pageable);

		int totalPages = transactionsPage.getTotalPages();

		// Check if requested page number exceeds total number of pages
		if (totalPages > 0 && page > totalPages - 1) {
			return pageNumberExceededResponse(totalPages, transactionsPage.getTotalElements());
		}

		// Check if there are any transactions
		if (transactionsPage.isEmpty()) {
			return emptyTransactionsResponse(accountNumber, totalPages, transactionsPage.getTotalElements());
		}

		List<TransactionDto> transactions = transactionsPage.getContent().stream()
				.map(transactionUtil::mapToTransactionDto).collect(Collectors.toList());

		return TransactionResponse.builder().status(200).message("Success").transactions(transactions)
				.totalNoOfPages(totalPages).totalNoOfTransactions(Math.toIntExact(transactionsPage.getTotalElements()))
				.build();
	}

	private TransactionResponse emptyTransactionsResponse(String accountNumber, int totalPages, Long totalElements) {

		return TransactionResponse.builder().status(200)
				.message(String.format(Constants.NO_TRANSACTIONS_MESSAGE, accountNumber))
				.transactions(Collections.emptyList()).totalNoOfPages(totalPages)
				.totalNoOfTransactions(Math.toIntExact(totalElements)).build();
	}

	private TransactionResponse pageNumberExceededResponse(int totalPages, Long totalElements) {

		return TransactionResponse.builder().status(200)
				.message(String.format(Constants.REQUESTED_PAGE_EXCEEDED_MESSAGE, totalPages))
				.transactions(Collections.emptyList()).totalNoOfPages(totalPages)
				.totalNoOfTransactions(Math.toIntExact(totalElements)).build();
	}

	private Optional<Account> checkAccountExists(String accountNumber) {

		Optional<Account> optionalAccount = accountRepository.findByAccountNumberAndAccountStatus(accountNumber,
				AccountStatus.ACTIVE.name());

		if (optionalAccount.isEmpty()) {

			log.error(String.format(Constants.ACCOUNT_DOES_NOT_EXIST_MESSAGE, accountNumber));
			throw new AccountDoesNotExistException(
					String.format(Constants.ACCOUNT_DOES_NOT_EXIST_MESSAGE, accountNumber));
		}
		return optionalAccount;
	}

	private void checkDuplicateTransaction(String transactionId) {

		Optional<Transaction> optionalTransaction = transactionRepository.findByTransactionId(transactionId);

		if (optionalTransaction.isPresent()) {

			log.error(String.format(Constants.DUPLICATE_TRANSACTION_MESSAGE, transactionId));
			throw new DuplicateTransactionException(
					String.format(Constants.DUPLICATE_TRANSACTION_MESSAGE, transactionId));
		}
	}

	private BigDecimal validateBalance(BigDecimal requestedAmount, BigDecimal accountBalance) {

		BigDecimal remainingBalance = accountBalance.subtract(requestedAmount);

		if (remainingBalance.compareTo(BigDecimal.ZERO) < 0) {

			log.error(Constants.INSUFFICIENT_FUNDS_MESSAGE);
			throw new InsufficientFundException(Constants.INSUFFICIENT_FUNDS_MESSAGE);
		}

		return remainingBalance;
	}

	private List<Transaction> createTransaction(TransactionRequest transactionRequest, LocalDateTime now,
			String transactionType, Account account) {

		List<Transaction> transactions = new ArrayList<>();

		Transaction transaction = Transaction.builder().transactionType(transactionType)
				.transactionId(transactionRequest.getTransactionId()).transactionDate(now)
				.transactionAmount(transactionRequest.getAmount()).account(account).build();
		transactions.add(transaction);

		return transactions;
	}
}
