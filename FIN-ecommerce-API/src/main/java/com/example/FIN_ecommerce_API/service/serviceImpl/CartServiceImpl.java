package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.request.CartItemRequestDto;
import com.example.FIN_ecommerce_API.dto.response.CartResponseDto;
import com.example.FIN_ecommerce_API.model.Cart;
import com.example.FIN_ecommerce_API.model.CartItem;
import com.example.FIN_ecommerce_API.model.Product;
import com.example.FIN_ecommerce_API.model.ProductVariant;
import com.example.FIN_ecommerce_API.model.User;
import com.example.FIN_ecommerce_API.repository.CartItemRepository;
import com.example.FIN_ecommerce_API.repository.CartRepository;
import com.example.FIN_ecommerce_API.repository.ProductRepository;
import com.example.FIN_ecommerce_API.repository.ProductVariantRepository;
import com.example.FIN_ecommerce_API.repository.UserRepository;
import com.example.FIN_ecommerce_API.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;


    // =========================================================
    // GET MY CART
    // =========================================================

    @Override
    @Transactional
    public CartResponseDto getMyCart() {

        User user = getCurrentUser();

        Cart cart = cartRepository.findByUserUsername(user.getUsername())
                .orElseGet(() -> createCart(user));

        return mapToResponse(cart);
    }


    // =========================================================
    // ADD TO CART
    // =========================================================

    @Override
    @Transactional
    public CartResponseDto addToCart(
            CartItemRequestDto request
    ) {

        User user = getCurrentUser();

        // Find product
        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Product not found with id: "
                                        + request.getProductId()
                        )
                );


        // Find or create cart
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createCart(user));


        // =====================================================
        // FIND VARIANT
        // =====================================================

        ProductVariant variant = null;

        if (request.getVariantId() != null) {

            variant = productVariantRepository
                    .findById(request.getVariantId())
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "Variant not found with id: "
                                            + request.getVariantId()
                            )
                    );


            // Make sure variant belongs to product
            if (!variant.getProduct()
                    .getId()
                    .equals(product.getId())) {

                throw new IllegalArgumentException(
                        "Selected variant does not belong to product"
                );
            }


            // Check stock
            if (variant.getStockQuantity()
                    < request.getQuantity()) {

                throw new IllegalArgumentException(
                        "Not enough stock"
                );
            }
        }


        // =====================================================
        // CALCULATE PRICE
        // =====================================================

        BigDecimal unitPrice = product.getPrice();

        if (variant != null
                && variant.getPriceAdjustment() != null) {

            unitPrice = unitPrice.add(
                    variant.getPriceAdjustment()
            );
        }


        // =====================================================
        // CHECK EXISTING CART ITEM
        // =====================================================

        CartItem existingItem;

        if (variant != null) {

            existingItem = cartItemRepository
                    .findByCartIdAndProductIdAndVariantId(
                            cart.getId(),
                            product.getId(),
                            variant.getId()
                    )
                    .orElse(null);

        } else {

            existingItem = cartItemRepository
                    .findByCartIdAndProductIdAndVariantIsNull(
                            cart.getId(),
                            product.getId()
                    )
                    .orElse(null);
        }


        // =====================================================
        // UPDATE EXISTING ITEM
        // =====================================================

        if (existingItem != null) {

            int newQuantity =
                    existingItem.getQuantity()
                            + request.getQuantity();


            if (variant != null
                    && variant.getStockQuantity()
                    < newQuantity) {

                throw new IllegalArgumentException(
                        "Not enough stock"
                );
            }


            existingItem.setQuantity(newQuantity);
            existingItem.setUnitPrice(unitPrice);

            cartItemRepository.save(existingItem);

        }

        // =====================================================
        // ADD NEW ITEM
        // =====================================================

        else {

            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .variant(variant)
                    .quantity(request.getQuantity())
                    .unitPrice(unitPrice)
                    .build();

            cartItemRepository.save(cartItem);
        }


        return mapToResponse(cart);
    }


    // =========================================================
    // UPDATE QUANTITY
    // =========================================================

    @Override
    @Transactional
    public CartResponseDto updateCartItem(Long cartItemId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException(
                    "Quantity must be at least 1"
            );
        }

        User user = getCurrentUser();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id: " + cartItemId));

        // Security check:
        // Cart item must belong to current user
        if (!cartItem.getCart()
                .getUser()
                .getId()
                .equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You cannot update this cart item"
            );
        }
        ProductVariant variant = cartItem.getVariant();
        // Check stock
        if (variant != null && variant.getStockQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Not enough stock"
            );
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return mapToResponse(cartItem.getCart());
    }


    // =========================================================
    // REMOVE ITEM
    // =========================================================


    @Override
    @Transactional
    public CartResponseDto removeFromCart(Long cartItemId) {
        User user = getCurrentUser();
        CartItem cartItem = cartItemRepository
                .findById(cartItemId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Cart item not found with id: " + cartItemId
                        )
                );
        // Security check
        if (!cartItem.getCart()
                .getUser()
                .getId()
                .equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You cannot remove this cart item"
            );
        }
        Cart cart = cartItem.getCart();
        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        return mapToResponse(cart);
    }

