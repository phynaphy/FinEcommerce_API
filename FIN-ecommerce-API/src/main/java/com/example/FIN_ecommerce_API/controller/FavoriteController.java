package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.response.FavoriteResponse;
import com.example.FIN_ecommerce_API.service.FavoriteService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constant.MAIN_PATH + "/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{username}/{productId}")
    public ResponseEntity<FavoriteResponse> addFavorite(
            @PathVariable String username,
            @PathVariable Long productId) {
        FavoriteResponse response = favoriteService.addFavorite(username, productId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}/{productId}")
    public ResponseEntity<String> removeFavorite(
            @PathVariable String username,
            @PathVariable Long productId) {
        favoriteService.removeFavorite(username, productId);
        return ResponseEntity.ok("Product removed from favorites successfully.");
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<FavoriteResponse>> getUserFavorites(
            @PathVariable String username) {
        List<FavoriteResponse> favorites = favoriteService.getUserFavorites(username);
        return ResponseEntity.ok(favorites);
    }
}