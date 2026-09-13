package com.example.FIN_ecommerce_API.service;


import com.example.FIN_ecommerce_API.dto.request.CategoryRequestDto;
import com.example.FIN_ecommerce_API.dto.response.CategoryResponseDto;

import java.util.List;

public interface AdminCategoryService {
    List<CategoryResponseDto> getAllCategories();
    CategoryResponseDto getCategoryById(Long id);
    CategoryResponseDto createCategory(CategoryRequestDto requestDto);
    CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto);
    void deleteCategory(Long id);
}