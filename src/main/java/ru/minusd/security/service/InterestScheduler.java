package ru.minusd.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.minusd.security.domain.model.BankAccount;
import ru.minusd.security.repository.BankAccountRepository;

import java.math.BigDecimal;

@Service
public class InterestScheduler {
    private static final Logger log = LoggerFactory.getLogger(InterestScheduler.class);

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Scheduled(fixedRate = 60000)
    public void addInterest() {
        Iterable<BankAccount> accounts = bankAccountRepository.findAll();
        for (BankAccount account : accounts) {
            BigDecimal currentBalance = account.getBalance();
            BigDecimal initialDeposit = account.getInitialDeposit();
            BigDecimal maxBalance = initialDeposit.multiply(BigDecimal.valueOf(2.07));
            if (currentBalance.compareTo(maxBalance) < 0) {
                BigDecimal newBalance = currentBalance.multiply(BigDecimal.valueOf(1.05));
                if (newBalance.compareTo(maxBalance) > 0) {
                    newBalance = maxBalance;
                }
                account.setBalance(newBalance);
                bankAccountRepository.save(account);
                log.info("Проценты начислены для счета с ID {}: новый баланс: {}", account.getId(), newBalance);
            }
        }
    }
}