package ru.minusd.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.minusd.security.domain.model.BankAccount;
import ru.minusd.security.domain.model.User;
import ru.minusd.security.repository.BankAccountRepository;
import ru.minusd.security.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransferService {
    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Transactional
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        log.debug("Начало трансфера средств");

        Optional<User> fromUserOpt = userRepository.findById(fromUserId);
        Optional<User> toUserOpt = userRepository.findById(toUserId);

        if (fromUserOpt.isPresent() && toUserOpt.isPresent()) {
            User fromUser = fromUserOpt.get();
            User toUser = toUserOpt.get();
            BankAccount fromAccount = fromUser.getBankAccount();
            BankAccount toAccount = toUser.getBankAccount();

            if (fromAccount.getBalance().compareTo(amount) >= 0) {
                log.debug("Перевод средств между пользователями");
                fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
                toAccount.setBalance(toAccount.getBalance().add(amount));
                bankAccountRepository.save(fromAccount);
                bankAccountRepository.save(toAccount);
                log.debug("Трансфер завершен успешно");
            } else {
                log.error("Недостаточно средств на счете");
                throw new IllegalArgumentException("Недостаточно средств");
            }
        } else {
            log.error("Пользователь не найден");
            throw new IllegalArgumentException("Пользователь не найден");
        }
    }
}
