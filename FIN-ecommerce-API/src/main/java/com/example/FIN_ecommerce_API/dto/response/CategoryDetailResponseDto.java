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
public class CategoryDetailResponseDto {
    private Long id;
    private String name;
    private String imageUrl;
    private Integer totalProducts;
    private List<ProductItemDto> products;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItemDto {
        private Long id;
        private String name;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Integer discountPercentage;
        private Integer reviewCount;
        private Boolean isOfficialStore;
        private String mainImageUrl;
    }
}