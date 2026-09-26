//package com.example.FIN_ecommerce_API.dto.request;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Positive;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.math.BigDecimal;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class ProductRequestDto {
//
//    @NotBlank(message = "Product name is required")
//    private String name;
//
//    @NotNull(message = "Price is required")
//    @Positive(message = "Price must be greater than zero")
//    private BigDecimal price;
//
//    private MultipartFile imageUrl;
//
//    private String description;
//
//    @NotNull(message = "Category ID is required")
//    private Long categoryId;
//}
package com.example.FIN_ecommerce_API.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    @Positive(message = "Original price must be greater than zero")
    private BigDecimal originalPrice;

    @Min(value = 0, message = "Discount percentage cannot be negative")
    @Max(value = 100, message = "Discount percentage cannot exceed 100")
    private Integer discountPercentage;

    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @Max(value = 5, message = "Rating cannot exceed 5.0")
    private Double rating;

    @PositiveOrZero(message = "Review count cannot be negative")
    private Integer reviewCount;

    private Boolean isOfficialStore;

    private MultipartFile mainImage;

    private List<MultipartFile> images;

    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Valid
    private List<ProductVariantRequestDto> variants;

    @Valid
    private List<ProductFeatureRequestDto> features;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductVariantRequestDto {

        @NotBlank(message = "Color is required")
        private String color;

        private String colorHex;

        @NotBlank(message = "Storage capability is required")
        private String storage;

        @PositiveOrZero(message = "Price adjustment must be zero or positive")
        private BigDecimal priceAdjustment;

        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero(message = "Stock quantity cannot be negative")
        private Integer stockQuantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductFeatureRequestDto {

        @NotBlank(message = "Feature key is required")
        private String featureKey;

        @NotBlank(message = "Feature value is required")
        private String featureValue;
    }
}