package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
}
