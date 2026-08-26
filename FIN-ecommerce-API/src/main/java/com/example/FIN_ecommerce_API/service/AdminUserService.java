package com.example.FIN_ecommerce_API.service;


import com.example.FIN_ecommerce_API.dto.request.LoginRequest;
import com.example.FIN_ecommerce_API.dto.request.UpdateAdminRequest;
import com.example.FIN_ecommerce_API.dto.response.AuthResponse;
import com.example.FIN_ecommerce_API.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminUserService {
    List<UserResponse> getAllAdmins();
    UserResponse getAdminById(Long id);
    UserResponse updateAdmin(Long id, UpdateAdminRequest request);
    void deleteAdmin(Long id);
    List<UserResponse> getAllUsers();
    AuthResponse login(LoginRequest request);

}