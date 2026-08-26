package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.request.LoginRequest;
import com.example.FIN_ecommerce_API.dto.request.UpdateAdminRequest;
import com.example.FIN_ecommerce_API.dto.response.AuthResponse;
import com.example.FIN_ecommerce_API.dto.response.UserResponse;
import com.example.FIN_ecommerce_API.model.Role;
import com.example.FIN_ecommerce_API.model.User;
import com.example.FIN_ecommerce_API.repository.UserRepository;
import com.example.FIN_ecommerce_API.security.JwtProvider;
import com.example.FIN_ecommerce_API.service.AdminUserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AdminUserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Restrict login exclusively to users with the ADMIN role
        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Access denied: ADMIN role required to log in.");
        }

        String token = jwtProvider.generateToken(user.getUsername());
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllAdmins() {
        return userRepository.findByRole(Role.ADMIN)
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getAdminById(Long id) {
        User admin = userRepository.findByIdAndRole(id, Role.ADMIN)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found with id: " + id));
        return mapToUserResponse(admin);
    }

    @Override
    public UserResponse updateAdmin(Long id, UpdateAdminRequest request) {
        User admin = userRepository.findByIdAndRole(id, Role.ADMIN)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found with id: " + id));

        if (request.getFullName() != null) {
            admin.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(admin.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            admin.setEmail(request.getEmail());
        }

        User updatedAdmin = userRepository.save(admin);
        return mapToUserResponse(updatedAdmin);
    }

    @Override
    public void deleteAdmin(Long id) {
        User admin = userRepository.findByIdAndRole(id, Role.ADMIN)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found with id: " + id));
        userRepository.delete(admin);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}