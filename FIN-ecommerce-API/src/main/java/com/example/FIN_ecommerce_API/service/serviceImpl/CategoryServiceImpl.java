package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.request.CategoryRequestDto;
import com.example.FIN_ecommerce_API.dto.response.CategoryResponseDto;
import com.example.FIN_ecommerce_API.model.Category;
import com.example.FIN_ecommerce_API.repository.CategoryRepository;
import com.example.FIN_ecommerce_API.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
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

        Category category = Category.builder()
                .name(requestDto.getName())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        if (!category.getName().equals(requestDto.getName()) && categoryRepository.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("Category name already exists: " + requestDto.getName());
        }

        category.setName(requestDto.getName());
        Category updatedCategory = categoryRepository.save(category);
        return mapToResponseDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryResponseDto mapToResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}