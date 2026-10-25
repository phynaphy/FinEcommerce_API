package com.example.FIN_ecommerce_API.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutPaymentResponseDto {

    // Order information
    private Long orderId;

    private String trackingNumber;

    private String status;

    private BigDecimal totalAmount;

    private String shippingAddress;

    private String phoneNumber;

    private String paymentMethod;

    private LocalDateTime createdAt;

    private List<OrderItemResponseDto> orderItems;

    // ACLEDA payment information
    private String txid;

    private String sessionId;

    private String paymentTokenId;

    private String qrValue;

    private String qrBase64;

    private String paymentMessage;
}
