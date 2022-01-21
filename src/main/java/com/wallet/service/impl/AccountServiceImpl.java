package com.wallet.service.impl;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wallet.exception.InvalidRequestDataException;
import com.wallet.model.AccountStatus;
import com.wallet.model.Constants;
import com.wallet.model.dto.request.AccountRequest;
import com.wallet.model.dto.response.AccountDto;
import com.wallet.model.entity.Account;
import com.wallet.repository.AccountRepository;
import com.wallet.service.AccountService;
import com.wallet.util.Util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private Util accountUtil;

	@Override
	public AccountDto createAccount(AccountRequest accountRequest) {

		// Check if account already exists
		Optional<Account> optionalAccount = accountRepository.findByEmailIdOrMobileNumber(accountRequest.getEmailId(),
				accountRequest.getMobileNumber());

		if (optionalAccount.isPresent()) {

			log.error(String.format(Constants.ACCOUNT_ALREADY_EXISTS_MESSAGE, accountRequest.getEmailId(),
					accountRequest.getMobileNumber()));
			throw new InvalidRequestDataException(String.format(Constants.ACCOUNT_ALREADY_EXISTS_MESSAGE,
					accountRequest.getEmailId(), accountRequest.getMobileNumber()));
		}

		Account account = new Account();
		BeanUtils.copyProperties(accountRequest, account);

		String accountNumber = accountUtil.generateAccountNumber();
		account.setAccountNumber(accountNumber);
		account.setAccountStatus(AccountStatus.ACTIVE.name());

		accountRepository.save(account);

		log.info("Account created successfully with accountNumber: {}", accountNumber);

		return AccountDto.builder().message(Constants.ACCOUNT_CREATION_SUCCESS_MESSAGE).accountNumber(accountNumber)
				.build();
	}

	@Override
	public AccountDto fetchBalance(String accountNumber) {

		Optional<Account> optionalAccount = accountRepository.findByAccountNumberAndAccountStatus(accountNumber,
				AccountStatus.ACTIVE.name());

		if (optionalAccount.isEmpty()) {

			log.error(String.format(Constants.ACCOUNT_DOES_NOT_EXIST_MESSAGE, accountNumber));
			throw new InvalidRequestDataException(String.format(Constants.ACCOUNT_DOES_NOT_EXIST_MESSAGE, accountNumber));
		}

		Account account = optionalAccount.get();

		return AccountDto.builder().message(Constants.BALANCE_SUCCESS_MESSAGE)
				.accountBalance(account.getBalanceAmount().toPlainString()).accountNumber(account.getAccountNumber())
				.build();
	}
}
