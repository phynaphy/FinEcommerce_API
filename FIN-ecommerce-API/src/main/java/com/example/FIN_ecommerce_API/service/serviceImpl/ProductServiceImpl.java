package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
import com.example.FIN_ecommerce_API.model.Category;
import com.example.FIN_ecommerce_API.model.Product;
import com.example.FIN_ecommerce_API.repository.CategoryRepository;
import com.example.FIN_ecommerce_API.repository.ProductRepository;
import com.example.FIN_ecommerce_API.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

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

        Product product = Product.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .imageUrl(requestDto.getImageUrl())
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }
}