package com.wallet.controller;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.exception.DuplicateTransactionException;
import com.wallet.exception.InvalidRequestDataException;
import com.wallet.model.dto.request.AccountRequest;
import com.wallet.model.dto.request.TransactionRequest;
import com.wallet.model.dto.response.AccountDto;
import com.wallet.model.dto.response.TransactionDto;
import com.wallet.model.dto.response.TransactionResponse;
import com.wallet.service.AccountService;
import com.wallet.service.TransactionService;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AccountService accountService;

	@MockBean
	private TransactionService transactionService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void shouldCreateAnAccount() throws Exception {

		AccountRequest accountRequest = AccountRequest.builder().emailId("test@test.com").accountType("SAVINGS")
				.balanceAmount(new BigDecimal(100)).fullName("Test Test").mobileNumber("1111111111").build();

		AccountDto accountDto = AccountDto.builder().message("Account created successfully").accountNumber("123456")
				.build();

		when(accountService.createAccount(any())).thenReturn(accountDto);

		mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON_VALUE).content(toJsonString(accountRequest)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.message").value("Account created successfully"))
				.andExpect(jsonPath("$.accountNumber").value("123456"));

		verify(accountService, times(1)).createAccount(accountRequest);
		verifyNoMoreInteractions(accountService);
	}

	@Test
	public void shouldReturnBadRequestWhenInvalidRequestIsSent() throws Exception {

		List<String> fields = Arrays.asList("emailId", "mobileNumber", "accountType", "balanceAmount", "fullName");
		List<String> errors = Arrays.asList("format is invalid", "must contain only numbers 0 to 9",
				"can only be 'SAVINGS'", "should be greater than or equal to 1", "cannot be null or empty");

		AccountRequest accountRequest = AccountRequest.builder().emailId("testtest.com").accountType("SAVINGSS")
				.balanceAmount(new BigDecimal(0)).fullName("").mobileNumber("M1111111111").build();

		mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON_VALUE).content(toJsonString(accountRequest)))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Bad Request"))
				.andExpect(jsonPath("$.errors[*].field", containsInAnyOrder(fields.toArray())))
				.andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(errors.toArray())));

		verify(accountService, times(0)).createAccount(accountRequest);
	}

	@Test
	public void shouldFetchTheBalanceForGivenAccountNumber() throws Exception {

		AccountDto accountDto = AccountDto.builder().message("Account balance fetched successfully")
				.accountNumber("123456").accountBalance("100").build();

		when(accountService.fetchBalance(anyString())).thenReturn(accountDto);

		mockMvc.perform(get("/123456/balance")).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Account balance fetched successfully"))
				.andExpect(jsonPath("$.accountNumber").value("123456"))
				.andExpect(jsonPath("$.accountBalance").value("100"));
	}

	@Test
	public void shouldDebitTheAmountFromAccount() throws Exception {

		TransactionRequest transactionRequest = TransactionRequest.builder().amount(new BigDecimal(10))
				.transactionId("TId12").build();

		TransactionDto transactionDto = TransactionDto.builder().accountNumber("123456")
				.message("Account debited successfully").transactionAmount("10").transactionId("TId12")
				.transactionType("DEBIT").build();

		when(transactionService.performDebit(anyString(), any())).thenReturn(transactionDto);

		mockMvc.perform(post("/123456/transactions/debit").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(toJsonString(transactionRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Account debited successfully"))
				.andExpect(jsonPath("$.accountNumber").value("123456"))
				.andExpect(jsonPath("$.transactionAmount").value("10"))
				.andExpect(jsonPath("$.transactionId").value("TId12"))
				.andExpect(jsonPath("$.transactionType").value("DEBIT"));
	}

	@Test
	public void shouldReturnBadRequestWhenTransactionRequestForDebitIsInvalid() throws Exception {

		List<String> fields = Arrays.asList("amount", "transactionId");
		List<String> errors = Arrays.asList("should be greater than or equal to 1", "cannot be null or empty");

		TransactionRequest transactionRequest = TransactionRequest.builder().amount(new BigDecimal(0)).transactionId("")
				.build();

		mockMvc.perform(post("/123456/transactions/debit").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(toJsonString(transactionRequest))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Bad Request"))
				.andExpect(jsonPath("$.errors[*].field", containsInAnyOrder(fields.toArray())))
				.andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(errors.toArray())));
	}

	@Test
	public void shouldReturnBadRequestWhenInsufficientFunds() throws Exception {

		TransactionRequest transactionRequest = TransactionRequest.builder().amount(new BigDecimal(1))
				.transactionId("TId18").build();

		when(transactionService.performDebit(anyString(), any()))
				.thenThrow(new InvalidRequestDataException("Invalid requested amount"));

		mockMvc.perform(post("/123456/transactions/debit").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(toJsonString(transactionRequest))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Invalid requested amount"));
	}

	@Test
	public void shouldReturnBadRequestWhenAccountDoesNotExist() throws Exception {

		TransactionRequest transactionRequest = TransactionRequest.builder().amount(new BigDecimal(1))
				.transactionId("TId18").build();

		when(transactionService.performDebit(anyString(), any()))
				.thenThrow(new InvalidRequestDataException("Account does not exist"));

		mockMvc.perform(post("/123456/transactions/debit").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(toJsonString(transactionRequest))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Account does not exist"));
	}

	@Test
	public void shouldReturnConflictWhenDuplicateTransaction() throws Exception {

		TransactionRequest transactionRequest = TransactionRequest.builder().amount(new BigDecimal(1))
				.transactionId("TId18").build();

		when(transactionService.performDebit(anyString(), any()))
				.thenThrow(new DuplicateTransactionException("Duplicate transaction"));

		mockMvc.perform(post("/123456/transactions/debit").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(toJsonString(transactionRequest))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.message").value("Duplicate transaction"));
	}

	@Test
	public void shouldCreditTheAmountToAccount() throws Exception {

		TransactionRequest transactionRequest = TransactionRequest.builder().amount(new BigDecimal(10))
				.transactionId("TId13").build();

		TransactionDto transactionDto = TransactionDto.builder().accountNumber("123456")
				.message("Account credited successfully").transactionAmount("10").transactionId("TId13")
				.transactionType("CREDIT").build();

		when(transactionService.performCredit(anyString(), any())).thenReturn(transactionDto);

		mockMvc.perform(post("/123456/transactions/credit").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(toJsonString(transactionRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Account credited successfully"))
				.andExpect(jsonPath("$.accountNumber").value("123456"))
				.andExpect(jsonPath("$.transactionAmount").value("10"))
				.andExpect(jsonPath("$.transactionId").value("TId13"))
				.andExpect(jsonPath("$.transactionType").value("CREDIT"));
	}

	@Test
	public void shouldReturnBadRequestWhenTransactionRequestForCreditIsInvalid() throws Exception {

		List<String> fields = Arrays.asList("amount", "transactionId");
		List<String> errors = Arrays.asList("should be greater than or equal to 1", "cannot be null or empty");

		TransactionRequest transactionRequest = TransactionRequest.builder().amount(new BigDecimal(0)).transactionId("")
				.build();

		mockMvc.perform(post("/123456/transactions/credit").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(toJsonString(transactionRequest))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Bad Request"))
				.andExpect(jsonPath("$.errors[*].field", containsInAnyOrder(fields.toArray())))
				.andExpect(jsonPath("$.errors[*].error", containsInAnyOrder(errors.toArray())));
	}

	@Test
	public void shouldReturnListOfTransactionsBasedOnCriteria() throws Exception {

		List<TransactionDto> transactionDtos = new ArrayList<>();
		TransactionDto transactionDto1 = TransactionDto.builder().accountNumber("123456").transactionAmount("10")
				.transactionId("TId15").transactionType("DEBIT").build();
		transactionDtos.add(transactionDto1);

		TransactionDto transactionDto2 = TransactionDto.builder().accountNumber("123456").transactionAmount("10")
				.transactionId("TId16").transactionType("CREDIT").build();
		transactionDtos.add(transactionDto2);

		when(transactionService.getAllTransactions(anyString(), anyInt(), anyInt()))
				.thenReturn(TransactionResponse.builder().message("Success").totalNoOfPages(1).totalNoOfTransactions(2)
						.transactions(transactionDtos).build());

		mockMvc.perform(get("/transactions?accountNumber=123456&page=0&limit=2")).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Success")).andExpect(jsonPath("$.totalNoOfPages").value(1))
				.andExpect(jsonPath("$.totalNoOfTransactions").value(2));
	}

	private String toJsonString(Object object) {

		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}
