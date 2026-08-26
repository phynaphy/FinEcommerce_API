package com.example.FIN_ecommerce_API.service;

import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductAdminService {
    List<ProductResponseDto> getAllProducts();
    List<ProductResponseDto> getProductsByCategory(Long categoryId);
    ProductResponseDto getProductById(Long id);
    ProductResponseDto createProduct(ProductRequestDto requestDto);
    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);
    void deleteProduct(Long id);
}
