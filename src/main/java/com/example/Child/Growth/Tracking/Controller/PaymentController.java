package com.example.Child.Growth.Tracking.Controller;

import com.example.Child.Growth.Tracking.Model.PaymentTransaction;
import com.example.Child.Growth.Tracking.Service.PaymentTransactionService;
import com.example.Child.Growth.Tracking.Service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Service.UserService;

@Controller
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private PaymentTransactionService paymentTransactionService;

    @GetMapping("/payment")
    public String showPaymentPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("page", "payment");
        return "user/payment";
    }
    @GetMapping("/paymentSuccess")
    public String showPaymentSuccessPage(Model model) {
        model.addAttribute("page", "payment");
        return "user/paymentSuccess";
    }

    @GetMapping("/vnpay-payment")
    public String vnpayPayment(
            @RequestParam(value = "vnp_ResponseCode", required = false) String responseCode,
            @RequestParam(value = "vnp_TransactionStatus", required = false) String transactionStatus,
            @RequestParam(value = "vnp_TxnRef", required = false) String txnRef,
            @RequestParam(value = "vnp_Amount", required = false) String amount,
            @RequestParam(value = "vnp_OrderInfo", required = false) String orderInfo,
            @RequestParam(value = "vnp_BankCode", required = false) String bankCode,
            @RequestParam(value = "vnp_PayDate", required = false) String payDate,
            HttpServletRequest request,
            Model model
    ) {
        if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();
                User user = userService.findByUsername(username).orElse(null);

                PaymentTransaction transaction = new PaymentTransaction();
                transaction.setTransactionRef(txnRef);
                transaction.setAmount(Long.parseLong(amount) / 100);
                transaction.setOrderInfo(orderInfo);
                transaction.setBankCode(bankCode);
                transaction.setPaymentDate(payDate);
                transaction.setStatus("SUCCESS");
                transaction.setUserId(user.getId());
                paymentTransactionService.save(transaction);

                return "redirect:/paymentSuccess";
            } catch (Exception e) {
                model.addAttribute("error", "Có lỗi xảy ra trong quá trình xử lý thanh toán");
            }
        }
        return "redirect:/home";
    }
    @GetMapping("/paymentHistory")
    public String showPaymentHistory(Model model) {
        model.addAttribute("page", "paymentHistory");
        return "user/paymentHistory";
    }
} 