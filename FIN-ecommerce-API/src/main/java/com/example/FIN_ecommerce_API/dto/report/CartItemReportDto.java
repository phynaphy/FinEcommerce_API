package com.example.FIN_ecommerce_API.dto.report;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CartItemReportDto {
    private Long cartItemId;
    private Long cartId;
    private Long userId;
    private String username;
    private Long productId;
    private String productName;
    private Long variantId;
    private String color;
    private String storage;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
