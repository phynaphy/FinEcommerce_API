package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
import com.example.FIN_ecommerce_API.model.Category;
import com.example.FIN_ecommerce_API.model.Product;
import com.example.FIN_ecommerce_API.repository.CategoryRepository;
import com.example.FIN_ecommerce_API.repository.ProductRepository;
import com.example.FIN_ecommerce_API.service.ProductAdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceAdminImpl implements ProductAdminService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public ProductServiceAdminImpl(ProductRepository productRepository,
                                   CategoryRepository categoryRepository,
                                   FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
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

        String storedImageUrl = null;
        if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().isEmpty()) {
            storedImageUrl = fileStorageService.storeFile(requestDto.getImageUrl());
        }

        Product product = Product.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .imageUrl(storedImageUrl)
                .category(category)
                .description(requestDto.getDescription())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (!existingProduct.getCategory().getId().equals(requestDto.getCategoryId())) {
            Category category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + requestDto.getCategoryId()));
            existingProduct.setCategory(category);
        }

        // Handle image replacement if a new file is uploaded
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

        if (product.getImageUrl() != null) {
            fileStorageService.deleteFile(product.getImageUrl());
        }

        productRepository.deleteById(id);
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }
}