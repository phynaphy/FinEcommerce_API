package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.report.DashboardReportDto;
import com.example.FIN_ecommerce_API.dto.response.DailyChartDataDto;
import com.example.FIN_ecommerce_API.dto.response.TimeAnalyticsDto;
import com.example.FIN_ecommerce_API.model.Role;
import com.example.FIN_ecommerce_API.repository.*;
import com.example.FIN_ecommerce_API.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final int LOW_STOCK_LIMIT = 5;

    private final AdminReportUserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AdminReportVariantRepository variantRepository;
    private final AdminReportCartRepository cartRepository;

    @Override
    public DashboardReportDto getDashboardMetrics() {
        Long stock = variantRepository.sumStockQuantity();
        Long quantity = cartRepository.sumCartQuantity();
        BigDecimal value = cartRepository.sumCartValue();

        return DashboardReportDto.builder()
                .totalUsers(userRepository.count())
                .totalAdminUsers(userRepository.countByRole(Role.ADMIN))
                .totalProducts(productRepository.count())
                .totalCategories(categoryRepository.count())
                .totalVariants(variantRepository.count())
                .totalStock(stock == null ? 0L : stock)
                .lowStockVariants(variantRepository.countByStockQuantityLessThanEqual(LOW_STOCK_LIMIT))
                .totalCarts(cartRepository.count())
                .totalCartItems(cartRepository.countCartItems())
                .totalCartQuantity(quantity == null ? 0L : quantity)
                .cartValue(value == null ? BigDecimal.ZERO : value)
                .build();
    }

    @Override
    public TimeAnalyticsDto getTimeAnalyticsMetrics() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime startOfYear = now.withDayOfYear(1).toLocalDate().atStartOfDay();

        BigDecimal todayVal = cartRepository.sumCartValueAfter(startOfToday);
        BigDecimal monthVal = cartRepository.sumCartValueAfter(startOfMonth);
        BigDecimal yearVal = cartRepository.sumCartValueAfter(startOfYear);

        // Daily trend data for the last 7 days
        List<DailyChartDataDto> dailyTrend = new ArrayList<>();
        LocalDate today = now.toLocalDate();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            BigDecimal dayRevenue = cartRepository.sumCartValueBetween(dayStart, dayEnd);
            long dayCarts = cartRepository.countCartsBetween(dayStart, dayEnd);

            String label = (i == 0) ? "Today" : date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            dailyTrend.add(DailyChartDataDto.builder()
                    .date(date)
                    .label(label)
                    .revenue(dayRevenue == null ? BigDecimal.ZERO : dayRevenue)
                    .orderCount(dayCarts)
                    .build());
        }

        return TimeAnalyticsDto.builder()
                .todayRevenue(todayVal == null ? BigDecimal.ZERO : todayVal)
                .monthRevenue(monthVal == null ? BigDecimal.ZERO : monthVal)
                .yearRevenue(yearVal == null ? BigDecimal.ZERO : yearVal)
                .dailyTrend(dailyTrend)
                .build();
    }
}