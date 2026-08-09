package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.request.*;
import com.example.FIN_ecommerce_API.dto.response.ApiResponse;
import com.example.FIN_ecommerce_API.dto.response.AuthResponse;
import com.example.FIN_ecommerce_API.security.JwtProvider;
import com.example.FIN_ecommerce_API.service.AuthService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constant.MAIN_PATH + "/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    public AuthController(AuthService authService, JwtProvider jwtProvider) {
        this.authService = authService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    //  Request OTP
    @PostMapping("/change-password/request-otp")
    public ResponseEntity<ApiResponse> requestChangePasswordOtp(@RequestBody RequestOtpRequest request) {
        authService.requestChangePasswordOtp(request);
        return ResponseEntity.ok(new ApiResponse( "OTP code sent to your email."));
    }

    //  Verify OTP
    @PostMapping("/change-password/verify-otp")
    public ResponseEntity<ApiResponse> verifyChangePasswordOtp(@RequestBody VerifyOtpRequest request) {
        authService.verifyChangePasswordOtp(request);
        return ResponseEntity.ok(new ApiResponse("OTP verified successfully."));
    }

    //  Complete Change Password
    @PostMapping("/change-password/complete")
    public ResponseEntity<ApiResponse> completeChangePassword(
            @RequestParam Long id,
            @RequestBody CompleteResetPasswordRequest request) {

        authService.completeChangePassword(id, request);
        return ResponseEntity.ok(new ApiResponse("Password changed successfully."));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok( new ApiResponse("OTP code sent to email."));
    }


    @PostMapping("/verify-forgot-password")
    public ResponseEntity<AuthResponse> verifyForgotPassword(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyForgotPassword(request));
    }

    // Extract username directly from token header
    @PostMapping("/reset-verify-password")
    public ResponseEntity<String> resetPassword(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ResetForgotPasswordRequest request) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: Missing or invalid Authorization token.");
        }
        // Remove "Bearer " prefix
        String token = authHeader.substring(7);
        // Get username directly from JwtProvider
        String username = jwtProvider.getUsernameFromToken(token);
        authService.resetForgotPassword(request, username);
        return ResponseEntity.ok("Password reset successfully.");
    }

}