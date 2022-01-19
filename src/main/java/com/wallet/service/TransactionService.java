package com.wallet.service;

import com.wallet.model.dto.request.TransactionRequest;
import com.wallet.model.dto.response.TransactionResponse;

public interface TransactionService {

	TransactionResponse performDebit(TransactionRequest transactionRequest);
}
