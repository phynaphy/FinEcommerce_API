package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.response.UserProfileResponse;
import com.example.FIN_ecommerce_API.service.AuthService;
import com.example.FIN_ecommerce_API.service.UserService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(Constant.MAIN_PATH + "/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/get-profile/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String username) {
        UserProfileResponse userProfile = userService.getUserProfile(username);
        return ResponseEntity.ok(userProfile);
    }

    @PostMapping(value = "/{username}/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> uploadProfilePicture(
            @PathVariable String username,
            @RequestParam("file") MultipartFile file) {
        UserProfileResponse response = userService.uploadProfilePicture(username, file);
        return ResponseEntity.ok(response);
    }
}
