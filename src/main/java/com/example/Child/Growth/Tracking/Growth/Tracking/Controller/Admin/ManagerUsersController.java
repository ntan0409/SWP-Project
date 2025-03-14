package com.example.Child.Growth.Tracking.Controller.Admin;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Service.UserService;
import com.example.Child.Growth.Tracking.ulti.UserRole;

@Controller
@RequestMapping("/manageUsers")
public class ManagerUsersController {

    private final UserService userService;

    // Autowire the UserService
    public ManagerUsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String manageUsers(Model model) {
        model.addAttribute("page", "manageUsers"); 

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
        
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/manageUsers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("availableRoles", UserRole.values());
        return "admin/editUser";
    }

    // @PostMapping("/update")
    // public String updateUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
    //     try {
    //         User existingUser = userService.findById(user.getId()).orElse(null);
    //         if (existingUser == null) {
    //             redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
    //             return "redirect:/manageUsers";
    //         }
    
    //         if (!existingUser.getUsername().equals(user.getUsername()) && userService.existsByUsername(user.getUsername())) {
    //             redirectAttributes.addFlashAttribute("errorMessage", "Username already exists!");
    //             return "redirect:/manageUsers/edit/" + user.getId();
    //         }
    
    //         if (!existingUser.getEmail().equals(user.getEmail()) && userService.existsByEmail(user.getEmail())) {
    //             redirectAttributes.addFlashAttribute("errorMessage", "Email already exists!");
    //             return "redirect:/manageUsers/edit/" + user.getId();
    //         }
    
    //         if (!existingUser.getPhoneNumber().equals(user.getPhoneNumber()) && userService.existsByPhone(user.getPhoneNumber())) {
    //             redirectAttributes.addFlashAttribute("errorMessage", "Phone number already exists!");
    //             return "redirect:/manageUsers/edit/" + user.getId();
    //         }
    
    //         existingUser.setFullName(user.getFullName());
    //         existingUser.setEmail(user.getEmail());
    //         existingUser.setPhoneNumber(user.getPhoneNumber());
    //         existingUser.setRole(user.getRole());
    
    //         userService.updateUser(existingUser);
    //         redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
    //         return "redirect:/manageUsers";
    //     } catch (Exception e) {
    //         redirectAttributes.addFlashAttribute("errorMessage", "Error updating user: " + e.getMessage());
    //         return "redirect:/manageUsers/edit/" + user.getId();
    //     }
    // }
    

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/manageUsers"; // Redirect về trang quản lý users sau khi xóa
    }

    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("availableRoles", UserRole.values());
        return "admin/createUser";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra username đã tồn tại chưa
            if (userService.existsByUsername(user.getUsername())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username already exists!");
                return "redirect:/manageUsers/create";
            }
    
            // Kiểm tra email đã tồn tại chưa
            if (userService.existsByEmail(user.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email already exists!");
                return "redirect:/manageUsers/create";
            }
    
            if (userService.existsByPhone(user.getPhoneNumber())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Phone number already exists!");
                return "redirect:/manageUsers/create";
            }

            userService.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
            return "redirect:/manageUsers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating user: " + e.getMessage());
            return "redirect:/manageUsers/create";
        }
    }
    
}
