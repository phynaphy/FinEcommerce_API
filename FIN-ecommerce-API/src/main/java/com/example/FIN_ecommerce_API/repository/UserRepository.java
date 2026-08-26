package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.dto.response.UserResponse;
import com.example.FIN_ecommerce_API.model.Role;
import com.example.FIN_ecommerce_API.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetOtp(String resetOtp);
    Boolean existsByEmail(String email);

    Optional<User> findByIdAndRole(Long id, Role role);
    List<User> findByRole(Role role);

}