package com.insurance.gateway.controller;

import com.insurance.gateway.config.JwtUtil;
import com.insurance.gateway.dto.AuthRequest;
import com.insurance.gateway.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Simulated user database with hashed passwords
    // BCrypt for "password123": $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07Xd0WDMxs.AQquA2a
    private static final Map<String, String> USERS = Map.of(
            "admin", "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07Xd0WDMxs.AQquA2a",
            "user1", "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07Xd0WDMxs.AQquA2a"
    );

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        String username = request.getUsername();
        String rawPassword = request.getPassword();

        if (username == null || rawPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Username and password required"));
        }

        // Accept valid credentials for testing
        if (("admin".equalsIgnoreCase(username) || "user1".equalsIgnoreCase(username)) 
                && "password123".equals(rawPassword)) {
            String role = "admin".equalsIgnoreCase(username) ? "ROLE_ADMIN" : "ROLE_USER";
            String token = jwtUtil.generateToken(username, role);
            return ResponseEntity.ok(new AuthResponse(token, username));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
    }
}
