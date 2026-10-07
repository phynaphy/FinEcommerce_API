package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;

public interface AdminReportCartRepository extends JpaRepository<Cart, Long> {
    @Query("select count(ci) from Cart c join c.items ci")
    long countCartItems();
    @Query("select coalesce(sum(ci.quantity), 0) from Cart c join c.items ci")
    Long sumCartQuantity();
    @Query("select coalesce(sum(ci.unitPrice * ci.quantity), 0) from Cart c join c.items ci")
    BigDecimal sumCartValue();
}
