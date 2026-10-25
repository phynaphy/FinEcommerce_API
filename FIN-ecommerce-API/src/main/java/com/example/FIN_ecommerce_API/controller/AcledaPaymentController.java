package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.checkout.CheckoutPaymentResponseDto;
import com.example.FIN_ecommerce_API.model.Order;
import com.example.FIN_ecommerce_API.repository.OrderRepository;
import com.example.FIN_ecommerce_API.service.serviceImpl.AcledaService;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments/acleda")
@RequiredArgsConstructor
public class AcledaPaymentController {

    private final AcledaService acledaService;
    private final OrderRepository orderRepository;

    /**
     * Generate QR for the latest pending ACLEDA order
     * belonging to the authenticated user.
     *
     * POST /api/v1/payments/acleda/session
     *
     * Header:
     * Authorization: Bearer YOUR_JWT_TOKEN
     *
     * No request body required.
     */
    @PostMapping("/session")
    public ResponseEntity<?> createPaymentSession() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal() == null
                || "anonymousUser".equals(
                authentication.getPrincipal().toString())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Please log in before making payment"
                    ));
        }

        try {
            // 1. Get username from the validated JWT authentication.
            String username = authentication.getName();

            // 2. Find this user's latest pending ACLEDA order.
            Order order = orderRepository
                    .findFirstByUser_UsernameAndStatusAndPaymentMethodOrderByCreatedAtDesc(
                            username,
                            "PENDING",
                            "ACLEDA_KHQR"
                    )
                    .orElse(null);

            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "message",
                                "No pending ACLEDA order found. Please checkout first."
                        ));
            }

            // 3. Get the amount from the database.
            if (order.getTotalAmount() == null
                    || order.getTotalAmount().signum() <= 0) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "message",
                                "Invalid order total amount"
                        ));
            }

            if (order.getTrackingNumber() == null
                    || order.getTrackingNumber().isBlank()) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "message",
                                "Order tracking number is missing"
                        ));
            }

            // 4. Calculate total quantity from saved order items.
            int quantity = 1;

            if (order.getOrderItems() != null
                    && !order.getOrderItems().isEmpty()) {

                quantity = order.getOrderItems().stream()
                        .mapToInt(item -> item.getQuantity())
                        .sum();
            }

            if (quantity < 1) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "message",
                                "Order quantity is invalid"
                        ));
            }

            // 5. Generate ACLEDA identifiers.
            String txid = acledaService.generateTransactionId();
            String invoiceId = order.getTrackingNumber();

            String itemName = "Online order";

            if (order.getOrderItems() != null
                    && !order.getOrderItems().isEmpty()
                    && order.getOrderItems().get(0).getProduct() != null
                    && order.getOrderItems().get(0).getProduct().getName() != null) {

                itemName = order.getOrderItems()
                        .get(0)
                        .getProduct()
                        .getName();
            }

            // 6. Open the session with the database amount.
            // Never take the amount from the client.
            JsonNode acledaResponse = acledaService.openSession(
                    txid,
                    invoiceId,
                    order.getTotalAmount(),
                    "Payment for order " + invoiceId,
                    itemName,
                    quantity
            );

            JsonNode result = acledaResponse.path("result");

            String sessionId =
                    result.path("sessionid").asText(null);

            String qrValue =
                    result.path("qrValue").asText(null);

            String qrBase64 =
                    result.path("qrBase64").asText(null);

            String paymentTokenId =
                    result.path("xTran")
                            .path("paymentTokenid")
                            .asText(null);

            if (sessionId == null || sessionId.isBlank()
                    || qrValue == null || qrValue.isBlank()) {

                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(Map.of(
                                "message",
                                "ACLEDA did not return a valid QR session"
                        ));
            }

            // 7. Return order details and QR information.
            CheckoutPaymentResponseDto response =
                    CheckoutPaymentResponseDto.builder()
                            .orderId(order.getId())
                            .trackingNumber(order.getTrackingNumber())
                            .status(order.getStatus())
                            .totalAmount(order.getTotalAmount())
                            .shippingAddress(order.getShippingAddress())
                            .phoneNumber(order.getPhoneNumber())
                            .paymentMethod(order.getPaymentMethod())
                            .createdAt(order.getCreatedAt())
                            .txid(txid)
                            .sessionId(sessionId)
                            .paymentTokenId(paymentTokenId)
                            .qrValue(qrValue)
                            .qrBase64(qrBase64)
                            .paymentMessage(
                                    "Scan the ACLEDA KHQR to pay for your order."
                            )
                            .build();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of(
                            "message",
                            "Failed to create ACLEDA payment session",
                            "error",
                            e.getMessage() == null
                                    ? "Unknown error"
                                    : e.getMessage()
                    ));
        }
    }
}