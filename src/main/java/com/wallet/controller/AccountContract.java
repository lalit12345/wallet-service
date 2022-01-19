package com.wallet.controller;

import org.springframework.http.ResponseEntity;

import com.wallet.model.dto.request.AccountRequest;
import com.wallet.model.dto.response.AccountResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "api/v1/account", description = "Account related operations for the user")
public interface AccountContract {

	/**
	 * Creates a new account for given account type
	 * 
	 * @param accountRequest
	 * @return a response entity with message
	 */
	@Operation(summary = "Create an account of given type", tags = { "api/v1/account" })
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Account created successfully", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = AccountRequest.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid request body"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	ResponseEntity<AccountResponse> createAccount(
			@Parameter(description = "Create an account of given type") AccountRequest accountRequest);

	/**
	 * Fetches the account balance for given account number
	 * 
	 * @param accountNumber
	 * @return a account get response entity
	 */
	@Operation(summary = "Fetch the balance for the given account number", tags = {
			"api/v1/account/{accountNumber}/balance" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Balance fetched successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid account number"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	ResponseEntity<AccountResponse> fetchBalance(
			@Parameter(description = "Fetch the balance for the given account number") String accountNumber);

}
