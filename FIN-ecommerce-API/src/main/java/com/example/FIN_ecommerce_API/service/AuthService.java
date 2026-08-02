package com.example.FIN_ecommerce_API.service;


import com.example.FIN_ecommerce_API.dto.AuthResponse;
import com.example.FIN_ecommerce_API.dto.LoginRequest;
import com.example.FIN_ecommerce_API.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}