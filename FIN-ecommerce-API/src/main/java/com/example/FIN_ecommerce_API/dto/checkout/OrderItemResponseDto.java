
package com.example.FIN_ecommerce_API.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {

    private Long orderItemId;

    private Long productId;

    private String productName;

    private Long variantId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;
}
