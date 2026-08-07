package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetOtp(String resetOtp);
    Boolean existsByEmail(String email);
}