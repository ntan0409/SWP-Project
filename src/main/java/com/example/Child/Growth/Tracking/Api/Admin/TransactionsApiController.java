package com.example.Child.Growth.Tracking.Api.Admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Child.Growth.Tracking.Model.PaymentTransaction;
import com.example.Child.Growth.Tracking.Service.PaymentTransactionService;
import com.example.Child.Growth.Tracking.Service.UserService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionsApiController {
    private final PaymentTransactionService transactionService;
    private final UserService userService;

    public TransactionsApiController(PaymentTransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
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

}
