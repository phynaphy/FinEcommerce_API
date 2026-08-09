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
    public AuthResponse verifyForgotPassword(VerifyOtpRequest request) {
        User user = userRepository.findByResetOtp(request.getOtp())
                .orElseThrow(() -> new RuntimeException("Invalid OTP code"));
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
            throw new RuntimeException("New password and confirm password do not match");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetOtp(null);
        user.setOtpExpiration(null);
        userRepository.save(user);
    }

    @Override
    public void changePassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email"));
        // Generate 6 digit OTP
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);
        // Store OTP expiration and encoded pending password
        user.setResetOtp(otp);
        user.setOtpExpiration(LocalDateTime.now().plusMinutes(10));
        user.setPendingPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        // Send OTP to email
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    //  Enter email Send OTP Code
    @Override
    public void requestChangePasswordOtp(RequestOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);
        user.setResetOtp(otp);
        user.setOtpExpiration(LocalDateTime.now().plusMinutes(10));
        user.setPendingPassword(null);
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    // Enter OTP code  Verify OTP
    @Override
    public void verifyChangePasswordOtp(VerifyOtpRequest request) {
        User user = userRepository.findByResetOtp(request.getOtp())
                .orElseThrow(() -> new RuntimeException("Invalid OTP code"));

        if (user.getOtpExpiration() == null || user.getOtpExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP code has expired");
        }

        // Flag user as verified in Step 2
        user.setPendingPassword("OTP_VERIFIED");
        userRepository.save(user);
    }

    // Current password New password Confirm password Update password
    @Override
    public void completeChangePassword(String email, CompleteResetPasswordRequest request) {
        // Confirm new password matches confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        // Enforce that Step 2 OTP verification passed
        if (!"OTP_VERIFIED".equals(user.getPendingPassword()) || user.getOtpExpiration() == null || user.getOtpExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP verification expired or incomplete. Please request a new OTP.");
        }

        // Verify current password matches the database
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password and wipe temporary security tokens
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        user.setConfirmPassword(encodedPassword);
        user.setResetOtp(null);
        user.setOtpExpiration(null);
        user.setPendingPassword(null);
        userRepository.save(user);
    }

}