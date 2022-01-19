package com.wallet.service;

import com.wallet.model.dto.request.AccountRequest;
import com.wallet.model.dto.response.AccountResponse;

public interface AccountService {

	AccountResponse createAccount(AccountRequest accountDto);

	AccountResponse fetchBalance(String accountNumber);
}
