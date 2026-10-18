package com.example.FIN_ecommerce_API.service;

import com.example.FIN_ecommerce_API.dto.report.DashboardReportDto;
import com.example.FIN_ecommerce_API.dto.response.DashboardDTO;
import com.example.FIN_ecommerce_API.dto.response.TimeAnalyticsDto;

public interface DashboardService {
    //DashboardDTO getDashboardMetrics();
    DashboardReportDto getDashboardMetrics();
    TimeAnalyticsDto getTimeAnalyticsMetrics();
}
