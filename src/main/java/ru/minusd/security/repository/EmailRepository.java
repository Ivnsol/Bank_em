package ru.minusd.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.minusd.security.domain.model.User;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<User.UserEmail, Long> {
    Optional<User> findByEmail(String email);
}
