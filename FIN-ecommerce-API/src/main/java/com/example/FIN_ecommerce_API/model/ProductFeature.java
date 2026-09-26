package com.example.FIN_ecommerce_API.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String featureKey; // e.g., "Display", "Processor", "Camera", "Battery"

    @Column(nullable = false)
    private String featureValue; // e.g., "6.8\" AMOLED 2X", "5000 mAh"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}