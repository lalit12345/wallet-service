package com.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wallet.exception.InvalidRequestDataException;
import com.wallet.model.dto.request.AccountRequest;
import com.wallet.model.dto.response.AccountDto;
import com.wallet.model.entity.Account;
import com.wallet.repository.AccountRepository;
import com.wallet.service.impl.AccountServiceImpl;
import com.wallet.util.Util;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

	@InjectMocks
	private AccountServiceImpl accountServiceImpl;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private Util util;

	@BeforeEach
	public void init() {

		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void shouldCreateAnAccount() {

		Account account = Account.builder().accountNumber("123456").emailId("test1@test1.com")
				.balanceAmount(new BigDecimal(100)).fullName("Test1 Test1").mobileNumber("1111111111")
				.accountType("SAVINGS").accountStatus("ACTIVE").build();

		AccountRequest accountRequest = AccountRequest.builder().accountType("SAVINGS")
				.balanceAmount(new BigDecimal(100)).emailId("test1@test1.com").fullName("Test1 Test1")
				.mobileNumber("1111111111").build();

		when(util.generateAccountNumber()).thenReturn("123456");

		AccountDto accountDto = accountServiceImpl.createAccount(accountRequest);

		verify(accountRepository, times(1)).save(account);
		assertEquals("123456", accountDto.getAccountNumber());
		assertEquals("Account created succesfully", accountDto.getMessage());
	}

	@Test
	public void shouldThrowAnExceptionIfAccountAlreadyExistsWithGivenEmailOrMobile() {

		Account account = Account.builder().accountNumber("123456").emailId("test1@test1.com")
				.balanceAmount(new BigDecimal(100)).fullName("Test1 Test1").mobileNumber("1111111111")
				.accountType("SAVINGS").accountStatus("ACTIVE").build();

		AccountRequest accountRequest = AccountRequest.builder().accountType("SAVINGS")
				.balanceAmount(new BigDecimal(100)).emailId("test1@test1.com").fullName("Test1 Test1")
				.mobileNumber("1111111111").build();

		when(accountRepository.findByEmailIdOrMobileNumber(anyString(), anyString())).thenReturn(Optional.of(account));

		InvalidRequestDataException accountAlreadyExistsException = assertThrows(InvalidRequestDataException.class, () -> {
			accountServiceImpl.createAccount(accountRequest);
		});

		assertEquals("Account already exists either with emailId: test1@test1.com or mobileNumber: 1111111111",
				accountAlreadyExistsException.getMessage());
	}

	@Test
	public void shouldFetchBalanceForGivenAccount() {

		Account account = Account.builder().accountNumber("123456").emailId("test1@test1.com")
				.balanceAmount(new BigDecimal(100)).fullName("Test1 Test1").mobileNumber("1111111111")
				.accountType("SAVINGS").accountStatus("ACTIVE").build();

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.of(account));

		AccountDto accountDto = accountServiceImpl.fetchBalance("123456");

		assertEquals("123456", accountDto.getAccountNumber());
		assertEquals("100", accountDto.getAccountBalance());
		assertEquals("Account balance fetched successfully", accountDto.getMessage());
	}

	@Test
	public void shouldThrowAnAccountDoesNotExistExceptionWhenWrongAccountNumberProvided() {

		when(accountRepository.findByAccountNumberAndAccountStatus(anyString(), anyString()))
				.thenReturn(Optional.empty());

		InvalidRequestDataException accountDoesNotExistException = assertThrows(InvalidRequestDataException.class, () -> {
			accountServiceImpl.fetchBalance("123456");
		});

		assertEquals("Account does not exist with accountNumber: 123456", accountDoesNotExistException.getMessage());
	}
}
