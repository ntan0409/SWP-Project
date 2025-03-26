package com.example.Child.Growth.Tracking.Controller.Admin;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Service.UserService;
import com.example.Child.Growth.Tracking.Service.PaymentTransactionService;
import com.example.Child.Growth.Tracking.Service.ChildrenService;

import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final UserService userService;
    private final PaymentTransactionService paymentService;
    private final ChildrenService childrenService;

    public DashboardController(UserService userService, 
                             PaymentTransactionService paymentService,
                             ChildrenService childrenService) {
        this.userService = userService;
        this.paymentService = paymentService;
        this.childrenService = childrenService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("page", "dashboard");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
            }
        }

        Long totalChildren = childrenService.countTotalChildren();
        Map<String, Long> childrenByGender = childrenService.countChildrenByGender();
        Map<String, Long> childrenByAge = childrenService.countChildrenByAgeGroup();

        model.addAttribute("totalChildren", totalChildren);
        model.addAttribute("childrenByGender", childrenByGender);
        model.addAttribute("childrenByAge", childrenByAge);

        Double totalRevenue = paymentService.calculateTotalRevenue();
        model.addAttribute("totalRevenue", totalRevenue);

        Map<String, Double> revenueByMonth = paymentService.getRevenueByMonth();
        model.addAttribute("revenueByMonth", revenueByMonth);

        return "admin/dashboard";
    }
}
