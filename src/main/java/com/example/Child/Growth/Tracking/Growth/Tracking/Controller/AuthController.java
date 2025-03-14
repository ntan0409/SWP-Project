package com.example.Child.Growth.Tracking.Controller;

import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.ulti.UserRole;
import com.example.Child.Growth.Tracking.Security.JwtUtil;
import com.example.Child.Growth.Tracking.Service.UserService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

@Controller
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection for dependencies
    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("page", "login"); 
                
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra nếu đã đăng nhập, chuyển hướng về home
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/home";
        }

        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        Optional<User> userOpt = userService.findByUsername(username);

        // If user is found and password matches
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            String token = jwtUtil.generateToken(userOpt.get().getUsername());
            model.addAttribute("token", token); // Add token to the model for the view
            return "home";  // Redirect to home page
        }

        model.addAttribute("error", "Invalid username or password");
        return "login";  // Return to login page with error message
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("page", "register"); 

        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, 
                        @RequestParam String password,
                        @RequestParam String confirmPassword,
                        @RequestParam String fullname,
                        @RequestParam String email,
                        @RequestParam String phoneNumber,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra tên người dùng đã tồn tại
            if (userService.findByUsername(username).isPresent()) {
                model.addAttribute("error", "Username already exists");
                return "register";  // Trả về trang đăng ký với thông báo lỗi
            }

            // Kiểm tra mật khẩu và xác nhận mật khẩu có giống nhau không
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match.");
                return "register";  // Trả về form đăng ký với lỗi
            }

            // Đăng ký người dùng
            userService.registerUser(username, password, UserRole.MEMBER, fullname, email, phoneNumber);
            redirectAttributes.addFlashAttribute("success", "Registration successful.");

            return "redirect:/login";  // Chuyển hướng đến trang đăng nhập
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";  // Trả về trang đăng ký nếu có lỗi
        }
    }


    // Helper method to convert role string to UserRole enum
    private UserRole getUserRole(String role, Model model) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid role");
            return null; // Return null for invalid role
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("page", "home"); 
        return "home";
    }

    @GetMapping("/check-username")
    @ResponseBody
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Collections.singletonMap("available", !exists);
    }

    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return Collections.singletonMap("available", !exists);
    }

    @GetMapping("/check-phone")
    @ResponseBody
    public Map<String, Boolean> checkPhone(@RequestParam String phone) {
        boolean exists = userService.existsByPhone(phone);
        return Collections.singletonMap("available", !exists);
    }

    @GetMapping("/check-username-edit")
    @ResponseBody
    public Map<String, Boolean> checkUsernameForEdit(
        @RequestParam String username,
        @RequestParam Long userId) {
        boolean exists = userService.existsByUsernameAndIdNot(username, userId);
        return Collections.singletonMap("available", !exists);
    }

    @GetMapping("/check-email-edit")
    @ResponseBody
    public Map<String, Boolean> checkEmailForEdit(
        @RequestParam String email,
        @RequestParam Long userId) {
        boolean exists = userService.existsByEmailAndIdNot(email, userId);
        return Collections.singletonMap("available", !exists);
    }

    @GetMapping("/check-phone-edit")
    @ResponseBody
    public Map<String, Boolean> checkPhoneForEdit(
        @RequestParam String phone,
        @RequestParam Long userId) {
        boolean exists = userService.existsByPhoneNumberAndIdNot(phone, userId);
        return Collections.singletonMap("available", !exists);
    }
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgotPassword";
    }
}
