package com.example.Child.Growth.Tracking.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.Child.Growth.Tracking.ulti.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private LocalDate birthDate;

    @Column(nullable = true, length = 255) 
    private String address; 

    @Column(nullable = false, unique = true, length = 15)
    private String phoneNumber;

    @Column(nullable = true, length = 500)
    private String avatar; 

    @Column(nullable = true)
    private String resetToken;

    @Column(nullable = true)
    private String paymentStatus;
    
    @Column(nullable = true)
    private LocalDateTime resetTokenExpiry;
}
