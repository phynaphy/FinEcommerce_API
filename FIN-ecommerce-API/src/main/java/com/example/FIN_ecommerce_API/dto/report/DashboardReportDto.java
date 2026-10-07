package com.example.FIN_ecommerce_API.dto.report;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardReportDto {
    private long totalUsers;
    private long totalAdminUsers;
    private long totalProducts;
    private long totalCategories;
    private long totalVariants;
    private long totalStock;
    private long lowStockVariants;
    private long totalCarts;
    private long totalCartItems;
    private long totalCartQuantity;
    private BigDecimal cartValue;
}
