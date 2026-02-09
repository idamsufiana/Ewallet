package com.neo.ewallet.service;

import com.neo.ewallet.dto.Result;
import com.neo.ewallet.model.Transactions;
import com.neo.ewallet.model.User;
import com.neo.ewallet.repository.TransactionRepository;
import com.neo.ewallet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EWalletService {

    private static final Logger log = LoggerFactory.getLogger(EWalletService.class);

    private static final String CREDIT = "credit";
    private static final String DEBIT  = "debit";
    private static final String SUCCESS = "success";
    private static final String ERROR  = "error";
    private final TransactionRepository transactionRepository;
    private final EntityManager em;


    public EWalletService(TransactionRepository transactionRepository, EntityManager em) {
        this.transactionRepository = transactionRepository;
        this.em = em;
    }

    @Transactional
    public Result credit(long userId, BigDecimal amount) {
        validateAmount(amount);

        BigDecimal newBalance = creditAndReturnBalance(userId, amount);
        if (newBalance == null) throw new IllegalArgumentException("User not found");

        Transactions transaction = saveTransaction(userId, amount, CREDIT);

        log.info("CREDIT userId={} amount={} newBalance={} txId={}", userId, amount, newBalance, transaction.getId());
        return new Result(SUCCESS, transaction.getId(), newBalance, null);
    }

    @Transactional
    public Result debit(long userId, BigDecimal amount) {
        validateAmount(amount);

        BigDecimal newBalance = debitAndReturnBalance(userId, amount);
        if (newBalance == null) {
            log.warn("DEBIT failed userId={} amount={} reason=insufficient_or_user_missing", userId, amount);
            return new Result(ERROR, null, null, "Insufficient funds");
        }
        Transactions transaction = saveTransaction(userId, amount, DEBIT);


        log.info("DEBIT userId={} amount={} newBalance={} txId={}", userId, amount, newBalance, transaction.getId());
        return new Result(SUCCESS, transaction.getId(), newBalance, null);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
    }

    private BigDecimal creditAndReturnBalance(long userId, BigDecimal amount) {
        BigDecimal balance = (BigDecimal) em.createNativeQuery("""
        UPDATE users
        SET balance = balance + :amount
        WHERE id = :userId
        RETURNING balance
    """)
                .setParameter("userId", userId)
                .setParameter("amount", amount)
                .getSingleResult();

        em.flush();
        em.clear();

        return balance;
    }

    public BigDecimal debitAndReturnBalance(long userId, BigDecimal amount) {
        try {
            BigDecimal balance = (BigDecimal) em.createNativeQuery("""
            UPDATE users
            SET balance = balance - :amount
            WHERE id = :userId
              AND balance >= :amount
            RETURNING balance
        """)
                    .setParameter("userId", userId)
                    .setParameter("amount", amount)
                    .getSingleResult();

            em.flush();
            em.clear();

            return balance;
        } catch (NoResultException e) {
            return null;
        }
    }

    private Transactions saveTransaction(long userId,
                                         BigDecimal amount,
                                         String type) {

        Transactions transaction = new Transactions();
        transaction.setUser(em.getReference(User.class, userId));
        transaction.setAmount(amount);
        transaction.setType(type);

        return transactionRepository.save(transaction);
    }


}