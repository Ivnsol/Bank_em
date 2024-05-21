package ru.minusd.security.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.minusd.security.domain.model.Role;
import ru.minusd.security.domain.model.User;
import ru.minusd.security.repository.EmailRepository;
import ru.minusd.security.repository.PhoneRepository;
import ru.minusd.security.repository.UserRepository;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PhoneRepository phoneRepository;
    private final EmailRepository emailRepository;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Transactional
    public User get(Long id) {
        log.debug("Получение пользователя по идентификатору: {}", id);
        Optional<User> user = repository.findById(id);
        return user.orElse(null);
    }

    @Transactional
    public User create(User user) {
        log.debug("Создание нового пользователя: {}", user.getUsername());
        return repository.save(user);
    }

    @Transactional
    public UserDetailsService userDetailsService() {
        log.debug("Получение сервиса деталей пользователя");
        return this::getBylogin;
    }

    public User getCurrentUser() {
        log.debug("Получение текущего пользователя");
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getBylogin(username);
    }

    @Deprecated
    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ROLE_ADMIN);
        repository.save(user);
    }

    @Transactional
    public User getBylogin(String userName) {
        log.debug("Получение пользователя по имени: {}", userName);
        Optional<User> user = repository.findByUsername(userName);
        return user.orElse(null);
    }

    @Transactional
    public User updatePhone(Long userId, String phoneNumber, Boolean updateOrChange) {
        log.debug("Обновление или добавление номера телефона пользователя: {}", userId);
        User user = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (phoneRepository.findByNumber(phoneNumber).isEmpty()) {
            throw new IllegalArgumentException("Номер телефона уже используется");
        }

        if (updateOrChange) {
            log.debug("Обновление телефонного номера пользователя");
            User.Phone phone = new User.Phone();
            phone.setNumber(phoneNumber);
            phone.setUser(user);

            user.getPhones().clear();
            user.getPhones().add(phone);
        } else { // Если updateOrChange равно false, значит нужно добавить новый телефонный номер
            log.debug("Добавление нового телефонного номера пользователя");
            User.Phone phone = new User.Phone();
            phone.setNumber(phoneNumber);
            phone.setUser(user);

            user.getPhones().add(phone);
        }
        return repository.save(user);
    }

    @Transactional
    public User updateEmail(Long userId, String newEmail, Boolean updateOrChange) {
        log.debug("Обновление или добавление email пользователя: {}", userId);
        if (emailRepository.findByEmail(newEmail).isEmpty()) {
            throw new IllegalArgumentException("Email уже используется");
        }

        User user = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (updateOrChange) {
            log.debug("Обновление email пользователя");
            User.UserEmail email = new User.UserEmail();
            email.setEmail(newEmail);
            email.setUser(user);

            user.getEmails().clear();
            user.getEmails().add(email);
        } else {
            log.debug("Добавление нового email пользователя");
            User.UserEmail email = new User.UserEmail();
            email.setEmail(newEmail);
            email.setUser(user);

            user.getEmails().add(email);
        }
        return repository.save(user);
    }

    @Transactional
    public User deletePhone(Long userId, String phoneNumber) {
        log.debug("Удаление телефонного номера пользователя: {}", userId);
        User user = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (user.getPhones().size() == 1) {
            throw new IllegalArgumentException("Нельзя удалить последний телефонный номер");
        }

        boolean removePhone = false;
        Iterator<User.Phone> phoneIterator = user.getPhones().iterator();
        while (phoneIterator.hasNext()) {
            User.Phone phone = phoneIterator.next();
            if (phone.getNumber().equals(phoneNumber)) {
                phoneIterator.remove();
                removePhone = true;
                break;
            }
        }

        if (removePhone) {
            throw new IllegalArgumentException("Телефонный номер не найден");
        }
        return repository.save(user);
    }

    @Transactional
    public User deleteEmail(Long userId, String email) {
        log.debug("Удаление email пользователя: {}", userId);
        User user = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (user.getEmails().size() == 1) {
            throw new IllegalArgumentException("Нельзя удалить последний email");
        }

        boolean removeEmail = false;
        Iterator<User.UserEmail> emailIterator = user.getEmails().iterator();
        while (emailIterator.hasNext()) {
            User.UserEmail userEmail = emailIterator.next();
            if (userEmail.getEmail().equals(email)) {
                emailIterator.remove();
                removeEmail = true;
                break;
            }
        }

        if (!removeEmail) {
            throw new IllegalArgumentException("Email не найден");
        }
        return repository.save(user);
    }

    @Transactional
    public Page<User> searchByBirthday(LocalDate birthday, Pageable pageable) {
        log.debug("Поиск пользователей по дню рождения: {}", birthday);
        return repository.findByBirthdayAfter(birthday, pageable);
    }
    @Transactional
    public User searchByPhone(String phone) {
        log.debug("Поиск пользователя по номеру телефона: {}", phone);
        Optional<User> user = phoneRepository.findByNumber(phone);
        return user.orElse(null);
    }
    @Transactional
    public Page<User> searchByFullName(String fullName, Pageable pageable) {
        log.debug("Поиск пользователя по имени: {}", fullName);
        return repository.findByFullNameIsLike(fullName, pageable);
    }

    @Transactional
    public User searchByEmail(String email) {
        log.debug("Поиск пользователя по email: {}", email);
        Optional<User> user = emailRepository.findByEmail(email);
        return user.orElse(null);
    }
}

