package com.example.FIN_ecommerce_API.controller;
import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
import com.example.FIN_ecommerce_API.service.ProductService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/product/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @PostMapping("add-product")
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto createdProduct = productService.createProduct(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}