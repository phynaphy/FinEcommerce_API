package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.request.CategoryRequestDto;
import com.example.FIN_ecommerce_API.dto.response.CategoryResponseDto;
import com.example.FIN_ecommerce_API.model.Category;
import com.example.FIN_ecommerce_API.repository.CategoryRepository;
import com.example.FIN_ecommerce_API.repository.ProductRepository;
import com.example.FIN_ecommerce_API.service.AdminCategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        return mapToResponseDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        if (categoryRepository.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("Category name already exists: " + requestDto.getName());
        }

        // 1. Upload new image file if provided
        String storedImageUrl = null;
        if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().isEmpty()) {
            storedImageUrl = fileStorageService.storeFile(requestDto.getImageUrl());
        }

        Category category = Category.builder()
                .name(requestDto.getName())
                .code(requestDto.getCode())
                .imageUrl(storedImageUrl)
                .isRecommended(requestDto.isRecommended()) // ✅ Fixed: Map recommendation status on create
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        if (!category.getName().equalsIgnoreCase(requestDto.getName())
                && categoryRepository.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("Category name already exists: " + requestDto.getName());
        }

        if (requestDto.getCode() != null && !requestDto.getCode().isEmpty()) {
            category.setCode(requestDto.getCode());
        }

        // 2. Delete old image and upload new image file
        if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().isEmpty()) {
            if (category.getImageUrl() != null) {
                fileStorageService.deleteFile(category.getImageUrl());
            }
            String newImageUrl = fileStorageService.storeFile(requestDto.getImageUrl());
            category.setImageUrl(newImageUrl);
        }

        category.setName(requestDto.getName());
        category.setRecommended(requestDto.isRecommended()); // ✅ Fixed: Map recommendation status on update

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponseDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        if (category.getImageUrl() != null) {
            fileStorageService.deleteFile(category.getImageUrl());
        }
        categoryRepository.delete(category);
    }

    private CategoryResponseDto mapToResponseDto(Category category) {
        int totalProducts = productRepository.countByCategoryId(category.getId());

        return CategoryResponseDto.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .totalProducts(totalProducts)
                .isRecommended(category.isRecommended())
                .build();
    }

    //    recommendation for you

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getRecommendedCategories() {
        return categoryRepository.findByIsRecommendedTrueOrderByRecommendationOrderAsc().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategoryRecommendation(Long id, boolean isRecommended) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        category.setRecommended(isRecommended);

        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDto(savedCategory);
    }

    @Override
    @Transactional
    public void removeCategoryRecommendation(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        category.setRecommended(false);
        category.setRecommendationOrder(null);
        categoryRepository.save(category);
    }
}