package com.example.FIN_ecommerce_API.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDto {

    private Long cartId;

    private List<CartItemResponseDto> items;

    private Integer totalItems;

    private BigDecimal totalPrice;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemResponseDto {

        private Long id;

        private Long productId;

        private String productName;

        private String mainImageUrl;

        private Long variantId;

        private String color;

        private String colorHex;

        private String storage;

        private Integer quantity;

        private BigDecimal unitPrice;

        private BigDecimal subtotal;
    }
}