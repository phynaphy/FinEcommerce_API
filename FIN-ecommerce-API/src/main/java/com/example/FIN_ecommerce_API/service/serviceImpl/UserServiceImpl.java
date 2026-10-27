package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.response.UserProfileResponse;
import com.example.FIN_ecommerce_API.dto.test.UpdateUserRequest;
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
import java.util.UUID;

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
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/" + UPLOAD_DIR + fileName;
            user.setProfilePictureUrl(fileUrl);
            userRepository.save(user);

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
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }

    @Override
    public UserProfileResponse updateUserWithFile(
            String username,
            UpdateUserRequest request,
            MultipartFile file) {

        if (request == null) {
            throw new IllegalArgumentException("Update request cannot be empty.");
        }

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found with username: " + username));

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Username is already taken: " + request.getUsername());
        }

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email is already in use: " + request.getEmail());
        }

        if (file != null && !file.isEmpty()) {
            // Fixed file size check (5 MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "Profile picture must not exceed 5 MB.");
            }

            String contentType = file.getContentType();

            if (contentType == null ||
                    !(contentType.equalsIgnoreCase("image/jpeg")
                            || contentType.equalsIgnoreCase("image/png")
                            || contentType.equalsIgnoreCase("image/webp"))) {
                throw new IllegalArgumentException(
                        "Only JPEG, PNG, and WEBP images are allowed.");
            }

            try {
                Path uploadPath = Paths.get(UPLOAD_DIR)
                        .toAbsolutePath()
                        .normalize();

                Files.createDirectories(uploadPath);

                String extension;
                if ("image/jpeg".equalsIgnoreCase(contentType)) {
                    extension = ".jpg";
                } else if ("image/png".equalsIgnoreCase(contentType)) {
                    extension = ".png";
                } else {
                    extension = ".webp";
                }

                String fileName = UUID.randomUUID() + extension;
                Path filePath = uploadPath.resolve(fileName).normalize();

                if (!filePath.startsWith(uploadPath)) {
                    throw new IllegalArgumentException("Invalid file path.");
                }

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String fileUrl = "/" + UPLOAD_DIR + fileName;
                user.setProfilePictureUrl(fileUrl);

            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to store profile picture.", e);
            }
        }

        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());

        User updatedUser = userRepository.save(user);
        return mapToUserProfileResponse(updatedUser);
    }
}