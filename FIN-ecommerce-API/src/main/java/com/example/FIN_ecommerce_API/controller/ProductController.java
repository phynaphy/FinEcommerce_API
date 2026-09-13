package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
import com.example.FIN_ecommerce_API.dto.response.GlobalApiResponse;
import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
import com.example.FIN_ecommerce_API.service.ProductService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constant.MAIN_PATH + "/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public ResponseEntity<GlobalApiResponse<List<ProductResponseDto>>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(
                GlobalApiResponse.success("Products retrieved successfully", products)
        );
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<GlobalApiResponse<ProductResponseDto>> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(
                GlobalApiResponse.success("Product retrieved successfully", product)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<GlobalApiResponse<List<ProductResponseDto>>> getProductsByCategory(
            @PathVariable Long categoryId) {
        List<ProductResponseDto> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(
                GlobalApiResponse.success("Category products retrieved successfully", products)
        );
    }

    @PostMapping
    public ResponseEntity<GlobalApiResponse<ProductResponseDto>> createProduct(
            @Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto createdProduct = productService.createProduct(requestDto);

        GlobalApiResponse<ProductResponseDto> response = GlobalApiResponse.<ProductResponseDto>builder()
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED.getReasonPhrase())
                .message("Product created successfully")
                .data(createdProduct)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                GlobalApiResponse.success("Product deleted successfully", null)
        );
    }
}