package com.example.FIN_ecommerce_API.dto.report;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CategoryReportDto {
    private Long id;
    private String name;
    private long productCount;
}
