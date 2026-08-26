package com.example.FIN_ecommerce_API.service;

import com.example.FIN_ecommerce_API.dto.request.*;
import com.example.FIN_ecommerce_API.dto.response.AuthResponse;

public interface AdminAuthService {
    AuthResponse login(LoginRequest request);
    void createAdminUser(RegisterRequest request); // Protected: Executed by existing Super Admin
    void forgotPassword(ForgotPasswordRequest request);
    AuthResponse verifyForgotPassword(VerifyOtpRequest request);
    void resetForgotPassword(ResetForgotPasswordRequest request, String username);
}