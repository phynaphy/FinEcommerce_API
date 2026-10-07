package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.report.*;
import com.example.FIN_ecommerce_API.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminReportController {
    private final AdminReportService reportService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardReportDto> dashboard(){ return ResponseEntity.ok(reportService.getDashboard()); }
    @GetMapping("/users")
    public ResponseEntity<List<UserReportDto>> users(){ return ResponseEntity.ok(reportService.getUsers()); }
    @GetMapping("/products")
    public ResponseEntity<List<ProductReportDto>> products(){ return ResponseEntity.ok(reportService.getProducts()); }
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryReportDto>> categories(){ return ResponseEntity.ok(reportService.getCategories()); }
    @GetMapping("/variants")
    public ResponseEntity<List<VariantReportDto>> variants(){ return ResponseEntity.ok(reportService.getVariants()); }
    @GetMapping("/cart-items")
    public ResponseEntity<List<CartItemReportDto>> cartItems(){ return ResponseEntity.ok(reportService.getCartItems()); }

    @GetMapping("/export/excel")
    public ResponseEntity<ByteArrayResource> exportExcel(){
        byte[] file=reportService.exportExcel();
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("FIN-Ecommerce-Admin-Report.xlsx").build());
        headers.setContentLength(file.length);
        return ResponseEntity.ok().headers(headers).body(new ByteArrayResource(file));
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportAllPdf() {
        byte[] pdf = reportService.exportPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=full-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/export/pdf/dashboard")
    public ResponseEntity<byte[]> exportDashboardPdf() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dashboard-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportService.exportDashboardPdf());
    }

    @GetMapping("/export/pdf/products")
    public ResponseEntity<byte[]> exportProductsPdf() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportService.exportProductsPdf());
    }
}
