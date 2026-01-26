package com.neo.ewallet.repository;

import com.neo.ewallet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query(value = """
      UPDATE users
      SET balance = balance + :amount
      WHERE id = :userId
      RETURNING balance
      """, nativeQuery = true)
    BigDecimal creditAndReturnBalance(long userId, BigDecimal amount);

    @Modifying
    @Query(value = """
      UPDATE users
      SET balance = balance - :amount
      WHERE id = :userId AND balance >= :amount
      RETURNING balance
      """, nativeQuery = true)
    BigDecimal debitIfSufficientAndReturnBalance(long userId, BigDecimal amount);
}