package com.example.FIN_ecommerce_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private Long variantId;
    private String color;
    private String storage;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}