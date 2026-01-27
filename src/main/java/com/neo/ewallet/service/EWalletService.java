package com.neo.ewallet.service;

import com.neo.ewallet.dto.Result;
import com.neo.ewallet.model.Transactions;
import com.neo.ewallet.model.User;
import com.neo.ewallet.repository.TransactionRepository;
import com.neo.ewallet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EWalletService {

    private static final Logger log = LoggerFactory.getLogger(EWalletService.class);

    private final UserRepository userRepo;
    private final TransactionRepository txRepo;
    private final EntityManager em;


    public EWalletService(UserRepository userRepo, TransactionRepository txRepo, EntityManager em) {
        this.userRepo = userRepo;
        this.txRepo = txRepo;
        this.em = em;
    }

    @Transactional
    public Result credit(long userId, BigDecimal amount) {
        validateAmount(amount);

        BigDecimal newBalance = creditAndReturnBalance(userId, amount);
        if (newBalance == null) throw new IllegalArgumentException("User not found");

        Transactions tx = new Transactions();
        tx.setUser(em.getReference(User.class, userId));
        tx.setAmount(amount);
        tx.setType("credit");
        txRepo.save(tx);

        log.info("CREDIT userId={} amount={} newBalance={} txId={}", userId, amount, newBalance, tx.getId());
        return new Result("success", tx.getId(), newBalance, null);
    }

    @Transactional
    public Result debit(long userId, BigDecimal amount) {
        validateAmount(amount);

        BigDecimal newBalance = debitAndReturnBalance(userId, amount);
        if (newBalance == null) {
            // bisa: user not found atau insufficient funds. biasanya cek user exist dulu biar message tepat.
            log.warn("DEBIT failed userId={} amount={} reason=insufficient_or_user_missing", userId, amount);
            return new Result("error", null, null, "Insufficient funds");
        }

        Transactions tx = new Transactions();
        tx.setUser(em.getReference(User.class, userId));
        tx.setAmount(amount);
        tx.setType("debit");
        txRepo.save(tx);

        log.info("DEBIT userId={} amount={} newBalance={} txId={}", userId, amount, newBalance, tx.getId());
        return new Result("success", tx.getId(), newBalance, null);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
    }

    private BigDecimal creditAndReturnBalance(long userId, BigDecimal amount) {
        return (BigDecimal) em.createNativeQuery("""
            UPDATE users
            SET balance = balance + :amount
            WHERE id = :userId
            RETURNING balance
        """)
                .setParameter("userId", userId)
                .setParameter("amount", amount)
                .getSingleResult();
    }

    @Transactional
    public BigDecimal debitAndReturnBalance(long userId, BigDecimal amount) {
        try {
            return (BigDecimal) em.createNativeQuery("""
            UPDATE users
            SET balance = balance - :amount
            WHERE id = :userId
              AND balance >= :amount
            RETURNING balance
        """)
                    .setParameter("userId", userId)
                    .setParameter("amount", amount)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }



}