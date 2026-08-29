package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.request.*;
import com.example.FIN_ecommerce_API.dto.response.AuthResponse;
import com.example.FIN_ecommerce_API.dto.response.UserResponse;
import com.example.FIN_ecommerce_API.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.example.FIN_ecommerce_API.utilities.Constant.WEB_PATH;

@RestController
@RequestMapping(WEB_PATH + "/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // 1. Admin Login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = adminUserService.login(request);
        return ResponseEntity.ok(response);
    }

    // 2. Create User
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        UserResponse createdUser = adminUserService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // 3. Get All Users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    // 4. Update User Details (FullName, Email, Role)
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        UserResponse updatedUser = adminUserService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    // 5. Update User Role Only
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestBody UpdateUserRoleRequest request) {
        UserResponse updatedUser = adminUserService.updateUserRole(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    // --- Admin-Specific Endpoints ---

    // 6. Get All Admins
    @GetMapping("/admins")
    public ResponseEntity<List<UserResponse>> getAllAdmins() {
        return ResponseEntity.ok(adminUserService.getAllAdmins());
    }

    // 7. Get Admin By ID
    @GetMapping("/admins/{id}")
    public ResponseEntity<UserResponse> getAdminById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getAdminById(id));
    }

    // 8. Update Admin Profile
    @PutMapping("/admins/{id}")
    public ResponseEntity<UserResponse> updateAdmin(
            @PathVariable Long id,
            @RequestBody UpdateAdminRequest request) {
        UserResponse updatedAdmin = adminUserService.updateAdmin(id, request);
        return ResponseEntity.ok(updatedAdmin);
    }

    // 9. Delete Admin / User
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminUserService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}