package com.example.FIN_ecommerce_API.config;

import com.example.FIN_ecommerce_API.model.User;
import com.example.FIN_ecommerce_API.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String rawRole = user.getRole().name(); // e.g. "ADMIN"
        String roleWithPrefix = rawRole.startsWith("ROLE_") ? rawRole : "ROLE_" + rawRole;
        String authorityWithoutPrefix = rawRole.replace("ROLE_", "");

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(
                        new SimpleGrantedAuthority(roleWithPrefix),        // Works for hasRole('ADMIN')
                        new SimpleGrantedAuthority(authorityWithoutPrefix)  // Works for hasAuthority('ADMIN')
                )
        );
    }
}