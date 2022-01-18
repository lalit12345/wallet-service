package com.wallet.service.impl;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wallet.domain.model.Account;
import com.wallet.enums.AccountStatus;
import com.wallet.repository.AccountRepository;
import com.wallet.requestmodel.AccountRequest;
import com.wallet.service.AccountService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public void createAccount(AccountRequest accountDto) {

		Account account = new Account();
		BeanUtils.copyProperties(accountDto, account);

		Random random = new Random();
		Integer accountNumber = random.nextInt(9000000) + 1000000;

		account.setAccountNumber(accountNumber);
		account.setBalanceAmount(BigDecimal.ZERO);
		account.setAccountStatus(AccountStatus.ACTIVE.name());

		accountRepository.save(account);

		log.info("Account with accountNumber: {} created successfully", accountNumber);
	}
}
