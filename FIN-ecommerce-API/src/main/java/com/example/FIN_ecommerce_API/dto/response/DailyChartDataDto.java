package com.example.FIN_ecommerce_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyChartDataDto {
    private LocalDate date;
    private String label; // e.g., "Mon", "Oct 12"
    private BigDecimal revenue;
    private long orderCount;
}