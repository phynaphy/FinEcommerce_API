package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.request.*;
import com.example.FIN_ecommerce_API.dto.response.AuthResponse;
import com.example.FIN_ecommerce_API.service.AdminAuthService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(Constant.WEB_PATH + "/admin/auth")
public class AdminAuthController {
    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = adminAuthService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> createAdminUser(@Valid @RequestBody RegisterRequest request) {
        adminAuthService.createAdminUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Admin user created successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        adminAuthService.forgotPassword(request);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully to email"));
    }

    @PostMapping("/verify-forgot-password")
    public ResponseEntity<AuthResponse> verifyForgotPassword(@Valid @RequestBody VerifyOtpRequest request) {
        AuthResponse response = adminAuthService.verifyForgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-forgot-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> resetForgotPassword(
            @Valid @RequestBody ResetForgotPasswordRequest request,
            Authentication authentication) {

        String currentAdminUsername = authentication.getName();
        adminAuthService.resetForgotPassword(request, currentAdminUsername);

        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }
}