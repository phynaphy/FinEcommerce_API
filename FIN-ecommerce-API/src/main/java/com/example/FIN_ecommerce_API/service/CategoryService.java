//package com.example.FIN_ecommerce_API.service;
//
//
//import com.example.FIN_ecommerce_API.dto.request.CategoryRequestDto;
//import com.example.FIN_ecommerce_API.dto.response.CategoryResponseDto;
//
//import java.util.List;
//
//public interface CategoryService {
//    List<CategoryResponseDto> getAllCategories();
//    CategoryResponseDto getCategoryById(Long id);
//    CategoryResponseDto createCategory(CategoryRequestDto requestDto);
//    CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto);
//    void deleteCategory(Long id);
//}
package com.example.FIN_ecommerce_API.service;

import com.example.FIN_ecommerce_API.dto.request.CategoryRequestDto;
import com.example.FIN_ecommerce_API.dto.response.CategoryDetailResponseDto;
import com.example.FIN_ecommerce_API.dto.response.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> getAllCategories();
    CategoryResponseDto getCategoryById(Long id);
    CategoryDetailResponseDto getCategoryDetail(Long id);
    List<CategoryDetailResponseDto.ProductItemDto> getProductsByCategory(Long categoryId);
    CategoryResponseDto createCategory(CategoryRequestDto requestDto);
    CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto);
    void deleteCategory(Long id);
}