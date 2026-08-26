package com.example.FIN_ecommerce_API.dto.request;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String adminSecretKey;
}