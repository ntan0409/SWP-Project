package com.example.Child.Growth.Tracking.Api.Admin;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.Child.Growth.Tracking.Model.PaymentTransaction;
import com.example.Child.Growth.Tracking.Service.PaymentTransactionService;
import com.example.Child.Growth.Tracking.Service.UserService;
import com.example.Child.Growth.Tracking.Service.ChildrenService;

@RestController
@RequestMapping("/api/dashboard")
public class ApiDashboardController {
    private final PaymentTransactionService transactionService;
    private final UserService userService;
    private final ChildrenService childrenService;

    public ApiDashboardController(
            PaymentTransactionService transactionService, 
            UserService userService,
            ChildrenService childrenService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.childrenService = childrenService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentTransaction>> getAllTransactions() {
        List<PaymentTransaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentTransaction> getTransaction(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        List<PaymentTransaction> transactions = transactionService.getAllTransactions();
        Long totalRevenue = transactions.stream()
                .filter(t -> "success".equalsIgnoreCase(t.getStatus()))
                .mapToLong(PaymentTransaction::getAmount)
                .sum();
                
        Map<String, Long> monthlyRevenue = transactions.stream()
                .filter(t -> "success".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.groupingBy(
                    t -> t.getPaymentDate().substring(0, 7),
                    Collectors.summingLong(PaymentTransaction::getAmount)
                ));

        Long totalChildren = childrenService.countTotalChildren();
        
        statistics.put("totalRevenue", totalRevenue);
        statistics.put("monthlyRevenue", monthlyRevenue);
        statistics.put("totalChildren", totalChildren);
        
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        Map<String, Object> dashboardData = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            userService.findByUsername(username)
                .ifPresent(user -> dashboardData.put("user", user));
        }

        dashboardData.put("totalChildren", childrenService.countTotalChildren());
        dashboardData.put("childrenByGender", childrenService.countChildrenByGender());
        dashboardData.put("childrenByAge", childrenService.countChildrenByAgeGroup());

        Map<String, Double> last12MonthsRevenue = getLast12MonthsRevenue();
        dashboardData.put("totalRevenue", transactionService.calculateTotalRevenue());
        dashboardData.put("revenueByMonth", last12MonthsRevenue);

        return ResponseEntity.ok(dashboardData);
    }

    private Map<String, Double> getLast12MonthsRevenue() {
        Map<String, Double> monthlyRevenue = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (int i = 11; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            monthlyRevenue.put(date.format(formatter), 0.0);
        }
        
        Map<String, Double> actualRevenue = transactionService.getRevenueByMonth();
        monthlyRevenue.forEach((month, value) -> {
            if (actualRevenue.containsKey(month)) {
                monthlyRevenue.put(month, actualRevenue.get(month));
            }
        });
        
        return monthlyRevenue;
    }
}
