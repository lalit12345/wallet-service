package com.wallet.requestmodel;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.wallet.enums.AccountType;
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
}
