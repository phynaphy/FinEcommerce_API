package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.response.FavoriteResponse;
import com.example.FIN_ecommerce_API.model.Favorite;
import com.example.FIN_ecommerce_API.model.Product;
import com.example.FIN_ecommerce_API.model.User;
import com.example.FIN_ecommerce_API.repository.FavoriteRepository;
import com.example.FIN_ecommerce_API.repository.ProductRepository;
import com.example.FIN_ecommerce_API.repository.UserRepository;
import com.example.FIN_ecommerce_API.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public FavoriteResponse addFavorite(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            throw new IllegalArgumentException("Product is already in favorites.");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .product(product)
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);
        return mapToResponse(savedFavorite);
    }

    @Override
    @Transactional
    public void removeFavorite(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Favorite favorite = favoriteRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new RuntimeException("Favorite not found for this product."));

        favoriteRepository.delete(favorite);
    }

    @Override
    public List<FavoriteResponse> getUserFavorites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Favorite> favorites = favoriteRepository.findByUser(user);
        return favorites.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FavoriteResponse mapToResponse(Favorite favorite) {
        Product p = favorite.getProduct();
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .productId(p.getId())
                .productName(p.getName())
                .price(p.getPrice())
                .originalPrice(p.getOriginalPrice())
                .mainImageUrl(p.getMainImageUrl())
                .rating(p.getRating())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}