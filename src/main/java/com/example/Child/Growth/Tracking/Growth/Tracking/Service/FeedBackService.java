package com.example.Child.Growth.Tracking.Service;

import com.example.Child.Growth.Tracking.Model.FeedBack;
import com.example.Child.Growth.Tracking.Repository.FeedBackRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;


@Service
public class FeedBackService {
    private final FeedBackRepository feedbackRepository;

    public FeedBackService(FeedBackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public List<FeedBack> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    public void deleteFeedbackById(Long id) {
        feedbackRepository.deleteById(id);
    }

    public FeedBack saveFeedback(FeedBack feedback) {
        return feedbackRepository.save(feedback);
    }

    public Optional<FeedBack> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }
}
