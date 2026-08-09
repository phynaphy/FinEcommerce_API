package com.example.FIN_ecommerce_API.service;


import com.example.FIN_ecommerce_API.dto.request.*;
import com.example.FIN_ecommerce_API.dto.response.AuthResponse;
import com.example.FIN_ecommerce_API.dto.response.UserProfileResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void changePassword(ResetPasswordRequest request);
    void verifyChangePasswordOtp(VerifyOtpRequest request);

    void forgotPassword(ForgotPasswordRequest request);
    AuthResponse verifyForgotPassword(VerifyOtpRequest request);
    void resetForgotPassword(ResetForgotPasswordRequest request, String username);

    void requestChangePasswordOtp(RequestOtpRequest request);
    void completeChangePassword(String email, CompleteResetPasswordRequest request);

    UserProfileResponse getUserProfile(String username);
}