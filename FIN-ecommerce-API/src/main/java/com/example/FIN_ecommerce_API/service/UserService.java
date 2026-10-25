package com.example.FIN_ecommerce_API.service;

import com.example.FIN_ecommerce_API.dto.response.UserProfileResponse;
import com.example.FIN_ecommerce_API.dto.test.UpdateUserRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserProfileResponse getUserProfile(String username);
    UserProfileResponse uploadProfilePicture(String username, MultipartFile file);
    UserProfileResponse updateUserWithFile(String username, UpdateUserRequest request, MultipartFile file);
}