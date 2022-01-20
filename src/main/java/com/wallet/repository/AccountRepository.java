package com.wallet.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.wallet.model.entity.Account;

@Repository
public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

	Optional<Account> findByAccountNumberAndAccountStatus(String accountNumber, String accountStatus);
}
