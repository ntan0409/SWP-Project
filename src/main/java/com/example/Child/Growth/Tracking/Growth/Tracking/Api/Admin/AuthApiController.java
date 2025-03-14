package com.example.Child.Growth.Tracking.Api.Admin;

import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Security.JwtUtil;
import com.example.Child.Growth.Tracking.Service.UserService;
import com.example.Child.Growth.Tracking.ulti.UserRole;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthApiController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    //     String username = credentials.get("username");
    //     String password = credentials.get("password");

    //     return userService.findByUsername(username)
    //             .filter(user -> passwordEncoder.matches(password, user.getPassword()))
    //             .map(user -> {
    //                 String token = jwtUtil.generateToken(username);
    //                 Map<String, Object> response = new HashMap<>();
    //                 response.put("token", token);
    //                 response.put("user", user);
    //                 return ResponseEntity.ok(response);
    //             })
    //             .orElse(ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid credentials")));
    // }

    // @PostMapping("/register")
    // public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData) {
    //     try {
    //         String username = registrationData.get("username");
    //         String password = registrationData.get("password");
    //         String confirmPassword = registrationData.get("confirmPassword");
    //         String fullname = registrationData.get("fullname");
    //         String email = registrationData.get("email");
    //         String phoneNumber = registrationData.get("phoneNumber");

    //         // Validate password match
    //         if (!password.equals(confirmPassword)) {
    //             return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Passwords do not match"));
    //         }

    //         // Check if username exists
    //         if (userService.existsByUsername(username)) {
    //             return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Username already exists"));
    //         }

    //         // Register user
    //         User user = userService.registerUser(username, password, UserRole.MEMBER, fullname, email, phoneNumber);
    //         return ResponseEntity.ok(Collections.singletonMap("message", "Registration successful"));

    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
    //     }
    // }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(Collections.singletonMap("available", !exists));
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(Collections.singletonMap("available", !exists));
    }

    @GetMapping("/check-phone")
    public ResponseEntity<Map<String, Boolean>> checkPhone(@RequestParam String phone) {
        boolean exists = userService.existsByPhone(phone);
        return ResponseEntity.ok(Collections.singletonMap("available", !exists));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
        userService.sendResetPasswordEmail(email);
        return ResponseEntity.ok(Collections.singletonMap("message", "Reset password email sent"));
    }
}
