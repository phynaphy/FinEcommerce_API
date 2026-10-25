package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.response.UserProfileResponse;
import com.example.FIN_ecommerce_API.dto.test.UpdateUserRequest;
import com.example.FIN_ecommerce_API.service.UserService;
import com.example.FIN_ecommerce_API.utilities.Constant;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(Constant.MAIN_PATH + "/auth")
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper; // Inject ObjectMapper

    public UserController(UserService userService, ObjectMapper objectMapper){
        this.userService = userService;
        this.objectMapper = objectMapper;
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


    @PutMapping(value = "/{username}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> updateProfileWithFile(
            @PathVariable String username,
            @RequestParam("request") String requestJson,
            @RequestParam(value = "file", required = false) MultipartFile file)
            throws com.fasterxml.jackson.core.JsonProcessingException {

        UpdateUserRequest request =
                objectMapper.readValue(requestJson, UpdateUserRequest.class);

        UserProfileResponse response =
                userService.updateUserWithFile(username, request, file);

        return ResponseEntity.ok(response);
    }
}
