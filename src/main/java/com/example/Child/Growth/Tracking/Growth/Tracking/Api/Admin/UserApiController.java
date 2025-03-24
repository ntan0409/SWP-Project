package com.example.Child.Growth.Tracking.Api.Admin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Child.Growth.Tracking.Model.BlogPost;
import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Service.BlogService;
import com.example.Child.Growth.Tracking.Service.UserService;
import com.example.Child.Growth.Tracking.ulti.UserRole;

@RestController
@RequestMapping("/api/users")
public class UserApiController {
    private final UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // Kiểm tra xem username đã tồn tại chưa
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Username already exists"));
        }

        // Kiểm tra xem email đã tồn tại chưa
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Email already exists"));
        }

        // Kiểm tra xem số điện thoại đã tồn tại chưa
        if (userService.existsByPhone(user.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Phone number already exists"));
        }
        if(user.getPassword() == null){
            user.setPassword(passwordEncoder.encode("123456"));
        }
        else{
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User savedUser = userService.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.findById(id)
                .map(existingUser -> {
                    if (!existingUser.getEmail().equals(user.getEmail()) && userService.existsByEmail(user.getEmail())) {
                        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Email already exists"));
                    }

                    if (!existingUser.getPhoneNumber().equals(user.getPhoneNumber()) && userService.existsByPhone(user.getPhoneNumber())) {
                        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Phone number already exists"));
                    }

                    user.setId(id);
                    if (user.getPassword() == null) {
                        user.setPassword(existingUser.getPassword());
                    }
                    else{
                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    User updatedUser = userService.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-username-edit")
    public ResponseEntity<Map<String, Boolean>> checkUsernameForEdit(
            @RequestParam String username,
            @RequestParam Long userId) {
        boolean exists = userService.existsByUsernameAndIdNot(username, userId);
        return ResponseEntity.ok(Collections.singletonMap("available", !exists));
    }

    @GetMapping("/check-email-edit")
    public ResponseEntity<Map<String, Boolean>> checkEmailForEdit(
            @RequestParam String email,
            @RequestParam Long userId) {
        boolean exists = userService.existsByEmailAndIdNot(email, userId);
        return ResponseEntity.ok(Collections.singletonMap("available", !exists));
    }

    @GetMapping("/check-phone-edit")
    public ResponseEntity<Map<String, Boolean>> checkPhoneForEdit(
            @RequestParam String phone,
            @RequestParam Long userId) {
        boolean exists = userService.existsByPhoneNumberAndIdNot(phone, userId);
        return ResponseEntity.ok(Collections.singletonMap("available", !exists));
    }

    @PutMapping("/{id}/update-profile")
    public ResponseEntity<? extends Object> updateProfile(
        @PathVariable Long id,
        @RequestParam(value = "fullname", required = false) String fullname,
        @RequestParam(value = "address", required = false) String address,
        @RequestParam(value = "phone_number", required = false) String phoneNumber,
        @RequestParam(value = "birth_date", required = false) String birthDateStr,
        @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) throws IOException {

        return userService.findById(id)
            .map(user -> {
                // Update user information
                if (fullname != null && !fullname.trim().isEmpty()) {
                    user.setFullName(fullname);
                }
                if (address != null && !address.trim().isEmpty()) {
                    user.setAddress(address);
                }
                if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                    user.setPhoneNumber(phoneNumber);
                }
                if (birthDateStr != null && !birthDateStr.isEmpty()) {
                    try {
                        LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_DATE);
                        user.setBirthDate(birthDate);
                    } catch (DateTimeParseException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(null); // Return a bad request if the date format is invalid
                    }
                }

                // Handle avatar
                if (avatarFile != null && !avatarFile.isEmpty()) {
                    String projectDir = System.getProperty("user.dir");
                    String uploadDir = projectDir + "/src/main/resources/static/images/avatar/";
                    String newFileName = user.getUsername() + ".jpg";
                    String newFilePath = Paths.get(uploadDir, newFileName).toString();

                    // Create directory if it doesn't exist
                    File directory = new File(uploadDir);
                    if (!directory.exists()) {
                        directory.mkdirs(); // Create the directory
                    }

                    // Delete old avatar if exists
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        File oldFile = new File(uploadDir + user.getAvatar());
                        if (oldFile.exists()) {
                            oldFile.delete();
                        }
                    }

                    // Save new avatar
                    File newFile = new File(newFilePath);
                    try {
                        avatarFile.transferTo(newFile);
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(null); // Return an error response if file upload fails
                    }
                    user.setAvatar(newFileName);
                }

                User updatedUser = userService.save(user);
                return ResponseEntity.ok(updatedUser); // Return the updated user
            })
            .orElse(ResponseEntity.notFound().build()); // Return 404 if user not found
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
        @PathVariable Long id,
        @RequestParam("currentPassword") String currentPassword,
        @RequestParam("newPassword") String newPassword,
        @RequestParam("confirmPassword") String confirmPassword) {

        return userService.findById(id)
            .map(user -> {
                // Check current password
                if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Collections.singletonMap("error", "Current password is incorrect."));
                }

                // Check new password and confirm password
                if (!newPassword.equals(confirmPassword)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Collections.singletonMap("error", "New password and confirm password do not match."));
                }

                // Update new password
                user.setPassword(passwordEncoder.encode(newPassword));
                userService.save(user);

                return ResponseEntity.ok(Collections.singletonMap("success", "Password changed successfully."));
            })
            .orElse(ResponseEntity.notFound().build()); // Return 404 if user not found
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<User>> getDoctors() {
        List<User> doctors = userService.findByRole(UserRole.DOCTOR);
        return ResponseEntity.ok(doctors);
    }
}
