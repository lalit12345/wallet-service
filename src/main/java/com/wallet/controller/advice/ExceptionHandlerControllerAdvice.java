package com.wallet.controller.advice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wallet.exception.AccountDoesNotExistException;
import com.wallet.exception.DuplicateTransactionException;
import com.wallet.exception.InsufficientFundException;
import com.wallet.exception.WalletServiceException;
import com.wallet.model.dto.ErrorMessage;
import com.wallet.model.dto.ErrorResponses;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponses> processMethodArgumentNotValidException(
			final MethodArgumentNotValidException argumentNotValidException) {

		log.error("MethodArgumentNotValidException: {}, {}", HttpStatus.BAD_REQUEST.value(),
				argumentNotValidException.getMessage());

		List<ErrorMessage> errorMessages = new ArrayList<>();

		argumentNotValidException.getBindingResult().getFieldErrors().forEach(fieldError -> {

			errorMessages.add(ErrorMessage.builder().field(String.valueOf(fieldError.getField()))
					.error(fieldError.getDefaultMessage()).build());
		});

		ErrorResponses errorResponses = ErrorResponses.builder().status(HttpStatus.BAD_REQUEST.value())
				.message(HttpStatus.BAD_REQUEST.getReasonPhrase()).errors(errorMessages).build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponses);
	}

	@ExceptionHandler({ AccountDoesNotExistException.class, InsufficientFundException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponses> processNotFoundException(final Exception exception) {

		log.error("Exception: {}, {}", HttpStatus.BAD_REQUEST.value(), exception.getMessage());

		ErrorResponses errorResponses = ErrorResponses.builder().status(HttpStatus.BAD_REQUEST.value())
				.message(exception.getMessage()).build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponses);
	}

	@ExceptionHandler(DuplicateTransactionException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<ErrorResponses> processDuplicateException(final Exception exception) {

		log.error("Exception: {}, {}", HttpStatus.CONFLICT.value(), exception.getMessage());

		ErrorResponses errorResponses = ErrorResponses.builder().status(HttpStatus.CONFLICT.value())
				.message(exception.getMessage()).build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponses);
	}

	@ExceptionHandler(WalletServiceException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponses> processException(final Exception exception) {

		log.error("Exception: {}, {}", HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());

		ErrorResponses errorResponses = ErrorResponses.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.message(exception.getMessage()).build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponses);
	}
}
