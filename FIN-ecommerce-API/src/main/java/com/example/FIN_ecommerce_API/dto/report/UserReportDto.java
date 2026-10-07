package com.example.FIN_ecommerce_API.dto.report;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserReportDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
}
