package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.request.CategoryRequestDto;
import com.example.FIN_ecommerce_API.model.Category;
import com.example.FIN_ecommerce_API.repository.CategoryRepository;
import com.example.FIN_ecommerce_API.utilities.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constant.MAIN_PATH +"/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        Category category = Category.builder()
                .name(requestDto.getName())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryRepository.save(category));
    }
}