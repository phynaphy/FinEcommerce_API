//package com.example.FIN_ecommerce_API.service;
//
//import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
//import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//public interface ProductService {
//
//    List<ProductResponseDto> getAllProducts();
//
//    List<ProductResponseDto> getProductsByCategory(Long categoryId);
//
//    ProductResponseDto getProductById(Long id);
//
//    ProductResponseDto createProduct(ProductRequestDto requestDto);
//
//    @Transactional
//    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);
//
//    void deleteProduct(Long id);
//}

package com.example.FIN_ecommerce_API.service;

import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<ProductResponseDto> getAllProducts();
    List<ProductResponseDto> getProductsByCategory(Long categoryId);
    ProductResponseDto getProductById(Long id);

    ProductResponseDto createProduct(ProductRequestDto requestDto, MultipartFile mainImage, List<MultipartFile> galleryImages);
    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto, MultipartFile mainImage, List<MultipartFile> galleryImages);

    void deleteProduct(Long id);
}