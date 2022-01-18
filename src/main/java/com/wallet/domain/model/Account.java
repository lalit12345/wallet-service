package com.wallet.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "account_number")
	private Integer accountNumber;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "mobile_number")
	private String mobileNumber;

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
	private List<Transaction> transactions;

	@Column(name = "balance_amount")
	private BigDecimal balanceAmount;

	@Column(name = "account_type")
	private String accountType;

	@Column(name = "account_status")
	private String accountStatus;
}
