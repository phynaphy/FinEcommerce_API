//package com.example.FIN_ecommerce_API.controller;
//import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
//import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
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
//@RequestMapping(Constant.MAIN_PATH + "products")
//@RequiredArgsConstructor
//public class MobileProductController {
//    private final ProductService productService;
//
//    // 1. List all products
//    @GetMapping
//    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
//        return ResponseEntity.ok(productService.getAllProducts());
//    }
//
//    // 2. Click product of category -> List product details
//    @GetMapping("/{id}")
//    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
//        return ResponseEntity.ok(productService.getProductById(id));
//    }
//
//    // 3. Create new product
//    @PostMapping
//    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto requestDto) {
//        return new ResponseEntity<>(productService.createProduct(requestDto), HttpStatus.CREATED);
//    }
//
//    // 4. Update product
//    @PutMapping("/{id}")
//    public ResponseEntity<ProductResponseDto> updateProduct(
//            @PathVariable Long id,
//            @Valid @RequestBody ProductRequestDto requestDto) {
//        return ResponseEntity.ok(productService.updateProduct(id, requestDto));
//    }
//
//    // 5. Delete product
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
//        productService.deleteProduct(id);
//        return ResponseEntity.noContent().build();
//    }
//}

package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
import com.example.FIN_ecommerce_API.service.ProductService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(Constant.MAIN_PATH + "products")
@RequiredArgsConstructor
public class MobileProductController {

    private final ProductService productService;

    // 1. List all products
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // 2. Click product of category -> List product details
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // 3. Create new product
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestPart("product") ProductRequestDto requestDto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "galleryImages", required = false) List<MultipartFile> galleryImages) {

        return new ResponseEntity<>(
                productService.createProduct(requestDto, mainImage, galleryImages),
                HttpStatus.CREATED
        );
    }

    // 4. Update product
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestPart("product") ProductRequestDto requestDto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "galleryImages", required = false) List<MultipartFile> galleryImages) {

        return ResponseEntity.ok(
                productService.updateProduct(id, requestDto, mainImage, galleryImages)
        );
    }

    // 5. Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}