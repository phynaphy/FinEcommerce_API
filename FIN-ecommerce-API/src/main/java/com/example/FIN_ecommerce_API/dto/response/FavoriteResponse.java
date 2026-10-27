package com.example.FIN_ecommerce_API.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FavoriteResponse {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String mainImageUrl;
    private Double rating;
    private LocalDateTime createdAt;
}