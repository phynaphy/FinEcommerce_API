package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.AuthResponse;
import com.example.FIN_ecommerce_API.dto.LoginRequest;
import com.example.FIN_ecommerce_API.dto.RegisterRequest;
import com.example.FIN_ecommerce_API.service.AuthService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constant.MAIN_PATH + "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}