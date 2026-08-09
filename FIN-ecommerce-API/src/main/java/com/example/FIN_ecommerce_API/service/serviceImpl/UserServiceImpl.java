package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.response.UserProfileResponse;
import com.example.FIN_ecommerce_API.model.User;
import com.example.FIN_ecommerce_API.repository.UserRepository;
import com.example.FIN_ecommerce_API.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final String UPLOAD_DIR = "uploads/profiles/";

    @Override
    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return mapToUserProfileResponse(user);
    }

    @Override
    public UserProfileResponse uploadProfilePicture(String username, MultipartFile file) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please select a file to upload.");
        }

        try {
            // 1. Ensure target directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Generate a unique filename
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // 3. Save file to disk
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Update the user entity and save to Database
            String fileUrl = "/" + UPLOAD_DIR + fileName;
            user.setProfilePictureUrl(fileUrl);
            userRepository.save(user); // Persists to SQL DB

            // 5. Return response mapped from database entity
            return mapToUserProfileResponse(user);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }
}