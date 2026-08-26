package com.example.FIN_ecommerce_API.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardStatsResponse {
    private double totalRevenue;
    private long totalOrders;
    private long totalCustomers;
    private long pendingOrders;
}