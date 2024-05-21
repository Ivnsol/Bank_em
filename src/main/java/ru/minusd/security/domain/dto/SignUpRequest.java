package ru.minusd.security.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на регистрацию")
public class SignUpRequest {
    @Schema(description = "Логин пользователя", example = "Jon")
    @Size(min = 5, max = 50, message = "Логин пользователя должен содержать от 5 до 50 символов")
    @NotBlank(message = "Логин пользователя не может быть пустым")
    private String userName;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    @Schema(description = "Депозит", example = "1000.00")
    @NotNull(message = "Депозит не должен быть пустым")
    @Positive(message = "Депозит должен быть положительным")
    private BigDecimal initialDeposit;

    @NotBlank(message = "Телефон не должен быть пустым")
    private String phone;

    @Schema(description = "Адрес электронной почты", example = "jondoe@gmail.com")
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустым")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;

    @Schema(description = "Дата рождения", example = "1990-01-01")
    @NotNull(message = "Дата рождения не может быть пустой")
    private LocalDate birthday;

    @Schema(description = "ФИО", example = "Рябина Тамара Валерьевна")
    @Size(min = 5, max = 255, message = "ФИО должно быть от 5 до 255 символов")
    private String fullName;
}