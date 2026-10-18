package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AdminReportCartRepository extends JpaRepository<Cart, Long> {
    @Query("select count(ci) from Cart c join c.items ci")
    long countCartItems();
    @Query("select coalesce(sum(ci.quantity), 0) from Cart c join c.items ci")
    Long sumCartQuantity();
    @Query("select coalesce(sum(ci.unitPrice * ci.quantity), 0) from Cart c join c.items ci")
    BigDecimal sumCartValue();

    // Time-filtered queries based on Cart creation date
    @Query("SELECT SUM(ci.unitPrice * ci.quantity) FROM CartItem ci JOIN ci.cart c WHERE c.createdAt >= :start")
    BigDecimal sumCartValueAfter(@Param("start") LocalDateTime start);

    @Query("SELECT SUM(ci.unitPrice * ci.quantity) FROM CartItem ci JOIN ci.cart c WHERE c.createdAt >= :start AND c.createdAt < :end")
    BigDecimal sumCartValueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(c) FROM Cart c WHERE c.createdAt >= :start AND c.createdAt < :end")
    long countCartsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
