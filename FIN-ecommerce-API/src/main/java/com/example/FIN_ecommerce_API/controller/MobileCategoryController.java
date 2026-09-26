//package com.example.FIN_ecommerce_API.controller;
//
//import com.example.FIN_ecommerce_API.dto.request.CategoryRequestDto;
//import com.example.FIN_ecommerce_API.dto.response.CategoryResponseDto;
//import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
//import com.example.FIN_ecommerce_API.service.CategoryService;
//import com.example.FIN_ecommerce_API.service.ProductService;
//import com.example.FIN_ecommerce_API.utilities.Constant;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping(Constant.MAIN_PATH+"/categories")
//@RequiredArgsConstructor
//public class MobileCategoryController {
//    private final CategoryService categoryService;
//
//    // 1. List all categories
//    @GetMapping
//    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
//        return ResponseEntity.ok(categoryService.getAllCategories());
//    }
//
//    // 2. Get single category by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
//        return ResponseEntity.ok(categoryService.getCategoryById(id));
//    }
//
//    // 3. Click category details -> List products in that category
//    @GetMapping("/{id}/products")
//    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@PathVariable Long id) {
//        return ResponseEntity.ok(categoryService.getProductsByCategory(id));
//    }
//
//    // 4. Create new category
//    @PostMapping
//    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
//        return new ResponseEntity<>(categoryService.createCategory(requestDto), HttpStatus.CREATED);
//    }
//
//    // 5. Update category
//    @PutMapping("/{id}")
//    public ResponseEntity<CategoryResponseDto> updateCategory(
//            @PathVariable Long id,
//            @Valid @RequestBody CategoryRequestDto requestDto) {
//        return ResponseEntity.ok(categoryService.updateCategory(id, requestDto));
//    }
//
//    // 6. Delete category
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
//        categoryService.deleteCategory(id);
//        return ResponseEntity.noContent().build();
//    }
//}

package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.request.CategoryRequestDto;
import com.example.FIN_ecommerce_API.dto.response.CategoryDetailResponseDto;
import com.example.FIN_ecommerce_API.dto.response.CategoryResponseDto;
import com.example.FIN_ecommerce_API.service.CategoryService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constant.MAIN_PATH + "/categories")
@RequiredArgsConstructor
public class MobileCategoryController {

    private final CategoryService categoryService;

    // 1. List all categories
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // 2. Get single category by ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // 3. Get category detail with embedded product list
    @GetMapping("/{id}/detail")
    public ResponseEntity<CategoryDetailResponseDto> getCategoryDetail(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryDetail(id));
    }

    // 4. Click category details -> List products in that category
    @GetMapping("/{id}/products")
    public ResponseEntity<List<CategoryDetailResponseDto.ProductItemDto>> getProductsByCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getProductsByCategory(id));
    }

    // 5. Create new category
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        return new ResponseEntity<>(categoryService.createCategory(requestDto), HttpStatus.CREATED);
    }

    // 6. Update category
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDto requestDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, requestDto));
    }

    // 7. Delete category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}