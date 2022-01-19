package com.wallet.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.exception.WalletServiceException;
import com.wallet.model.dto.request.AccountRequest;
import com.wallet.model.dto.request.TransactionRequest;
import com.wallet.model.dto.response.AccountResponse;
import com.wallet.model.dto.response.TransactionResponse;
import com.wallet.service.AccountService;
import com.wallet.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/accounts")
public class AccountController implements AccountContract {

	@Autowired
	private AccountService accountService;

	@Autowired
	private TransactionService transactionService;

	@Override
	@PostMapping
	public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest accountRequest) {

		log.info("New account is getting created");

		try {
			return new ResponseEntity<AccountResponse>(accountService.createAccount(accountRequest),
					HttpStatus.CREATED);

		} catch (Exception exception) {

			log.error("Error occurred while creating an account: {}", exception.getMessage());
			throw new WalletServiceException(exception.getMessage());
		}
	}

	@Override
	@GetMapping(value = "/{accountNumber}/balance")
	public ResponseEntity<AccountResponse> fetchBalance(
			@PathVariable(name = "accountNumber") @NotBlank(message = "{accountNumber.not-blank}") String accountNumber) {

		log.info("Fetching the balance for accountNumber: {}", accountNumber);

		return ResponseEntity.ok().body(accountService.fetchBalance(accountNumber));
	}

	@Override
	@PostMapping(value = "/{accountNumber}/transactions/debit")
	public ResponseEntity<TransactionResponse> performDebit(
			@NotBlank @PathVariable(name = "accountNumber") String accountNumber,
			@Valid @RequestBody TransactionRequest transactionRequest) {

		log.info("Performing DEBIT transaction on account: {} with transactionId: {}", accountNumber,
				transactionRequest.getTransactionId());

		return ResponseEntity.ok(transactionService.performDebit(accountNumber, transactionRequest));
	}

	@Override
	@PostMapping(value = "/{accountNumber}/transactions/credit")
	public ResponseEntity<TransactionResponse> performCredit(
			@NotBlank @PathVariable(name = "accountNumber") String accountNumber,
			@Valid @RequestBody TransactionRequest transactionRequest) {

		log.info("Performing CREDIT transaction on account: {} with transactionId: {}", accountNumber,
				transactionRequest.getTransactionId());

		return ResponseEntity.ok(transactionService.performCredit(accountNumber, transactionRequest));
	}

	@Override
	@GetMapping(value = "/{accountNumber}/transactions")
	public ResponseEntity<TransactionResponse> getAllTransactions(
			@NotBlank @PathVariable(name = "accountNumber") String accountNumber) {

		log.info("Get all transactions for given accountNumber: {}", accountNumber);

		return ResponseEntity.ok().build();
	}
}
