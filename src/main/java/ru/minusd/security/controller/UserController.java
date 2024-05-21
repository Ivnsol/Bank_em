package ru.minusd.security.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.minusd.security.domain.model.User;
import ru.minusd.security.service.UserService;

import java.time.LocalDate;

@RestController
@RequestMapping("/example")
@RequiredArgsConstructor
@Tag(name = "Примеры", description = "Примеры запросов")
public class UserController {
    private final UserService service;

//    @GetMapping("/admin")
//    @Operation(summary = "Доступен только авторизованным пользователям с ролью ADMIN")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String exampleAdmin() {
//        return "Hello, admin!";
//    }

//    @GetMapping("/get-admin")
//    @Operation(summary = "Получить роль ADMIN")
//    public void getAdmin() {
//        service.getAdmin();
//    }

    @GetMapping
    public User getByLogin(@RequestParam String login) {
        return service.getBylogin(login);
    }

    @PostMapping("/{userId}/updatePhone")
    public ResponseEntity<User> updatePhone(@PathVariable Long userId, @RequestParam @NotBlank String newPhone,
                                            @RequestParam Boolean updateOrChange) {
        User updatedUser = service.updatePhone(userId, newPhone, updateOrChange);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/{userId}/updateEmail")
    public ResponseEntity<User> updateEmail(@PathVariable Long userId, @RequestParam @Email String newEmail,
                                            @RequestParam Boolean updateOrChange) {
        User updatedUser = service.updateEmail(userId, newEmail, updateOrChange);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}/phones/{phoneNumber}")
    public ResponseEntity<User> deletePhone(@PathVariable Long userId, @RequestParam String phoneNumber) {
        User updatedUser = service.deletePhone(userId, phoneNumber);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}/email")
    public ResponseEntity<User> deleteEmail(@PathVariable Long userId, @RequestParam String email) {
        User updatedUser = service.deleteEmail(userId, email);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/searchByBirthday")
    public Page<User> searchByBirthday(@RequestParam LocalDate birthday, Pageable pageable) {
        return service.searchByBirthday(birthday, pageable);
    }

    @GetMapping("/searchByPhone")
    public User searchByPhone(@RequestParam String phone) {
        return service.searchByPhone(phone);
    }

    @GetMapping("/searchByFullName")
    public Page<User> searchByFullName(@RequestParam String fullName, Pageable pageable) {
        return service.searchByFullName(fullName, pageable);
    }

    @GetMapping("/searchByEmail")
    public User searchByEmail(@RequestParam String email) {
        return service.searchByEmail(email);
    }
}