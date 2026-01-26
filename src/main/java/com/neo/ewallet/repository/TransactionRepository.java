package com.neo.ewallet.repository;

import com.neo.ewallet.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {
}
