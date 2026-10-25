package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.checkout.CheckoutPaymentResponseDto;
import com.example.FIN_ecommerce_API.dto.checkout.CheckoutRequestDto;
import com.example.FIN_ecommerce_API.dto.checkout.OrderResponseDto;
import com.example.FIN_ecommerce_API.service.CheckoutTestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutTestService checkoutService;

    @PostMapping
    public ResponseEntity<?> checkout(
            @Valid @RequestBody CheckoutRequestDto request) {

        if (request.getPaymentMethod() == null
                || request.getPaymentMethod().isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", "Payment method is required")
            );
        }

        String paymentMethod = request.getPaymentMethod()
                .trim()
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);

        if (!"ACLEDA_KHQR".equals(paymentMethod)) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Unsupported payment method")
            );
        }

        request.setPaymentMethod(paymentMethod);

        // The service gets the authenticated user's cart,
        // calculates its total, and saves the order.
        OrderResponseDto order =
                checkoutService.processCheckout(request);

        CheckoutPaymentResponseDto response =
                CheckoutPaymentResponseDto.builder()
                        .orderId(order.getOrderId())
                        .trackingNumber(order.getTrackingNumber())
                        .status(order.getStatus())
                        .totalAmount(order.getTotalAmount())
                        .shippingAddress(order.getShippingAddress())
                        .phoneNumber(order.getPhoneNumber())
                        .paymentMethod(order.getPaymentMethod())
                        .createdAt(order.getCreatedAt())
                        .orderItems(order.getOrderItems())
                        .paymentMessage(
                                "Order created. Click Pay Now to generate your ACLEDA KHQR."
                        )
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}