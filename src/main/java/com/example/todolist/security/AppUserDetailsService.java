package com.example.todolist.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private Map<String, String> encodedPasswords;
    private Map<String, List<SimpleGrantedAuthority>> authoritiesMap;

    @PostConstruct
    void init() {
        String encoded = passwordEncoder.encode("password");

        encodedPasswords = Map.of("user", encoded, "reader", encoded);

        authoritiesMap = Map.of(
                "user", List.of(
                        new SimpleGrantedAuthority("ROLE_USER")
                ),
                "reader", List.of(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("READ_PRIVILEGE")
                )
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password = encodedPasswords == null ? null : encodedPasswords.get(username);
        if (password == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return User.builder()
                .username(username)
                .password(password)
                .authorities(authoritiesMap.get(username))
                .build();
    }
}
