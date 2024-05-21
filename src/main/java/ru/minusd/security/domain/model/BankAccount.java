package ru.minusd.security.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "bank_account")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_account_id")
    private Long id;

    @Positive
    @Column(name = "bank_account_balance")
    private BigDecimal balance;

    @Positive
    @NotNull
    @Column(name = "initial_deposit", nullable = false)
    private BigDecimal initialDeposit;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
