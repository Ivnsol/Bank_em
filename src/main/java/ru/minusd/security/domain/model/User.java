package ru.minusd.security.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEmail> emails = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BankAccount bankAccount;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "fullName")
    private String fullName;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public void addPhone(String number) {
        Phone phone = new Phone(number, this);
        if (phones == null) {
            phones = new ArrayList<>();
            phones.add(phone);
        } else {
            phones.add(phone);
        }
    }

    public void addEmail(String emailAddress) {
        UserEmail email = new UserEmail(emailAddress, this);
        if (emails == null) {
            emails = new ArrayList<>();
            emails.add(email);
        } else {
            emails.add(email);
        }
    }

    @Entity
    @Data
    @NoArgsConstructor
    @Table(name = "phone")
    public static class Phone {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "phone_id")
        private Long id;

        @Column(name = "number", unique = true, nullable = false)
        private String number;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private User user;

        public Phone(String number, User user) {
            this.number = number;
            this.user = user;
        }
    }

    @Entity
    @Data
    @NoArgsConstructor
    @Table(name = "user_email")
    public static class UserEmail {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "email_id")
        private Long id;

        @Column(name = "email", unique = true, nullable = false)
        private String email;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private User user;

        public UserEmail(String email, User user) {
            this.email = email;
            this.user = user;
        }
    }
}
