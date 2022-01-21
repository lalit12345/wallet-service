package com.wallet.controller;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.model.dto.request.AccountRequest;
import com.wallet.model.dto.request.TransactionRequest;
import com.wallet.model.dto.response.AccountDto;
import com.wallet.model.dto.response.TransactionDto;
import com.wallet.model.dto.response.TransactionResponse;
import com.wallet.service.AccountService;
import com.wallet.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping
public class AccountController implements AccountContract {

	@Autowired
	private AccountService accountService;

	@Autowired
	private TransactionService transactionService;

	@Override
	@PostMapping
	public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountRequest accountRequest) {

		log.info("New account is getting created");

		return new ResponseEntity<AccountDto>(accountService.createAccount(accountRequest), HttpStatus.CREATED);
	}

	@Override
	@GetMapping(value = "/{accountNumber}/balance")
	public ResponseEntity<AccountDto> fetchBalance(
			@PathVariable(name = "accountNumber") @NotBlank(message = "{accountNumber.not-blank}") String accountNumber) {

		log.info("Fetching the balance for accountNumber: {}", accountNumber);

		return ResponseEntity.ok().body(accountService.fetchBalance(accountNumber));
	}

	@Override
	@PostMapping(value = "/{accountNumber}/transactions/debit")
	public ResponseEntity<TransactionDto> performDebit(
			@NotBlank @PathVariable(name = "accountNumber") String accountNumber,
			@Valid @RequestBody TransactionRequest transactionRequest) {

		log.info("Performing DEBIT transaction on account: {} with transactionId: {}", accountNumber,
				transactionRequest.getTransactionId());

		return ResponseEntity.ok(transactionService.performDebit(accountNumber, transactionRequest));
	}

	@Override
	@PostMapping(value = "/{accountNumber}/transactions/credit")
	public ResponseEntity<TransactionDto> performCredit(
			@NotBlank @PathVariable(name = "accountNumber") String accountNumber,
			@Valid @RequestBody TransactionRequest transactionRequest) {

		log.info("Performing CREDIT transaction on account: {} with transactionId: {}", accountNumber,
				transactionRequest.getTransactionId());

		return ResponseEntity.ok(transactionService.performCredit(accountNumber, transactionRequest));
	}

	@Override
	@GetMapping(value = "/transactions")
	public ResponseEntity<TransactionResponse> getAllTransactions(
			@NotBlank @RequestParam(name = "accountNumber") String accountNumber,
			@Min(0) @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
			@Min(1) @Max(50) @RequestParam(name = "limit", defaultValue = "10", required = false) Integer limit) {

		log.info("Get all transactions for given accountNumber: {}", accountNumber);

		return ResponseEntity.ok(transactionService.getAllTransactions(accountNumber, page, limit));
	}
}
