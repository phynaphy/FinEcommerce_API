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

    @Override
    public UserProfileResponse updateUserWithFile(
            String username,
            UpdateUserRequest request,
            MultipartFile file) {

        // 1. Validate request
        if (request == null) {
            throw new IllegalArgumentException("Update request cannot be empty.");
        }

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        // 2. Find existing user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found with username: " + username));

        // 3. Check duplicate username
        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {

            throw new IllegalArgumentException(
                    "Username is already taken: " + request.getUsername());
        }

        // 4. Check duplicate email
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new IllegalArgumentException(
                    "Email is already in use: " + request.getEmail());
        }

        // 5. Upload profile picture if provided
        if (file != null && !file.isEmpty()) {

            // Limit upload size to 5 MB
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "Profile picture must not exceed 5 MB.");
            }

            // Allow only common image types
            String contentType = file.getContentType();

            if (contentType == null ||
                    !(contentType.equalsIgnoreCase("image/jpeg")
                            || contentType.equalsIgnoreCase("image/png")
                            || contentType.equalsIgnoreCase("image/webp"))) {

                throw new IllegalArgumentException(
                        "Only JPEG, PNG, and WEBP images are allowed.");
            }

            try {
                // Create upload directory
                Path uploadPath = Paths.get(UPLOAD_DIR)
                        .toAbsolutePath()
                        .normalize();

                Files.createDirectories(uploadPath);

                // Generate a safe, unique filename
                String extension;

                if ("image/jpeg".equalsIgnoreCase(contentType)) {
                    extension = ".jpg";
                } else if ("image/png".equalsIgnoreCase(contentType)) {
                    extension = ".png";
                } else {
                    extension = ".webp";
                }

                String fileName = java.util.UUID.randomUUID() + extension;

                Path filePath = uploadPath.resolve(fileName).normalize();

                // Ensure destination remains inside the upload directory
                if (!filePath.startsWith(uploadPath)) {
                    throw new IllegalArgumentException("Invalid file path.");
                }

                // Save image
                Files.copy(file.getInputStream(), filePath);

                // Save relative URL in database
                String fileUrl = "/uploads/profiles/" + fileName;
                user.setProfilePictureUrl(fileUrl);

            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to store profile picture.", e);
            }
        }

        // 6. Update user information
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim());
        user.setFullName(request.getFullName());

        // IMPORTANT:
        // Only update the role if your authorization rules permit it.
        // Do not allow an ordinary user to promote themselves to ADMIN.
        user.setRole(request.getRole());

        // 7. Save updated user
        User updatedUser = userRepository.save(user);

        // 8. Return updated profile
        return mapToUserProfileResponse(updatedUser);
    }
}