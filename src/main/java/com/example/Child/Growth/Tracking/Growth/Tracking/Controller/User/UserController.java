package com.example.Child.Growth.Tracking.Controller.User;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Service.UserService;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.io.IOException;
@Controller
@RequestMapping("/profile")
public class UserController {

    private final UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Autowire the UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profile(Model model) {
        model.addAttribute("page", "profile"); 

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        // Kiểm tra nếu người dùng đã đăng nhập
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();  // Lấy username của người dùng

            // Giả sử bạn có một phương thức để lấy thông tin người dùng từ username
            User user = userService.findByUsername(username).orElse(null);  // Trả về user nếu tìm thấy, nếu không trả về null

            // Thêm đối tượng user vào model
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
        return "user/profile/profile";
    }

    @GetMapping("/update")
    public String profileUpdate(Model model) {
        model.addAttribute("page", "profile-update");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        
        return "user/profile/profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            @RequestParam(value = "fullname", required = false) String fullname,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "phone_number", required = false) String phoneNumber,
            @RequestParam(value = "birth_date", required = false) String birthDateStr,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
            RedirectAttributes redirectAttributes) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // Kiểm tra số điện thoại có bị trùng không (ngoại trừ chính user đang đăng nhập)
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            Optional<User> existingUser = userService.findByPhoneNumber(phoneNumber);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Phone number already exists!");
                return "redirect:/profile/update";
            }
            user.setPhoneNumber(phoneNumber);
        }
        
        // Chỉ cập nhật nếu có giá trị mới
        if (fullname != null && !fullname.trim().isEmpty()) {
            user.setFullName(fullname);
        }
        if (address != null && !address.trim().isEmpty()) {
            user.setAddress(address);
        } else {
            user.setAddress(null);
        }

        // Cập nhật birthDate nếu hợp lệ
        if (birthDateStr != null && !birthDateStr.isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_DATE);
                user.setBirthDate(birthDate);
            } catch (DateTimeParseException e) {
                System.out.println("Lỗi parse ngày sinh: " + e.getMessage());
            }
        } else {
            user.setBirthDate(null);
        }

        // Kiểm tra và cập nhật avatar nếu có ảnh mới
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String projectDir = System.getProperty("user.dir");

            String uploadDir = projectDir + "/src/main/resources/static/images/avatar/";
            System.out.println("url: " + uploadDir);

            String newFileName = user.getUsername() + ".jpg";
            String newFilePath = Paths.get(uploadDir, newFileName).toString();

            // Xóa avatar cũ nếu có
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                File oldFile = new File(uploadDir + user.getAvatar());
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            // Lưu avatar mới
            File newFile = new File(newFilePath);
            avatarFile.transferTo(newFile);
            user.setAvatar(newFileName);
        }

        userService.save(user); // Lưu thay đổi vào database

        return "redirect:/profile";
    }

    @GetMapping("/changepass")
    public String changePass(Model model) {
        model.addAttribute("page", "profile-changepass");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
        
        return "user/profile/profile";
    }

    @PostMapping("/changepass")
    public String changePass(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra nếu người dùng chưa đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // Kiểm tra mật khẩu hiện tại có đúng không
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            // model.addAttribute("error", "Current password is incorrect.");

            return "redirect:/profile/changepass";
        }

        // Kiểm tra mật khẩu mới có khớp không
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password and confirm password do not match.");
            // model.addAttribute("error", "New password and confirm password do not match.");

            return "redirect:/profile/changepass";
        }
        
        // Mã hóa mật khẩu mới và cập nhật vào database
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

        redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/profile";
    }

}
