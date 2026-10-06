package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.Cart;
import com.example.FIN_ecommerce_API.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository
        extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserUsername(String username);

}