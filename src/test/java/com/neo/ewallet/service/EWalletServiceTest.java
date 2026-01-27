package com.neo.ewallet.service;

import com.neo.ewallet.dto.Result;
import com.neo.ewallet.model.Transactions;
import com.neo.ewallet.model.User;
import com.neo.ewallet.repository.TransactionRepository;
import com.neo.ewallet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EWalletServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private TransactionRepository txRepo;

    @Mock
    private EntityManager em;

    @Mock
    private Query query;

    @InjectMocks
    private EWalletService service;

    private final long USER_ID = 1L;

    @BeforeEach
    void setup() {
    }

    @Test
    void credit_success() {
        when(em.getReference(eq(User.class), anyLong()))
                .thenReturn(new User());
        BigDecimal amount = BigDecimal.valueOf(100);

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(BigDecimal.valueOf(1100));

        when(txRepo.save(any(Transactions.class)))
                .thenAnswer(inv -> {
                    Transactions tx = inv.getArgument(0);
                    tx.setId(10L);
                    return tx;
                });

        Result result = service.credit(USER_ID, amount);

        assertThat(result.getStatus()).isEqualTo("success");
        assertThat(result.getNewBalance()).isEqualTo(BigDecimal.valueOf(1100));
        assertThat(result.getTransactionId()).isEqualTo(10L);

        verify(txRepo).save(any(Transactions.class));
    }

    @Test
    void credit_invalidAmount_shouldThrowException() {
        assertThatThrownBy(() ->
                service.credit(USER_ID, BigDecimal.ZERO)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid amount");
    }

    @Test
    void debit_success() {
        when(em.getReference(eq(User.class), anyLong()))
                .thenReturn(new User());

        BigDecimal amount = BigDecimal.valueOf(200);

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(BigDecimal.valueOf(800));

        when(txRepo.save(any(Transactions.class)))
                .thenAnswer(inv -> {
                    Transactions tx = inv.getArgument(0);
                    tx.setId(20L);
                    return tx;
                });

        Result result = service.debit(USER_ID, amount);

        assertThat(result.getStatus()).isEqualTo("success");
        assertThat(result.getNewBalance()).isEqualTo(BigDecimal.valueOf(800));
        assertThat(result.getTransactionId()).isEqualTo(20L);
    }

    @Test
    void debit_insufficientFunds() {
        BigDecimal amount = BigDecimal.valueOf(500);

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new jakarta.persistence.NoResultException());

        Result result = service.debit(USER_ID, amount);

        assertThat(result.getStatus()).isEqualTo("error");
        assertThat(result.getMessage()).isEqualTo("Insufficient funds");

        verify(txRepo, never()).save(any());
    }
}
