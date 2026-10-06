package com.example.FIN_ecommerce_API.service;

import com.example.FIN_ecommerce_API.dto.request.CartItemRequestDto;
import com.example.FIN_ecommerce_API.dto.response.CartResponseDto;

public interface CartService {

    CartResponseDto getMyCart();

    CartResponseDto addToCart(
            CartItemRequestDto request
    );

    CartResponseDto updateCartItem(
            Long cartItemId,
            Integer quantity
    );

    CartResponseDto removeFromCart(
            Long cartItemId
    );

    void clearCart();
}