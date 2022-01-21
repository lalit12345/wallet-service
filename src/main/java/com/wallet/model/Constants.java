package com.wallet.model;

public class Constants {

	public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Account created succesfully";

	public static final String BALANCE_SUCCESS_MESSAGE = "Account balance fetched successfully";

	public static final String ACCOUNT_DOES_NOT_EXIST_MESSAGE = "Account does not exist with accountNumber: %s";

	public static final String ACCOUNT_ALREADY_EXISTS_MESSAGE = "Account already exists either with emailId: %s or mobileNumber: %s";

	public static final String DUPLICATE_TRANSACTION_MESSAGE = "Transaction was already performed with transactionId: %s";

	public static final String REQUESTED_PAGE_EXCEEDED_MESSAGE = "Requested page exceeds total number of pages: %d";

	public static final String NO_TRANSACTIONS_MESSAGE = "There were no transations performed on accountNumber: %s";

	public static final String CREDIT_SUCCESS_MESSAGE = "Account credited successfully";

	public static final String DEBIT_SUCCESS_MESSAGE = "Account debited successfully";

	public static final String INSUFFICIENT_FUNDS_MESSAGE = "There are insufficient funds in your account. Please provide different amount.";
}
