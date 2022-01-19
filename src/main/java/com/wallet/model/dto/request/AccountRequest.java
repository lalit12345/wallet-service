package com.wallet.model.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.wallet.model.AccountType;
import com.wallet.validator.EnumValidator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

	@NotBlank(message = "{fullName.not-blank}")
	private String fullName;

	@Email(message = "{emailId.pattern}")
	@NotBlank(message = "{emailId.not-blank}")
	private String emailId;

	@Pattern(regexp = "^[0-9]+?|^$", message = "{mobileNumber.pattern}")
	@NotBlank(message = "{mobileNumber.not-blank}")
	private String mobileNumber;

	@EnumValidator(enumClass = AccountType.class, message = "{accountType.invalid}")
	private String accountType;

	@DecimalMin(value = "1", message = "{amount.min}")
	private BigDecimal balanceAmount;
}
