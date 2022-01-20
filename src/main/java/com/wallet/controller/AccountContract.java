package com.wallet.controller;

import org.springframework.http.ResponseEntity;

import com.wallet.model.dto.request.AccountRequest;
import com.wallet.model.dto.request.TransactionRequest;
import com.wallet.model.dto.response.AccountDto;
import com.wallet.model.dto.response.TransactionDto;
import com.wallet.model.dto.response.TransactionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "/accounts", description = "Account related operations for the user")
public interface AccountContract {

	/**
	 * Creates a new account for given account type
	 * 
	 * @param accountRequest
	 * @return a response entity with message
	 */
	@Operation(summary = "Create an account of given type", tags = { "/accounts" })
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Account created successfully", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = AccountRequest.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid request body"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	ResponseEntity<AccountDto> createAccount(
			@Parameter(description = "Create an account of given type") AccountRequest accountRequest);

	/**
	 * Fetches the account balance for given account number
	 * 
	 * @param accountNumber
	 * @return a account get response entity
	 */
	@Operation(summary = "Fetch the balance for the given account number", tags = {
			"/accounts/{accountNumber}/balance" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Balance fetched successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid account number"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	ResponseEntity<AccountDto> fetchBalance(
			@Parameter(description = "Fetch the balance for the given account number") String accountNumber);

	/**
	 * Perform a DEBIT transaction on given account number and created transaction
	 * record for given transactionId
	 * 
	 * @param transactionRequest
	 * @return a response entity with transaction response
	 */
	@Operation(summary = "Perform a DEBIT transaction on given account", tags = {
			"/accounts/{accountNumber}/transactions/debit" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Account debited successfully", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionRequest.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid request body"),
			@ApiResponse(responseCode = "409", description = "Duplicate transaction found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	ResponseEntity<TransactionDto> performDebit(
			@Parameter(description = "Perform a DEBIT transaction on given account") String accountNumber,
			TransactionRequest transactionRequest);

	/**
	 * Perform a CREDIT transaction on given account number and created transaction
	 * record for given transactionId
	 * 
	 * @param transactionRequest
	 * @return a response entity with transaction response
	 */
	@Operation(summary = "Perform a CREDIT transaction on given account", tags = {
			"/accounts/{accountNumber}/transactions/credit" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Account credited successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionRequest.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid request body"),
			@ApiResponse(responseCode = "409", description = "Duplicate transaction found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	ResponseEntity<TransactionDto> performCredit(
			@Parameter(description = "Perform a CREDIT transaction on given account") String accountNumber,
			TransactionRequest transactionRequest);

	/**
	 * Get all transactions for given user account
	 * 
	 * @param account number
	 * @return a response entity with transactions
	 */
	@Operation(summary = "Get all transactions for given user account", tags = { "/accounts/transactions" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Transaction details per user"),
			@ApiResponse(responseCode = "400", description = "Invalid account number"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	ResponseEntity<TransactionResponse> getAllTransactions(
			@Parameter(description = "Get all transactions for given user account") String accountNumber, Integer page,
			Integer limit);

}
