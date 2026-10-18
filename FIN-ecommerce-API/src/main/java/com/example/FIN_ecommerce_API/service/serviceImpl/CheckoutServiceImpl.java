package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.request.CheckoutRequestDto;
import com.example.FIN_ecommerce_API.dto.response.OrderItemResponseDto;
import com.example.FIN_ecommerce_API.dto.response.OrderResponseDto;
import com.example.FIN_ecommerce_API.model.*;
import com.example.FIN_ecommerce_API.repository.*;
import com.example.FIN_ecommerce_API.service.CheckoutService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponseDto processCheckout(CheckoutRequestDto request) {
        User user = getCurrentUser();

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart is empty or not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart");
        }

        // Check stock availability
        for (CartItem item : cart.getItems()) {
            if (item.getVariant() != null) {
                ProductVariant variant = item.getVariant();
                if (variant.getStockQuantity() < item.getQuantity()) {
                    throw new IllegalArgumentException("Not enough stock for variant ID: " + variant.getId());
                }
            }
        }

        // Build Order entity
        Order order = Order.builder()
                .user(user)
                .trackingNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status("PENDING")
                .shippingAddress(request.getShippingAddress())
                .phoneNumber(request.getPhoneNumber())
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .orderItems(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            BigDecimal subtotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            // Deduct stock
            if (cartItem.getVariant() != null) {
                ProductVariant variant = cartItem.getVariant();
                variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
                productVariantRepository.save(variant);
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .variant(cartItem.getVariant())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .subtotal(subtotal)
                    .build();

            order.getOrderItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Clear user cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public void updateOrderStatusByTrackingNumber(String trackingNumber, String status) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + trackingNumber));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getMyOrders() {
        User user = getCurrentUser();
        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByUsername(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    private OrderResponseDto mapToOrderResponse(Order order) {
        List<OrderItemResponseDto> itemResponses = order.getOrderItems().stream().map(item ->
                OrderItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                        .color(item.getVariant() != null ? item.getVariant().getColor() : null)
                        .storage(item.getVariant() != null ? item.getVariant().getStorage() : null)
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build()
        ).toList();

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .trackingNumber(order.getTrackingNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .phoneNumber(order.getPhoneNumber())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .orderItems(itemResponses)
                .build();
    }
}