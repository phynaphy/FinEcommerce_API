//package com.example.FIN_ecommerce_API.dto.response;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class ProductResponseDto {
//    private Long id;
//    private String name;
//    private BigDecimal price;
//    private String imageUrl;
//    private String categoryName;
//    private String description;
//    private Long categoryId;
//    private Integer stock;
//}
package com.example.FIN_ecommerce_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Double discountPercentage;
    private Double rating;
    private Integer reviewCount;
    private Boolean isOfficialStore;
    private String mainImageUrl;
    private List<String> imageUrls;
    private String description;
    private Long categoryId;
    private String categoryName;

    private List<ProductVariantResponseDto> variants;
    private List<ProductFeatureResponseDto> features;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductVariantResponseDto {
        private Long id;
        private String color;
        private String colorHex;
        private String storage;
        private BigDecimal priceAdjustment;
        private Integer stockQuantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductFeatureResponseDto {
        private Long id;
        private String featureKey;
        private String featureValue;
    }
}