package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.checkout.CheckoutRequestDto;
import com.example.FIN_ecommerce_API.dto.checkout.OrderResponseDto;
import com.example.FIN_ecommerce_API.model.*;
import com.example.FIN_ecommerce_API.repository.CartRepository;
import com.example.FIN_ecommerce_API.repository.OrderRepository;
import com.example.FIN_ecommerce_API.repository.ProductVariantRepository;
import com.example.FIN_ecommerce_API.repository.UserRepository;

import com.example.FIN_ecommerce_API.service.CheckoutTestService;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutTestServiceImpl implements CheckoutTestService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponseDto processCheckout(CheckoutRequestDto request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Checkout request is required"
            );
        }

        if (request.getPhoneNumber() == null
                || request.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException(
                    "Phone number is required"
            );
        }

        if (request.getShippingAddress() == null
                || request.getShippingAddress().isBlank()) {
            throw new IllegalArgumentException(
                    "Shipping address is required"
            );
        }

        // 1. Get authenticated user
        User user = getCurrentUser();

        // 2. Get cart belonging to the authenticated user
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Cart not found"
                        )
                );

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "Your cart is empty"
            );
        }

        // 3. Validate stock and calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            if (cartItem.getQuantity() == null
                    || cartItem.getQuantity() < 1) {
                throw new IllegalArgumentException(
                        "Invalid cart item quantity"
                );
            }

            if (cartItem.getUnitPrice() == null
                    || cartItem.getUnitPrice().signum() < 0) {
                throw new IllegalArgumentException(
                        "Invalid cart item price"
                );
            }

            BigDecimal subtotal = cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            totalAmount = totalAmount.add(subtotal);
        }

        if (totalAmount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Order total must be greater than zero"
            );
        }

        if (totalAmount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Order total must be greater than zero"
            );
        }

        // 4. Create order using your EXISTING Order entity
        Order order = Order.builder()
                .user(user)
                .trackingNumber(generateTrackingNumber())
                .status("PENDING")
                .totalAmount(totalAmount)
                .shippingAddress(
                        request.getShippingAddress().trim()
                )
                .phoneNumber(request.getPhoneNumber().trim())
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .orderItems(new ArrayList<>())
                .build();

        // 5. Convert cart items into order items
        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = OrderItem.builder()
                    .product(cartItem.getProduct())
                    .variant(cartItem.getVariant())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .subtotal(
                            cartItem.getUnitPrice().multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            )
                    )
                    .build();
            order.getOrderItems().add(orderItem);
            orderItem.setOrder(order);
        }

        // 6. Save order and order items
        Order savedOrder = orderRepository.save(order);

        // 7. Deduct variant stock
        for (CartItem cartItem : cart.getItems()) {

            if (cartItem.getVariant() != null) {

                ProductVariant variant =
                        productVariantRepository.findById(
                                cartItem.getVariant().getId()
                        ).orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Product variant not found"
                                )
                        );

                variant.setStockQuantity(
                        variant.getStockQuantity()
                                - cartItem.getQuantity()
                );

                productVariantRepository.save(variant);
            }
        }

        // 8. Clear the cart
        cart.getItems().clear();
        cartRepository.save(cart);

        // 9. Return response
        return mapToResponse(savedOrder);
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(
                authentication.getPrincipal()
        )) {
            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User not found: " + username
                        )
                );
    }

    private String generateTrackingNumber() {
        return "ORD-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }

    private OrderResponseDto mapToResponse(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .trackingNumber(order.getTrackingNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .phoneNumber(order.getPhoneNumber())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .build();
    }
}