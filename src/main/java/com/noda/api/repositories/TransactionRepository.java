package com.noda.api.repositories;

import com.noda.api.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_IdOrderByTimestampDesc(Long accountId);
}
