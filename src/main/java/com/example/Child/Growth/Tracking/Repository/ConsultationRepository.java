package com.example.Child.Growth.Tracking.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Child.Growth.Tracking.Model.Consultations;
import com.example.Child.Growth.Tracking.ulti.ConsultationStatus;

public interface ConsultationRepository extends JpaRepository<Consultations, Long> {
    Consultations save(Consultations consultation);
    List<Consultations> findByChildId(Long childId);
    List<Consultations> findByChildIdAndStatus(Long childId, ConsultationStatus status);
    List<Consultations> findByDoctorId(Long doctorId);
    List<Consultations> findByStatus(ConsultationStatus status);
    List<Consultations> findByStatusAndDoctorId(ConsultationStatus status, Long doctorId);
}
