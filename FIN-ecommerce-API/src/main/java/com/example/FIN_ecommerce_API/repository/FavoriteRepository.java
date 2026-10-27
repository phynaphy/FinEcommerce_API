package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.Favorite;
import com.example.FIN_ecommerce_API.model.User;
import com.example.FIN_ecommerce_API.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserAndProduct(User user, Product product);
    boolean existsByUserAndProduct(User user, Product product);
    void deleteByUserAndProduct(User user, Product product);
}