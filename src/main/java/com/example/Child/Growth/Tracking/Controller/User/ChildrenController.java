package com.example.Child.Growth.Tracking.Controller.User;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Child.Growth.Tracking.Model.Children;
import com.example.Child.Growth.Tracking.Service.UserService;
import com.example.Child.Growth.Tracking.ulti.Gender;
import com.example.Child.Growth.Tracking.Model.User;

@Controller
@RequestMapping("/children")
public class ChildrenController {

    private final UserService userService;

    public ChildrenController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String manageChildren(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("currentUser", currentUser);
        return "user/children";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("childrenId", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("availableGenders", Gender.values());
        return "user/editChildren"; // Chuyển hướng đến trang editChildren.html
    }

    @GetMapping("/create")
    public String showCreateChildrenForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("currentUser", currentUser);
        return "user/createChildren";
    }
}
