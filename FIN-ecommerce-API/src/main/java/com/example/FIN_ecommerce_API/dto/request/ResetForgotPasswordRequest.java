package com.example.FIN_ecommerce_API.dto.request;

import lombok.Data;

@Data
public class ResetForgotPasswordRequest {
    private String newPassword;
    private String confirmPassword;
}