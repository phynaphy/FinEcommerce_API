package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.request.LoginRequest;
import com.example.FIN_ecommerce_API.dto.request.UpdateAdminRequest;
import com.example.FIN_ecommerce_API.dto.response.AuthResponse;
import com.example.FIN_ecommerce_API.dto.response.UserResponse;
import com.example.FIN_ecommerce_API.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.example.FIN_ecommerce_API.utilities.Constant.WEB_PATH;

@RestController
@RequestMapping(WEB_PATH + "/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // Login endpoint (Requires ADMIN credentials via Basic Auth / Service check)
    @PostMapping("/login")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADMIN')")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(adminUserService.login(request));
    }

    // Get all registered users (ADMIN only)
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    // Get all admin users (ADMIN only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllAdmins() {
        return ResponseEntity.ok(adminUserService.getAllAdmins());
    }

    // Get single admin by ID (ADMIN only)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> getAdminById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getAdminById(id));
    }

    // Update admin profile details (ADMIN only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAdminRequest request) {
        return ResponseEntity.ok(adminUserService.updateAdmin(id, request));
    }

    // Delete admin profile (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteAdmin(@PathVariable Long id) {
        adminUserService.deleteAdmin(id);
        return ResponseEntity.ok(Map.of("message", "Admin account deleted successfully"));
    }
}