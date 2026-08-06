package com.example.FIN_ecommerce_API.service;


import com.example.FIN_ecommerce_API.dto.request.*;
import com.example.FIN_ecommerce_API.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void verifyOtp(VerifyOtpRequest request);
}