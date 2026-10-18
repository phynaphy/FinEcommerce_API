package com.example.FIN_ecommerce_API.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String  code;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_recommended", nullable = false)
    private boolean isRecommended = false;

    @Column(name = "recommendation_order")
    private Integer recommendationOrder;
}