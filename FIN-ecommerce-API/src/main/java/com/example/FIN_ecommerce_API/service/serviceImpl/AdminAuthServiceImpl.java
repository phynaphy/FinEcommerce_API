package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.request.*;
import com.example.FIN_ecommerce_API.dto.response.AuthResponse;
import com.example.FIN_ecommerce_API.model.Role;
import com.example.FIN_ecommerce_API.model.User;
import com.example.FIN_ecommerce_API.repository.UserRepository;
import com.example.FIN_ecommerce_API.security.JwtProvider;
import com.example.FIN_ecommerce_API.service.AdminAuthService;
import com.example.FIN_ecommerce_API.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
public class AdminAuthServiceImpl implements AdminAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;

    public AdminAuthServiceImpl(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                JwtProvider jwtProvider,
                                EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.emailService = emailService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Reject non-admin attempts strictly
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Insufficient privileges");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtProvider.generateToken(user.getUsername());
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public void createAdminUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Error: Passwords do not match!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User admin = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .confirmPassword(encodedPassword) // Add this line
                .fullName(request.getFullName())
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Not an admin account");
        }

        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);
        user.setResetOtp(otp);
        user.setOtpExpiration(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    public AuthResponse verifyForgotPassword(VerifyOtpRequest request) {
        User user = userRepository.findByResetOtp(request.getOtp())
                .orElseThrow(() -> new RuntimeException("Invalid OTP code"));

        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }

        if (user.getOtpExpiration() == null || user.getOtpExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP code has expired");
        }

        String token = jwtProvider.generateToken(user.getUsername());
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public void resetForgotPassword(ResetForgotPasswordRequest request, String username) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetOtp(null);
        user.setOtpExpiration(null);
        user.setPendingPassword(null);
        userRepository.save(user);
    }

    @Override
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header format");
        }

        String token = authHeader.substring(7);

        if (!jwtProvider.validateToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        // Invalidate the token server-side
        jwtProvider.invalidateToken(token);
    }
}