package com.example.FIN_ecommerce_API.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteResetPasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}