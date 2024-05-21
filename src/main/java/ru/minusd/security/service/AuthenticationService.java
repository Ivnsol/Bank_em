package ru.minusd.security.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.minusd.security.domain.dto.JwtAuthenticationResponse;
import ru.minusd.security.domain.dto.SignInRequest;
import ru.minusd.security.domain.dto.SignUpRequest;
import ru.minusd.security.domain.model.Role;
import ru.minusd.security.domain.model.User;
import ru.minusd.security.domain.model.BankAccount;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        log.info("Выполняется регистрация пользователя...");

        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(request.getInitialDeposit());
        bankAccount.setInitialDeposit(request.getInitialDeposit());

        log.debug("Создан банковский счет для пользователя: {}", request.getUserName());

        var user = User.builder()
                .username(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .bankAccount(bankAccount)
                .birthday(request.getBirthday())
                .fullName(request.getFullName())
                .role(Role.ROLE_USER)
                .build();

        bankAccount.setUser(user);
        user.addEmail(request.getEmail());
        user.addPhone(request.getPhone());
        bankAccount.setUser(user);

        userService.create(user);

        log.info("Пользователь успешно зарегистрирован: {}", request.getUserName());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        log.info("Выполняется аутентификация пользователя...");

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        log.debug("Пользователь {} успешно прошел аутентификацию", request.getUsername());

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        var jwt = jwtService.generateToken(user);

        log.info("Пользователь {} успешно аутентифицирован", request.getUsername());

        return new JwtAuthenticationResponse(jwt);
    }
}