package com.example.Child.Growth.Tracking.Controller.User;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Child.Growth.Tracking.Model.Children;
import com.example.Child.Growth.Tracking.Service.ChildrenService;
import com.example.Child.Growth.Tracking.Service.CustomUserDetailsService;
import com.example.Child.Growth.Tracking.Service.UserService;
import com.example.Child.Growth.Tracking.ulti.Gender;
import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Repository.UserRepository;

@Controller
public class ChildrenController {
    private final UserRepository userRepository;
    private final ChildrenService childrenService;

    @Autowired
    public ChildrenController(UserRepository userRepository, ChildrenService childrenService) {
        this.userRepository = userRepository;
        this.childrenService = childrenService;
    }

    @GetMapping("/children")
    public String manageChildren(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("page", "children"); 
        List<Children> children = childrenService.findByUserId(currentUser.getId());
        model.addAttribute("children", children);
        return "user/children";
    }

    @GetMapping("/children/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Children children = childrenService.findById(id);
        model.addAttribute("children", children);
        model.addAttribute("availableGenders", Gender.values());
        return "user/editChildren";
    }

    @PostMapping("/children/update")
    public String updateChildren(@ModelAttribute Children children, RedirectAttributes redirectAttributes) {
        try {
            childrenService.updateChildren(children);
            redirectAttributes.addFlashAttribute("successMessage", "Children updated successfully!");
            return "redirect:/children";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating children: " + e.getMessage());
            return "redirect:/children";
        }
    }

    @GetMapping("/children/delete/{id}")
    public String deleteChildren(@PathVariable Long id) {
        childrenService.deleteById(id);
        return "redirect:/children"; 
    }

    @GetMapping("/children/create")
    public String showCreateChildrenForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("children", new Children());
        model.addAttribute("availableGenders", Gender.values());
        return "user/createChildren";
    }

    @PostMapping("/children/create")
    public String createChildren(@ModelAttribute Children children) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            children.setUserId(currentUser.getId());
            
            childrenService.save(children);
            
            return "redirect:/children";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating children: " + e.getMessage());
        }
    }
}
