package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.request.CartItemRequestDto;
import com.example.FIN_ecommerce_API.dto.response.CartResponseDto;
import com.example.FIN_ecommerce_API.service.CartService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constant.MAIN_PATH + "/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    // GET CART
    @GetMapping
    public ResponseEntity<CartResponseDto> getMyCart() {
        return ResponseEntity.ok(
                cartService.getMyCart()
        );
    }

    // ADD TO CART
    @PostMapping("/add")
    public ResponseEntity<CartResponseDto> addToCart(
            @Valid @RequestBody CartItemRequestDto request
    ) {
        return ResponseEntity.ok(
                cartService.addToCart(request)
        );
    }

    // UPDATE QUANTITY
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(
                cartService.updateCartItem(
                        cartItemId,
                        quantity
                )
        );
    }

    // REMOVE ITEM
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> removeFromCart(
            @PathVariable Long cartItemId
    ) {

        return ResponseEntity.ok(
                cartService.removeFromCart(cartItemId)
        );
    }

    // CLEAR CART
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {

        cartService.clearCart();

        return ResponseEntity.noContent().build();
    }
}