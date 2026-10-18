package com.example.FIN_ecommerce_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private Long orderId;
    private String trackingNumber;
    private String status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String phoneNumber;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDto> orderItems;
}