package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AdminReportCartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("select ci from CartItem ci join fetch ci.cart c join fetch c.user u join fetch ci.product p left join fetch ci.variant v order by ci.id desc")
    List<CartItem> findAllForReport();
}
