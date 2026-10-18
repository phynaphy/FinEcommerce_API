package com.example.FIN_ecommerce_API.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeAnalyticsDto {
    private BigDecimal todayRevenue;
    private BigDecimal monthRevenue;
    private BigDecimal yearRevenue;
    private List<DailyChartDataDto> dailyTrend;
}