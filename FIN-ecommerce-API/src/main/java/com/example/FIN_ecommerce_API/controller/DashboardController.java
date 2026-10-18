package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.report.DashboardReportDto;
import com.example.FIN_ecommerce_API.dto.response.DashboardDTO;
import com.example.FIN_ecommerce_API.dto.response.TimeAnalyticsDto;
import com.example.FIN_ecommerce_API.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;
    @GetMapping("/summary")
    public ResponseEntity<DashboardReportDto> getDashboardMetrics() {
        return ResponseEntity.ok(dashboardService.getDashboardMetrics());
    }

    // Get Today, Month, Year, and Daily Trend Analytics
    @GetMapping("/analytics/time-series")
    public ResponseEntity<TimeAnalyticsDto> getTimeAnalytics() {
        return ResponseEntity.ok(dashboardService.getTimeAnalyticsMetrics());
    }
}