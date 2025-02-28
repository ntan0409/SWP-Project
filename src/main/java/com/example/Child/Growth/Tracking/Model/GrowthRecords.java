package com.example.Child.Growth.Tracking.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "growth_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrowthRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "child_id", nullable = false)
    private Long childId;

    @Column(nullable = false)
    private Double weight;  // Weight in kg

    @Column(nullable = false)
    private Double height;  // Height in cm

    @Column
    private Double bmi;    // BMI index

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @ManyToOne
    @JoinColumn(name = "child_id", insertable = false, updatable = false)
    private Children child;
}

