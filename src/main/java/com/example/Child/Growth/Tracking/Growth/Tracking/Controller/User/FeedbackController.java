package com.example.Child.Growth.Tracking.Controller.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.Child.Growth.Tracking.Model.FeedBack;
import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Repository.FeedBackRepository;
import com.example.Child.Growth.Tracking.Repository.UserRepository;

import java.util.List;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    
    private final UserRepository userRepository;
    private final FeedBackRepository feedbackRepository;

    @Autowired
    public FeedbackController(UserRepository userRepository, FeedBackRepository feedbackRepository) {
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping
    public String feedback(Model model) {
        model.addAttribute("page", "feedback");

        // Lấy thông tin user hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
           

        model.addAttribute("currentUser",currentUser);

        // Lấy danh sách feedback đã gửi
        List<FeedBack> feedbackList = feedbackRepository.findAll();
        model.addAttribute("feedbackList", feedbackList);
        
        return "user/feedback/feedback";
    }

    @PostMapping("/submit")
    public String submitFeedback(@RequestParam("rating") Integer rating,
                                 @RequestParam("comment") String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo feedback mới
        FeedBack feedback = new FeedBack();
        feedback.setUser(currentUser);  
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedbackRepository.save(feedback);

        return "redirect:/feedback";
    }
}
