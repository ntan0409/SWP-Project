package com.example.Child.Growth.Tracking.Api;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Child.Growth.Tracking.Service.PaymentTransactionService;
import com.example.Child.Growth.Tracking.Service.UserService;
import com.example.Child.Growth.Tracking.Service.VNPayService;

import jakarta.servlet.http.HttpServletRequest;

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

    
} 