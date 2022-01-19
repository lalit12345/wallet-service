package com.wallet.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.model.dto.request.TransactionRequest;
import com.wallet.model.dto.response.TransactionResponse;
import com.wallet.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/transaction")
public class TransactionController implements TransactionContract {

	@Autowired
	private TransactionService transactionService;

	@Override
	@PostMapping(value = "/debit")
	public ResponseEntity<TransactionResponse> performDebit(@Valid @RequestBody TransactionRequest transactionRequest) {

		log.info("Performing DEBIT transaction on account: {} with transactionId: {}",
				transactionRequest.getAccountNumber(), transactionRequest.getTransactionId());

		return ResponseEntity.ok(transactionService.performDebit(transactionRequest));
	}

	@Override
	@PostMapping(value = "/credit")
	public ResponseEntity<TransactionResponse> performCredit(
			@Valid @RequestBody TransactionRequest transactionRequest) {

		log.info("Performing CREDIT transaction on account: {} with transactionId: {}",
				transactionRequest.getAccountNumber(), transactionRequest.getTransactionId());

		return ResponseEntity.ok(transactionService.performCredit(transactionRequest));
	}
}
