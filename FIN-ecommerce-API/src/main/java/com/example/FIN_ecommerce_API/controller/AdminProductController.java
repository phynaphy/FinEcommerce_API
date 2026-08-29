//package com.example.FIN_ecommerce_API.controller;
//
//import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
//import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
//import com.example.FIN_ecommerce_API.service.ProductAdminService;
//import com.example.FIN_ecommerce_API.utilities.Constant;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping(Constant.WEB_PATH + "/admin/products")
//@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminProductController {
//
//    private final ProductAdminService productAdminService;
//
//    @GetMapping
//    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
//        return ResponseEntity.ok(productAdminService.getAllProducts());
//    }
//
//    @GetMapping("/category/{categoryId}")
//    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@PathVariable Long categoryId) {
//        return ResponseEntity.ok(productAdminService.getProductsByCategory(categoryId));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
//        return ResponseEntity.ok(productAdminService.getProductById(id));
//    }
//
//    @PostMapping
//    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto requestDto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(productAdminService.createProduct(requestDto));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ProductResponseDto> updateProduct(
//            @PathVariable Long id,
//            @Valid @RequestBody ProductRequestDto requestDto) {
//        return ResponseEntity.ok(productAdminService.updateProduct(id, requestDto));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
//        productAdminService.deleteProduct(id);
//        return ResponseEntity.noContent().build();
//    }
//}

package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
import com.example.FIN_ecommerce_API.service.ProductAdminService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constant.WEB_PATH + "/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductAdminService productAdminService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productAdminService.getAllProducts());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productAdminService.getProductsByCategory(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productAdminService.getProductById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @ModelAttribute ProductRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productAdminService.createProduct(requestDto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductRequestDto requestDto) {
        return ResponseEntity.ok(productAdminService.updateProduct(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productAdminService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}