//    public CartResponseDto removeFromCart(Long cartItemId) {
//        User user = getCurrentUser();
//        CartItem cartItem = cartItemRepository
//                .findById(cartItemId)
//                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
//        // Security check
//        if (!cartItem.getCart()
//                .getUser()
//                .getId()
//                .equals(user.getId())) {
//            throw new IllegalArgumentException("You cannot remove this cart item");
//        }
//        Cart cart = cartItem.getCart();
//        cartItemRepository.delete(cartItem);
//        return mapToResponse(cart);
//    }


    // =========================================================
    // CLEAR CART
    // =========================================================

    @Override
    @Transactional
    public void clearCart() {
        User user = getCurrentUser();
        Cart cart = cartRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Cart not found"
                        )
                );

        cart.getItems().clear();
        cartRepository.save(cart);
    }


    // =========================================================
    // CREATE CART
    // =========================================================
    private Cart createCart(User user) {

        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build();

        return cartRepository.save(cart);
    }


    // =========================================================
    // CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }

        String email = authentication.getName();
        return userRepository
                .findByUsername(email)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User not found with email: "
                                        + email
                        )
                );
    }


    // =========================================================
    // MAP RESPONSE
    // =========================================================

    private CartResponseDto mapToResponse(Cart cart) { List<CartResponseDto.CartItemResponseDto> items =
                cart.getItems()
                        .stream()
                        .map(this::mapCartItem)
                        .toList();
        Integer totalItems = items.stream().mapToInt(
                        CartResponseDto.CartItemResponseDto
                        ::getQuantity).sum();


        BigDecimal totalPrice = items.stream()
                .map(
                        CartResponseDto.CartItemResponseDto
                                ::getSubtotal
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );


        return CartResponseDto.builder()
                .cartId(cart.getId())
                .items(items)
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .build();
    }


    private CartResponseDto.CartItemResponseDto mapCartItem(
            CartItem item
    ) {

        Product product = item.getProduct();

        ProductVariant variant = item.getVariant();


        BigDecimal subtotal =
                item.getUnitPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        item.getQuantity()
                                )
                        );


        return CartResponseDto.CartItemResponseDto
                .builder()
                .id(item.getId())

                .productId(product.getId())
                .productName(product.getName())
                .mainImageUrl(product.getMainImageUrl())

                .variantId(
                        variant != null
                                ? variant.getId()
                                : null
                )

                .color(
                        variant != null
                                ? variant.getColor()
                                : null
                )

                .colorHex(
                        variant != null
                                ? variant.getColorHex()
                                : null
                )

                .storage(
                        variant != null
                                ? variant.getStorage()
                                : null
                )

                .quantity(item.getQuantity())

                .unitPrice(item.getUnitPrice())

                .subtotal(subtotal)

                .build();
    }
}