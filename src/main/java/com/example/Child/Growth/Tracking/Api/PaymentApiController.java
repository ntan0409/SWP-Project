package com.example.Child.Growth.Tracking.Api;

import com.example.Child.Growth.Tracking.Service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import com.example.Child.Growth.Tracking.Service.UserService;
import com.example.Child.Growth.Tracking.Service.PaymentTransactionService;
import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Model.PaymentTransaction;

@RestController
@RequestMapping("/api/payment")
public class PaymentApiController {

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private PaymentTransactionService paymentTransactionService;

    @PostMapping("/create-payment")
    public ResponseEntity<String> createPayment(
            @RequestParam("amount") long amount,
            @RequestParam("orderInfo") String orderInfo,
            HttpServletRequest request) throws UnsupportedEncodingException {
        String paymentUrl = vnPayService.createPaymentUrl(amount, orderInfo, request);
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/payment-callback")
    public ResponseEntity<String> paymentCallback(
            @RequestParam(value = "vnp_ResponseCode") String responseCode,
            @RequestParam(value = "vnp_TxnRef") String txnRef) {
        if ("00".equals(responseCode)) {
            // Thanh toán thành công
            // Thêm logic xử lý khi thanh toán thành công
            return ResponseEntity.ok("Payment successful!");
        } else {
            // Thanh toán thất bại
            // Thêm logic xử lý khi thanh toán thất bại
            return ResponseEntity.badRequest().body("Payment failed!");
        }
    }

    @GetMapping("/payment-history")
    public ResponseEntity<?> getPaymentHistory(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
            }

            String username = authentication.getName();
            com.example.Child.Growth.Tracking.Model.User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            List<PaymentTransaction> transactions = paymentTransactionService
                .getTransactionsByUserId(user.getId());

            if (transactions.isEmpty()) {
                return ResponseEntity
                    .ok()
                    .body(Collections.emptyList());
            }

            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching payment history: " + e.getMessage());
        }
    }
} 