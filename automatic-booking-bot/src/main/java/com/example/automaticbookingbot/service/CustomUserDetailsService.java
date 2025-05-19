package com.example.automaticbookingbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.automaticbookingbot.entity.User;
import com.example.automaticbookingbot.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailInput) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email from login form: '{}'", emailInput);

        if (emailInput == null || emailInput.trim().isEmpty()) {
            log.warn("Email input from login form is empty or null. Authentication will fail.");
            throw new UsernameNotFoundException("Email (from login form) cannot be empty.");
        }

        User user = userRepository.findByEmail(emailInput)
                .orElseThrow(() -> {
                    log.warn("User not found in DB with email: '{}'", emailInput);
                    return new UsernameNotFoundException("User not found with email: " + emailInput);
                });

        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
        log.debug("Authorities for email '{}': {}", emailInput, authorities);

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.isEnabled(),
                        true, // accountNonExpired
                        true, // credentialsNonExpired
                        true, // accountNonLocked
                        authorities);

        log.debug("Returning Spring Security UserDetails for '{}': email='{}', Enabled={}, Authorities={}",
                emailInput, springUser.getUsername(), springUser.isEnabled(), springUser.getAuthorities());
        return springUser;
    }
}
