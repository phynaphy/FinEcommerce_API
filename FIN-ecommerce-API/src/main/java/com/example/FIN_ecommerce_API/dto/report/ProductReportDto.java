package com.example.FIN_ecommerce_API.dto.report;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductReportDto {
    private Long id;
    private String name;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Double discountPercentage;
    private Double rating;
    private Integer reviewCount;
    private Boolean officialStore;
    private long variantCount;
}
