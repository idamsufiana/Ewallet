package com.neo.ewallet;

import com.neo.ewallet.model.User;
import com.neo.ewallet.repository.UserRepository;
import com.neo.ewallet.service.EWalletService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EWalletConcurrentTest {

    @Autowired
    private EWalletService eWalletService;

    @Autowired
    private UserRepository userRepository;

    private long userId;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setBalance(BigDecimal.ZERO);
        user.setUsername("test-user");
        user = userRepository.saveAndFlush(user);
        userId = user.getId();
    }

    @Test
    void concurrent_credit_should_be_atomic() throws Exception {

        int threadCount = 100;
        int txPerThread = 100;
        BigDecimal amount = BigDecimal.valueOf(1_000);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < txPerThread; j++) {
                        eWalletService.credit(userId, amount);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        User user = userRepository.findById(userId).orElseThrow();
        BigDecimal expected =
                BigDecimal.valueOf(threadCount)
                        .multiply(BigDecimal.valueOf(txPerThread))
                        .multiply(amount);

        assertThat(user.getBalance()).isEqualByComparingTo(expected);
    }
}
