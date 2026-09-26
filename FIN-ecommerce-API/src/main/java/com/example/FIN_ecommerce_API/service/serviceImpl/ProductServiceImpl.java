//package com.example.FIN_ecommerce_API.service.serviceImpl;
//
//import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
//import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
//import com.example.FIN_ecommerce_API.model.Category;
//import com.example.FIN_ecommerce_API.model.Product;
//import com.example.FIN_ecommerce_API.repository.CategoryRepository;
//import com.example.FIN_ecommerce_API.repository.ProductRepository;
//import com.example.FIN_ecommerce_API.service.ProductService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ProductServiceImpl implements ProductService {
//
//    private final ProductRepository productRepository;
//    private final CategoryRepository categoryRepository;
//    private final FileStorageService fileStorageService;
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<ProductResponseDto> getAllProducts() {
//        return productRepository.findAllWithCategory()
//                .stream()
//                .map(this::mapToResponseDto)
//                .toList();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
//        if (!categoryRepository.existsById(categoryId)) {
//            throw new RuntimeException("Category not found with id: " + categoryId);
//        }
//        return productRepository.findByCategoryId(categoryId)
//                .stream()
//                .map(this::mapToResponseDto)
//                .toList();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public ProductResponseDto getProductById(Long id) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
//        return mapToResponseDto(product);
//    }
//
//    @Override
//    @Transactional
//    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
//        Category category = categoryRepository.findById(requestDto.getCategoryId())
//                .orElseThrow(() -> new RuntimeException("Category not found with id: " + requestDto.getCategoryId()));
//
//        // Upload and get stored relative path URL
//        String storedImageUrl = null;
//        if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().isEmpty()) {
//            storedImageUrl = fileStorageService.storeFile(requestDto.getImageUrl());
//        }
//
//        Product product = Product.builder()
//                .name(requestDto.getName())
//                .price(requestDto.getPrice())
//                .imageUrl(storedImageUrl)
//                .category(category)
//                .build();
//
//        Product savedProduct = productRepository.save(product);
//        return mapToResponseDto(savedProduct);
//    }
//
//    @Transactional
//    @Override
//    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
//        Product existingProduct = productRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
//
//        // Update category if changed
//        if (!existingProduct.getCategory().getId().equals(requestDto.getCategoryId())) {
//            Category category = categoryRepository.findById(requestDto.getCategoryId())
//                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + requestDto.getCategoryId()));
//            existingProduct.setCategory(category);
//        }
//
//        // Replace old image with new image if uploaded
//        if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().isEmpty()) {
//            if (existingProduct.getImageUrl() != null) {
//                fileStorageService.deleteFile(existingProduct.getImageUrl());
//            }
//            String newImageUrl = fileStorageService.storeFile(requestDto.getImageUrl());
//            existingProduct.setImageUrl(newImageUrl);
//        }
//
//        existingProduct.setName(requestDto.getName());
//        existingProduct.setPrice(requestDto.getPrice());
//
//        Product updatedProduct = productRepository.save(existingProduct);
//        return mapToResponseDto(updatedProduct);
//    }
//
//    @Override
//    @Transactional
//    public void deleteProduct(Long id) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
//
//        // Delete physical image file from storage
//        if (product.getImageUrl() != null) {
//            fileStorageService.deleteFile(product.getImageUrl());
//        }
//
//        productRepository.deleteById(id);
//    }
//
//    private ProductResponseDto mapToResponseDto(Product product) {
//        Long categoryId = null;
//        String categoryName = null;
//
//        if (product.getCategory() != null) {
//            categoryId = product.getCategory().getId();
//            categoryName = product.getCategory().getName();
//        }
//
//        return ProductResponseDto.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .price(product.getPrice())
//                .imageUrl(product.getImageUrl())
//                .description(product.getDescription())
//                .categoryId(categoryId)
//                .categoryName(categoryName)
//                .build();
//    }
//}
//
//package com.example.FIN_ecommerce_API.service.serviceImpl;
//
//import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
//import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
//import com.example.FIN_ecommerce_API.model.Category;
//import com.example.FIN_ecommerce_API.model.Product;
//import com.example.FIN_ecommerce_API.repository.CategoryRepository;
//import com.example.FIN_ecommerce_API.repository.ProductRepository;
//import com.example.FIN_ecommerce_API.service.ProductService;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ProductServiceImpl implements ProductService {
//
//    private final ProductRepository productRepository;
//    private final CategoryRepository categoryRepository;
//    private final FileStorageService fileStorageService;
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<ProductResponseDto> getAllProducts() {
//        return productRepository.findAllWithCategory()
//                .stream()
//                .map(this::mapToResponseDto)
//                .toList();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
//        if (!categoryRepository.existsById(categoryId)) {
//            throw new EntityNotFoundException("Category not found with id: " + categoryId);
//        }
//        return productRepository.findByCategoryId(categoryId)
//                .stream()
//                .map(this::mapToResponseDto)
//                .toList();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public ProductResponseDto getProductById(Long id) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
//        return mapToResponseDto(product);
//    }
//
//    @Override
//    @Transactional
//    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
//        Category category = categoryRepository.findById(requestDto.getCategoryId())
//                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + requestDto.getCategoryId()));
//
////        String storedImageUrl = null;
////        if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().isEmpty()) {
////            storedImageUrl = fileStorageService.storeFile(requestDto.getImageUrl());
////        }
//
//        Product product = Product.builder()
//                .name(requestDto.getName())
//                .price(requestDto.getPrice())
//                .description(requestDto.getDescription()) // Fixed: missing description field
////                .imageUrl(storedImageUrl)
//                .category(category)
//                .build();
//
//        Product savedProduct = productRepository.save(product);
//        return mapToResponseDto(savedProduct);
//    }
//
//    @Override
//    @Transactional
//    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
//        Product existingProduct = productRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
//
//        // Update category only if changed
//        if (requestDto.getCategoryId() != null &&
//                !existingProduct.getCategory().getId().equals(requestDto.getCategoryId())) {
//            Category category = categoryRepository.findById(requestDto.getCategoryId())
//                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + requestDto.getCategoryId()));
//            existingProduct.setCategory(category);
//        }
//
//        // Replace image file if a new path/file string is provided
////        if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().isEmpty()) {
////            if (existingProduct.getImageUrl() != null) {
////                fileStorageService.deleteFile(existingProduct.getImageUrl());
////            }
////            String newImageUrl = fileStorageService.storeFile(requestDto.getImageUrl());
////            existingProduct.setImageUrl(newImageUrl);
////        }
//
//        existingProduct.setName(requestDto.getName());
//        existingProduct.setPrice(requestDto.getPrice());
//        existingProduct.setDescription(requestDto.getDescription()); // Fixed: sync description on update
//
//        Product updatedProduct = productRepository.save(existingProduct);
//        return mapToResponseDto(updatedProduct);
//    }
//
//    @Override
//    @Transactional
//    public void deleteProduct(Long id) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
//
////        if (product.getImageUrl() != null) {
////            fileStorageService.deleteFile(product.getImageUrl());
////        }
//
//        productRepository.delete(product);
//    }
//
//    private ProductResponseDto mapToResponseDto(Product product) {
//        Long categoryId = null;
//        String categoryName = null;
//
//        if (product.getCategory() != null) {
//            categoryId = product.getCategory().getId();
//            categoryName = product.getCategory().getName();
//        }
//
//        return ProductResponseDto.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .price(product.getPrice())
////                .imageUrl(product.getImageUrl())
//                .description(product.getDescription())
//                .categoryId(categoryId)
//                .categoryName(categoryName)
//                .build();
//    }
//}

package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.request.ProductRequestDto;
import com.example.FIN_ecommerce_API.dto.response.ProductResponseDto;
import com.example.FIN_ecommerce_API.model.Category;
import com.example.FIN_ecommerce_API.model.Product;
import com.example.FIN_ecommerce_API.model.ProductFeature;
import com.example.FIN_ecommerce_API.model.ProductVariant;
import com.example.FIN_ecommerce_API.repository.CategoryRepository;
import com.example.FIN_ecommerce_API.repository.ProductRepository;
import com.example.FIN_ecommerce_API.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
            throw new EntityNotFoundException("Category not found with id: " + categoryId);
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
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return mapToResponseDto(product);
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto, MultipartFile mainImage, List<MultipartFile> galleryImages) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + requestDto.getCategoryId()));

        // Process Main Image File
        String mainImageUrl = null;
        if (mainImage != null && !mainImage.isEmpty()) {
            mainImageUrl = fileStorageService.storeFile(mainImage);
        }

        // Process Gallery Image Files
        List<String> imageUrls = new ArrayList<>();
        if (galleryImages != null && !galleryImages.isEmpty()) {
            imageUrls = galleryImages.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(fileStorageService::storeFile)
                    .toList();
        }

        Product product = Product.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .originalPrice(requestDto.getOriginalPrice())
                .discountPercentage(requestDto.getDiscountPercentage())
                .rating(requestDto.getRating() != null ? requestDto.getRating() : 5.0)
                .reviewCount(requestDto.getReviewCount() != null ? requestDto.getReviewCount() : 0)
                .isOfficialStore(requestDto.getIsOfficialStore() != null && requestDto.getIsOfficialStore())
                .mainImageUrl(mainImageUrl)
                .imageUrls(imageUrls)
                .description(requestDto.getDescription())
                .category(category)
                .variants(new ArrayList<>())
                .features(new ArrayList<>())
                .build();

        // Process Product Variants
        if (requestDto.getVariants() != null) {
            List<ProductVariant> variants = requestDto.getVariants().stream()
                    .map(v -> ProductVariant.builder()
                            .color(v.getColor())
                            .colorHex(v.getColorHex())
                            .storage(v.getStorage())
                            .priceAdjustment(v.getPriceAdjustment())
                            .stockQuantity(v.getStockQuantity())
                            .product(product)
                            .build())
                    .toList();
            product.getVariants().addAll(variants);
        }

        // Process Product Features
        if (requestDto.getFeatures() != null) {
            List<ProductFeature> features = requestDto.getFeatures().stream()
                    .map(f -> ProductFeature.builder()
                            .featureKey(f.getFeatureKey())
                            .featureValue(f.getFeatureValue())
                            .product(product)
                            .build())
                    .toList();
            product.getFeatures().addAll(features);
        }

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto, MultipartFile mainImage, List<MultipartFile> galleryImages) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        // Update category if changed
        if (requestDto.getCategoryId() != null &&
                !existingProduct.getCategory().getId().equals(requestDto.getCategoryId())) {
            Category category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + requestDto.getCategoryId()));
            existingProduct.setCategory(category);
        }

        // Replace Main Image
        if (mainImage != null && !mainImage.isEmpty()) {
            if (existingProduct.getMainImageUrl() != null) {
                fileStorageService.deleteFile(existingProduct.getMainImageUrl());
            }
            existingProduct.setMainImageUrl(fileStorageService.storeFile(mainImage));
        }

        // Replace Gallery Images
        if (galleryImages != null && !galleryImages.isEmpty()) {
            if (existingProduct.getImageUrls() != null) {
                existingProduct.getImageUrls().forEach(fileStorageService::deleteFile);
            }
            List<String> newImageUrls = galleryImages.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(fileStorageService::storeFile)
                    .toList();
            existingProduct.setImageUrls(newImageUrls);
        }

        existingProduct.setName(requestDto.getName());
        existingProduct.setPrice(requestDto.getPrice());
        existingProduct.setOriginalPrice(requestDto.getOriginalPrice());
        existingProduct.setDiscountPercentage(requestDto.getDiscountPercentage());
        existingProduct.setRating(requestDto.getRating());
        existingProduct.setReviewCount(requestDto.getReviewCount());
        existingProduct.setIsOfficialStore(requestDto.getIsOfficialStore());
        existingProduct.setDescription(requestDto.getDescription());

        // Sync Variants
        existingProduct.getVariants().clear();
        if (requestDto.getVariants() != null) {
            List<ProductVariant> updatedVariants = requestDto.getVariants().stream()
                    .map(v -> ProductVariant.builder()
                            .color(v.getColor())
                            .colorHex(v.getColorHex())
                            .storage(v.getStorage())
                            .priceAdjustment(v.getPriceAdjustment())
                            .stockQuantity(v.getStockQuantity())
                            .product(existingProduct)
                            .build())
                    .toList();
            existingProduct.getVariants().addAll(updatedVariants);
        }

        // Sync Features
        existingProduct.getFeatures().clear();
        if (requestDto.getFeatures() != null) {
            List<ProductFeature> updatedFeatures = requestDto.getFeatures().stream()
                    .map(f -> ProductFeature.builder()
                            .featureKey(f.getFeatureKey())
                            .featureValue(f.getFeatureValue())
                            .product(existingProduct)
                            .build())
                    .toList();
            existingProduct.getFeatures().addAll(updatedFeatures);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToResponseDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        if (product.getMainImageUrl() != null) {
            fileStorageService.deleteFile(product.getMainImageUrl());
        }
        if (product.getImageUrls() != null) {
            product.getImageUrls().forEach(fileStorageService::deleteFile);
        }

        productRepository.delete(product);
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        Long categoryId = null;
        String categoryName = null;

        if (product.getCategory() != null) {
            categoryId = product.getCategory().getId();
            categoryName = product.getCategory().getName();
        }

        List<ProductResponseDto.ProductVariantResponseDto> variantDtos = product.getVariants() == null ? List.of() :
                product.getVariants().stream()
                        .map(v -> ProductResponseDto.ProductVariantResponseDto.builder()
                                .id(v.getId())
                                .color(v.getColor())
                                .colorHex(v.getColorHex())
                                .storage(v.getStorage())
                                .priceAdjustment(v.getPriceAdjustment())
                                .stockQuantity(v.getStockQuantity())
                                .build())
                        .toList();

        List<ProductResponseDto.ProductFeatureResponseDto> featureDtos = product.getFeatures() == null ? List.of() :
                product.getFeatures().stream()
                        .map(f -> ProductResponseDto.ProductFeatureResponseDto.builder()
                                .id(f.getId())
                                .featureKey(f.getFeatureKey())
                                .featureValue(f.getFeatureValue())
                                .build())
                        .toList();

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .discountPercentage(product.getDiscountPercentage())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .isOfficialStore(product.getIsOfficialStore())
                .mainImageUrl(product.getMainImageUrl())
                .imageUrls(product.getImageUrls())
                .description(product.getDescription())
                .categoryId(categoryId)
                .categoryName(categoryName)
                .variants(variantDtos)
                .features(featureDtos)
                .build();
    }
}