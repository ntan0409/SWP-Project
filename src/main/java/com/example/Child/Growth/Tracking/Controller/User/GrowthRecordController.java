package com.example.Child.Growth.Tracking.Controller.User;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Child.Growth.Tracking.Model.Children;
import com.example.Child.Growth.Tracking.Model.GrowthRecords;
import com.example.Child.Growth.Tracking.Service.ChildrenService;
import com.example.Child.Growth.Tracking.Service.CustomUserDetailsService;
import com.example.Child.Growth.Tracking.Service.GrowthRecordService;
import com.example.Child.Growth.Tracking.Service.UserService;

@Controller
@RequestMapping("/children")
public class GrowthRecordController {
    private final GrowthRecordService growthRecordService;
    private final ChildrenService childrenService;


    @Autowired
    public GrowthRecordController(GrowthRecordService growthRecordService, ChildrenService childrenService) {
        this.growthRecordService = growthRecordService;
        this.childrenService = childrenService;
    }

    @GetMapping("/growth-records/{id}")
    public String manageGrowthRecords(@PathVariable Long id, Model model) {
        model.addAttribute("childrenId", id);
        return "user/Growth/index";
    }

    @GetMapping("/growth-records/{childrenId}/edit/{id}")
    public String showEditForm(@PathVariable Long id, @PathVariable Long childrenId, Model model) {
        model.addAttribute("childrenId", childrenId);
        model.addAttribute("recordId", id);
        model.addAttribute("growthRecord", new GrowthRecords());
        return "user/Growth/edit";
    }

    @PostMapping("/growth-records/update")
    public String updateGrowthRecord(@ModelAttribute GrowthRecords growthRecord, RedirectAttributes redirectAttributes) {
        try {
            growthRecordService.updateGrowthRecord(growthRecord);
            redirectAttributes.addFlashAttribute("successMessage", "Growth record updated successfully!");
            return "redirect:/children/growth-records/" + growthRecord.getChildId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating growth record: " + e.getMessage());
            return "redirect:/children/growth-records/" + growthRecord.getChildId();
        }
    }

    @GetMapping("/growth-records/{childrenId}/delete/{id}")
    public String deleteGrowthRecord(@PathVariable Long id, @PathVariable Long childrenId) {
        growthRecordService.deleteById(id);
        return "redirect:/children/growth-records/" + childrenId; 
    }

    @GetMapping("/growth-records/{id}/create")
    public String showCreateGrowthRecordForm(@PathVariable Long id, Model model) {
        model.addAttribute("childrenId", id);
        return "user/Growth/create";
    }

    @PostMapping("/growth-records/{id}/create")
    public String createGrowthRecord(@ModelAttribute GrowthRecords growthRecord, @PathVariable Long id) {
        try {
            growthRecord.setId(null);
            growthRecord.setChildId(id);
            growthRecordService.save(growthRecord);
            
            return "redirect:/children/growth-records/" + id;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating growth record: " + e.getMessage());
        }
    }
}
