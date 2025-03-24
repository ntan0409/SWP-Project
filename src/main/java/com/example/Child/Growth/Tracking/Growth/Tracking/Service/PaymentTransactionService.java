package com.example.Child.Growth.Tracking.Service;

import com.example.Child.Growth.Tracking.Model.PaymentTransaction;
import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Repository.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PaymentTransactionService {

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public PaymentTransaction save(PaymentTransaction transaction) {
        User user = userService.findById(transaction.getUserId()).orElse(null);
        if (user != null) {
            user.setPaymentStatus("paid");
            userService.save(user);
        }
        return paymentTransactionRepository.save(transaction);
    }

    public List<PaymentTransaction> getAllTransactions() {
        return paymentTransactionRepository.findTopNOrderByPaymentDateDesc(10);
    }

    public Optional<PaymentTransaction> getTransactionById(Long id) {
        return paymentTransactionRepository.findById(id);
    }

    public Optional<PaymentTransaction> getTransactionByRef(String transactionRef) {
        return paymentTransactionRepository.findByTransactionRef(transactionRef);
    }

    public List<PaymentTransaction> getTransactionsByStatus(String status) {
        return paymentTransactionRepository.findByStatus(status);
    }

    @Transactional
    public void updateTransactionStatus(Long id, String status) {
        Optional<PaymentTransaction> transaction = paymentTransactionRepository.findById(id);
        transaction.ifPresent(t -> {
            t.setStatus(status);
            paymentTransactionRepository.save(t);
        });
    }

    @Transactional
    public void deleteTransaction(Long id) {
        paymentTransactionRepository.deleteById(id);
    }

    public boolean isTransactionExists(String transactionRef) {
        return paymentTransactionRepository.findByTransactionRef(transactionRef) != null;
    }

    public List<PaymentTransaction> getTransactionsByUserId(Long userId) {
        return paymentTransactionRepository.findByUserId(userId);
    }

    public Double calculateTotalRevenue() {
        List<PaymentTransaction> transactions = paymentTransactionRepository.findByStatus("success");
        return transactions.stream()
                .mapToDouble(PaymentTransaction::getAmount)
                .sum();
    }

    public Map<String, Double> getRevenueByMonth() {
        List<PaymentTransaction> transactions = paymentTransactionRepository.findByStatus("success");
        return transactions.stream()
                .collect(Collectors.groupingBy(
                    transaction -> {
                        LocalDateTime date = LocalDateTime.parse(
                            transaction.getPaymentDate(),
                            DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                        );
                        return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                    },
                    Collectors.summingDouble(PaymentTransaction::getAmount)
                ));
    }
} 