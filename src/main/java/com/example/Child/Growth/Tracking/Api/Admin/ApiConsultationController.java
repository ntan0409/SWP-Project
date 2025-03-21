package com.example.Child.Growth.Tracking.Api.Admin;

import com.example.Child.Growth.Tracking.Model.Children;
import com.example.Child.Growth.Tracking.Model.Consultations;
import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Service.ChildrenService;
import com.example.Child.Growth.Tracking.Service.ConsultationService;
import com.example.Child.Growth.Tracking.Service.UserService;
import com.example.Child.Growth.Tracking.ulti.UserRole;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/consultations")
public class ApiConsultationController {
    private final ConsultationService consultationService;
    private final ChildrenService childrenService;
    private final UserService userService;

    public ApiConsultationController(ConsultationService consultationService, 
                                   ChildrenService childrenService, 
                                   UserService userService) {
        this.consultationService = consultationService;
        this.childrenService = childrenService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Consultations>> getAllConsultations() {
        List<Consultations> consultations = consultationService.findAll();
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/create-data")
    public ResponseEntity<Map<String, Object>> getCreateConsultationData() {
        List<User> members = userService.findByRole(UserRole.MEMBER);
        List<User> doctors = userService.findByRole(UserRole.DOCTOR);
        
        Map<Long, List<Children>> allChildrenMap = new HashMap<>();
        for (User member : members) {
            List<Children> children = childrenService.findByUserId(member.getId());
            allChildrenMap.put(member.getId(), children);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("members", members);
        response.put("doctors", doctors);
        response.put("allChildrenMap", allChildrenMap);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Consultations> createConsultation(@RequestBody Consultations consultation) {
        consultation.setCreatedAt(LocalDate.now());
        Consultations saved = consultationService.save(consultation);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
