package com.example.FIN_ecommerce_API.dto.report;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class VariantReportDto {
    private Long id;
    private Long productId;
    private String productName;
    private String color;
    private String colorHex;
    private String storage;
    private BigDecimal priceAdjustment;
    private Integer stockQuantity;
    private String stockStatus;
}
