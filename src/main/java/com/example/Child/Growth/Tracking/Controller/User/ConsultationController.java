package com.example.Child.Growth.Tracking.Controller.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ConsultationController {
    
    @GetMapping("/consultations/list/{childId}")
    public String listConsultations(@PathVariable Long childId, Model model) {
        model.addAttribute("childId", childId);
        return "user/Consultation/index";
    }

    @GetMapping("/manageConsultations")
    public String manageConsultations() {
        return "doctor/Consultation/index";
    }
}
