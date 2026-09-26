package com.example.FIN_ecommerce_API.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String color; // e.g., "Titanium Gray"

    @Column(name = "color_hex")
    private String colorHex; // e.g., "#5A5B60" for UI rendering

    @Column(nullable = false)
    private String storage; // e.g., "256GB", "512GB", "1TB"

    @Column(name = "price_adjustment", precision = 10, scale = 2)
    private BigDecimal priceAdjustment; // e.g., 0.00, 120.00, 300.00

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}