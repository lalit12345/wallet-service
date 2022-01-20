package com.wallet.service;

import com.wallet.model.dto.request.TransactionRequest;
import com.wallet.model.dto.response.TransactionDto;
import com.wallet.model.dto.response.TransactionResponse;

public interface TransactionService {

	TransactionDto performDebit(String accountNumber, TransactionRequest transactionRequest);

	TransactionDto performCredit(String accountNumber, TransactionRequest transactionRequest);

	TransactionResponse getAllTransactions(String accountNumber, Integer page, Integer limit);
}
