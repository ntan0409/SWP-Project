package com.example.Child.Growth.Tracking.Api.Admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.Child.Growth.Tracking.Model.FeedBack;
import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Service.FeedBackService;
import com.example.Child.Growth.Tracking.Service.UserService;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedBackApiController {
    private final FeedBackService feedbackService;
    private final UserService userService;

    public FeedBackApiController(FeedBackService feedbackService, UserService userService) {
        this.feedbackService = feedbackService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<FeedBack>> getAllFeedbacks() {
        List<FeedBack> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedBack> getFeedback(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FeedBack> createFeedback(@RequestBody FeedBack feedback) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        feedback.setUser(user);
        FeedBack savedFeedback = feedbackService.saveFeedback(feedback);
        return ResponseEntity.ok(savedFeedback);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        if (!feedbackService.getFeedbackById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        feedbackService.deleteFeedbackById(id);
        return ResponseEntity.ok().build();
    }

}
