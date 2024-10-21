package com.diplom.cloudstorage.controllers;

import com.diplom.cloudstorage.dto.LoginRequest;
import com.diplom.cloudstorage.dto.LoginResponse;
import com.diplom.cloudstorage.security.JwtUtils;
import com.diplom.cloudstorage.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AuthController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Received loginUser request with login: {}", loginRequest.getLogin());

        String jwt = authService.loginUser(loginRequest.getLogin(), loginRequest.getPassword());

        logger.info("User logged in successfully: {}", loginRequest.getLogin());
        return ResponseEntity.ok(new LoginResponse(jwt));

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("auth-token") String authToken) {
        logger.info("Received logoutUser request with authToken: {}", authToken);

        String owner = jwtUtils.getUserNameFromAuthToken(authToken);
        authService.logoutUser(owner);

        logger.info("User logged out successfully: {}", owner);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/login")
    public ResponseEntity<?> login() {
        logger.info("Received login request");
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
