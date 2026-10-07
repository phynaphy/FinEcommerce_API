package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AdminReportVariantRepository extends JpaRepository<ProductVariant, Long> {
    @Query("select v from ProductVariant v join fetch v.product p order by v.stockQuantity asc, v.id desc")
    List<ProductVariant> findAllForReport();
    long countByStockQuantityLessThanEqual(Integer stockQuantity);
    @Query("select coalesce(sum(v.stockQuantity), 0) from ProductVariant v")
    Long sumStockQuantity();
}
