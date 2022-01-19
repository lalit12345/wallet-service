package com.wallet.service.impl;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wallet.exception.AccountNotFoundException;
import com.wallet.model.AccountStatus;
import com.wallet.model.dto.request.AccountRequest;
import com.wallet.model.dto.response.AccountResponse;
import com.wallet.model.entity.Account;
import com.wallet.repository.AccountRepository;
import com.wallet.service.AccountService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public AccountResponse createAccount(AccountRequest accountDto) {

		Account account = new Account();
		BeanUtils.copyProperties(accountDto, account);

		Random random = new Random();
		String accountNumber = String.valueOf(random.nextInt(9000000) + 1000000);

		account.setAccountNumber(accountNumber);
		account.setAccountStatus(AccountStatus.ACTIVE.name());

		accountRepository.save(account);

		log.info("Account created successfully with accountNumber: {}", accountNumber);

		return AccountResponse.builder().message("Account created succesfully").accountNumber(accountNumber).build();
	}

	@Override
	public AccountResponse fetchBalance(String accountNumber) {

		Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);

		if (optionalAccount.isEmpty()) {

			log.error("Account not found with accountNumber: {}", accountNumber);
			throw new AccountNotFoundException(
					String.format("Account not found with accountNumber: %s", accountNumber));
		}

		Account account = optionalAccount.get();

		return AccountResponse.builder().message("Account balance fetched successfully")
				.accountBalance(account.getBalanceAmount().toPlainString()).accountNumber(account.getAccountNumber())
				.build();
	}
}
