package com.example.Child.Growth.Tracking.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.example.Child.Growth.Tracking.ulti.ConsultationStatus;

@Entity
@Table(name = "consultations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Consultations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Lob
    private String request;

    @Lob
    private String response;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsultationStatus status = ConsultationStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Children child;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}



