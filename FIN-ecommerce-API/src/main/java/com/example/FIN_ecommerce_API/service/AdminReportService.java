package com.example.FIN_ecommerce_API.service;

import com.example.FIN_ecommerce_API.dto.report.*;
import java.util.List;

public interface AdminReportService {
    DashboardReportDto getDashboard();
    List<UserReportDto> getUsers();
    List<ProductReportDto> getProducts();
    List<CategoryReportDto> getCategories();
    List<VariantReportDto> getVariants();
    List<CartItemReportDto> getCartItems();
    byte[] exportExcel();
    
    // PDF Export methods
    byte[] exportPdf(); // All reports in one PDF document
    byte[] exportDashboardPdf();
    byte[] exportUsersPdf();
    byte[] exportProductsPdf();
    byte[] exportCategoriesPdf();
    byte[] exportVariantsPdf();
    byte[] exportCartItemsPdf();
}
