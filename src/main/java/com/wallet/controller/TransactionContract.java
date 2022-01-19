package com.wallet.controller;

import org.springframework.http.ResponseEntity;

import com.wallet.model.dto.request.TransactionRequest;
import com.wallet.model.dto.response.TransactionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "api/v1/transaction", description = "Transactions on the account performed by the user")
public interface TransactionContract {

	/**
	 * Perform a DEBIT transaction on given account number and created transaction
	 * record for given transactionId
	 * 
	 * @param transactionRequest
	 * @return a response entity with transaction response
	 */
	@Operation(summary = "Perform a DEBIT transaction on given account", tags = { "api/v1/transaction/debit" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Account debited successfully", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionRequest.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid request body"),
			@ApiResponse(responseCode = "409", description = "Duplicate transaction found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	ResponseEntity<TransactionResponse> performDebit(
			@Parameter(description = "Perform a DEBIT transaction on given account") TransactionRequest transactionRequest);

	/**
	 * Perform a DEBIT transaction on given account number and created transaction
	 * record for given transactionId
	 * 
	 * @param transactionRequest
	 * @return a response entity with transaction response
	 */
	@Operation(summary = "Perform a CREDIT transaction on given account", tags = { "api/v1/transaction/credit" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Account credited successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionRequest.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid request body"),
			@ApiResponse(responseCode = "409", description = "Duplicate transaction found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	ResponseEntity<TransactionResponse> performCredit(
			@Parameter(description = "Perform a CREDIT transaction on given account") TransactionRequest transactionRequest);
}
