package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.response.UserProfileResponse;
import com.example.FIN_ecommerce_API.service.AuthService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.MAIN_PATH + "/auth")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService){
        this.authService = authService;

    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return ResponseEntity.ok(authService.getUserProfile(username));
    }
}
