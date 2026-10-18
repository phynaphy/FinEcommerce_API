package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    Optional<Order> findByTrackingNumber(String trackingNumber);
}