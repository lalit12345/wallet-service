package com.wallet.service;

import com.wallet.model.dto.request.AccountRequest;
import com.wallet.model.dto.response.AccountDto;

public interface AccountService {

	AccountDto createAccount(AccountRequest accountDto);

	AccountDto fetchBalance(String accountNumber);
}
