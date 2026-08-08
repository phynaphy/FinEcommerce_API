package com.example.FIN_ecommerce_API.service.serviceImpl;
import com.example.FIN_ecommerce_API.dto.request.*;
import com.example.FIN_ecommerce_API.dto.response.AuthResponse;
import com.example.FIN_ecommerce_API.model.Role;
import com.example.FIN_ecommerce_API.model.User;
import com.example.FIN_ecommerce_API.repository.UserRepository;
import com.example.FIN_ecommerce_API.security.JwtProvider;
import com.example.FIN_ecommerce_API.service.AuthService;
import com.example.FIN_ecommerce_API.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.emailService = emailService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Error: Passwords do not match!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .confirmPassword(passwordEncoder.encode(request.getConfirmPassword()))
                .fullName(request.getFullName())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        String token = jwtProvider.generateToken(user.getUsername());
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String token = jwtProvider.generateToken(user.getUsername());
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);
        user.setResetOtp(otp);
        user.setOtpExpiration(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email"));
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);
        // Store OTP expiration and encoded pending password
        user.setResetOtp(otp);
        user.setOtpExpiration(LocalDateTime.now().plusMinutes(10));
        user.setPendingPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        // Send OTP to email
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    public void verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByResetOtp(request.getOtp())
                .orElseThrow(() -> new RuntimeException("Invalid OTP code"));
        if (user.getOtpExpiration() == null || user.getOtpExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP code has expired");
        }
        if (user.getPendingPassword() == null) {
            throw new RuntimeException("No pending password change request found");
        }
        user.setPassword(user.getPendingPassword());
        user.setPendingPassword(null);
        user.setResetOtp(null);
        user.setOtpExpiration(null);

        userRepository.save(user);
    }
}