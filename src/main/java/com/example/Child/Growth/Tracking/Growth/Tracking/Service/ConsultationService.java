package com.example.Child.Growth.Tracking.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Child.Growth.Tracking.Model.Children;
import com.example.Child.Growth.Tracking.Model.Consultations;
import com.example.Child.Growth.Tracking.Repository.ChildrenRepository;
import com.example.Child.Growth.Tracking.Repository.ConsultationRepository;
import com.example.Child.Growth.Tracking.ulti.ConsultationStatus;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    
    public ConsultationService(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    public List<Consultations> findAll() {
        return consultationRepository.findAll();
    }
    public Consultations save(Consultations consultation) {
        try {
            consultation.setCreatedAt(LocalDate.now());
            return consultationRepository.save(consultation);
        } catch (Exception e) {
            throw new RuntimeException("Error creating consultation: " + e.getMessage());
        }
    }
    public List<Consultations> findByDoctorId(Long doctorId) {
        return consultationRepository.findByDoctorId(doctorId);
    }
    public List<Consultations> findByChildId(Long childId) {
        return consultationRepository.findByChildId(childId);
    }
    public List<Consultations> findByChildIdAndStatus(Long childId) {
        return consultationRepository.findByChildIdAndStatus(childId, ConsultationStatus.COMPLETED);
    }
    public List<Consultations> findByChildIdAndStatusPending(Long childId) {
        return consultationRepository.findByChildIdAndStatus(childId, ConsultationStatus.PENDING);
    }
    public List<Consultations> findByStatusAndDoctorId(ConsultationStatus status, Long doctorId) {
        return consultationRepository.findByStatusAndDoctorId(status, doctorId);
    }
    public void updateResponse(Long id, String response) {
        Consultations consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));
        consultation.setResponse(response);
        consultation.setStatus(ConsultationStatus.COMPLETED);
        consultationRepository.save(consultation);
    }
    public void deleteById(Long id) {
        consultationRepository.deleteById(id);
    }
}
