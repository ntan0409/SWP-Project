package com.example.Child.Growth.Tracking.Api.User;

import com.example.Child.Growth.Tracking.Model.Consultations;
import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Service.ConsultationService;
import com.example.Child.Growth.Tracking.Service.UserService;
import com.example.Child.Growth.Tracking.ulti.ConsultationStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationApiController {
    private final ConsultationService consultationService;
    private final UserService userService;

    public ConsultationApiController(ConsultationService consultationService, UserService userService) {
        this.consultationService = consultationService;
        this.userService = userService;
    }

    @PostMapping("/create/{childId}")
    public ResponseEntity<?> createConsultation(@PathVariable Long childId, 
                                              @RequestBody Consultations consultation) {
        consultation.setCreatedAt(LocalDate.now());
        consultation.setStatus(ConsultationStatus.PENDING);
        Consultations saved = consultationService.save(consultation);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/child/{childId}")
    public ResponseEntity<?> getConsultationsByChild(@PathVariable Long childId) {
        List<Consultations> pending = consultationService.findByChildIdAndStatusPending(childId);
        List<Consultations> completed = consultationService.findByChildIdAndStatus(childId);
        
        // Sort by createdAt in descending order
        Comparator<Consultations> byDateDesc = (a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt());
        pending.sort(byDateDesc);
        completed.sort(byDateDesc);
        
        return ResponseEntity.ok(Map.of(
            "pending", pending,
            "completed", completed
        ));
    }

    @GetMapping("/doctor")
    public ResponseEntity<?> getDoctorConsultations() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User doctor = userService.findByUsername(auth.getName())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        List<Consultations> consultations = consultationService.findByDoctorId(doctor.getId());
        consultations.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/doctor/{status}")
    public ResponseEntity<?> getDoctorConsultationsByStatus(@PathVariable String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User doctor = userService.findByUsername(auth.getName())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        ConsultationStatus consultationStatus = ConsultationStatus.valueOf(status.toUpperCase());
        List<Consultations> consultations = consultationService
            .findByStatusAndDoctorId(consultationStatus, doctor.getId());
        consultations.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        return ResponseEntity.ok(consultations);
    }

    @PutMapping("/{id}/respond")
    public ResponseEntity<?> respondToConsultation(@PathVariable Long id, 
                                                 @RequestBody Map<String, String> payload) {
        String response = payload.get("response");
        if (response == null || response.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Response cannot be empty");
        }
        
        consultationService.updateResponse(id, response);
        return ResponseEntity.ok().build();
    }
}
