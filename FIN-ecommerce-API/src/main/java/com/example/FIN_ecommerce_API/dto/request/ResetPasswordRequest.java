package com.example.FIN_ecommerce_API.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String newPassword;
    private String confirmPassword;
}