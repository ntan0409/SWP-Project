package com.example.Child.Growth.Tracking.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionRef;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String orderInfo;

    @Column(nullable = false)
    private String bankCode;

    @Column(nullable = false)
    private String paymentDate;

    @Column(nullable = false)
    private String status;

    @Column(nullable = true)
    private Long userId;
} 