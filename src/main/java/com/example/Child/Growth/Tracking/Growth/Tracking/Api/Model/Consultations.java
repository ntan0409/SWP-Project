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
    private Long memberId;
    private Long doctorId;
    private Long childId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}



