package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
import com.example.FIN_ecommerce_API.model.Category;
import com.example.FIN_ecommerce_API.model.Product;
import com.example.FIN_ecommerce_API.repository.CategoryRepository;
import com.example.FIN_ecommerce_API.repository.ProductRepository;
import com.example.FIN_ecommerce_API.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAllWithCategory()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return mapToResponseDto(product);
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + requestDto.getCategoryId()));

        // Upload and get stored relative path URL
        String storedImageUrl = null;
        if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().isEmpty()) {
            storedImageUrl = fileStorageService.storeFile(requestDto.getImageUrl());
        }

        Product product = Product.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .imageUrl(storedImageUrl)
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    @Transactional
    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update category if changed
        if (!existingProduct.getCategory().getId().equals(requestDto.getCategoryId())) {
            Category category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + requestDto.getCategoryId()));
            existingProduct.setCategory(category);
        }

        // Replace old image with new image if uploaded
        if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().isEmpty()) {
            if (existingProduct.getImageUrl() != null) {
                fileStorageService.deleteFile(existingProduct.getImageUrl());
            }
            String newImageUrl = fileStorageService.storeFile(requestDto.getImageUrl());
            existingProduct.setImageUrl(newImageUrl);
        }

        existingProduct.setName(requestDto.getName());
        existingProduct.setPrice(requestDto.getPrice());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToResponseDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Delete physical image file from storage
        if (product.getImageUrl() != null) {
            fileStorageService.deleteFile(product.getImageUrl());
        }

        productRepository.deleteById(id);
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        Long categoryId = null;
        String categoryName = null;

        if (product.getCategory() != null) {
            categoryId = product.getCategory().getId();
            categoryName = product.getCategory().getName();
        }

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .categoryId(categoryId)
                .categoryName(categoryName)
                .build();
    }
}