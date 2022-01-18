package com.wallet.controller;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.exception.WalletServiceException;
import com.wallet.requestmodel.AccountRequest;
import com.wallet.responsemodel.AccountResponse;
import com.wallet.service.AccountService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/accounts")
public class AccountController implements AccountContract {

	@Autowired
	private AccountService accountService;

	@Override
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createAccount(@Valid @RequestBody AccountRequest accountRequest) {

		log.info("New account is getting created");

		try {
			accountService.createAccount(accountRequest);

			return new ResponseEntity<String>(new ObjectMapper().writeValueAsString("Account created successfully!!"),
					HttpStatus.CREATED);

		} catch (Exception exception) {

			log.error("Error occurred while creating an account: {}", exception.getMessage());
			throw new WalletServiceException(exception.getMessage());
		}
	}

	@Override
	@GetMapping(value = "{accountNumber}")
	public ResponseEntity<AccountResponse> fetchBalance(
			@PathVariable(name = "accountNumber") @NotBlank(message = "{accountNumber.not-blank}") String accountNumber) {

		log.info("Fetching the balance for accountNumber: {}", accountNumber);

		AccountResponse accountResponse = AccountResponse.builder().accountBalance(BigDecimal.ONE.toPlainString())
				.build();

		return ResponseEntity.ok().body(accountResponse);
	}
}
