package com.wallet.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.wallet.model.entity.Account;
import com.wallet.model.entity.Transaction;

@Repository
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {

	Optional<Transaction> findByTransactionId(String transactionId);

	Page<Transaction> findAllByAccount(Account account, Pageable pageable);
}
