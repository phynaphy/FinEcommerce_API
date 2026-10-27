package com.example.FIN_ecommerce_API.service;

import com.example.FIN_ecommerce_API.dto.response.FavoriteResponse;
import java.util.List;

public interface FavoriteService {
    FavoriteResponse addFavorite(String username, Long productId);
    void removeFavorite(String username, Long productId);
    List<FavoriteResponse> getUserFavorites(String username);
}