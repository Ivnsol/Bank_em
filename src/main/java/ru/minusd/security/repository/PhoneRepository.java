package ru.minusd.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.minusd.security.domain.model.User;

import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<User.Phone, Long> {
    Optional<User> findByNumber(String phoneNumber);
}
