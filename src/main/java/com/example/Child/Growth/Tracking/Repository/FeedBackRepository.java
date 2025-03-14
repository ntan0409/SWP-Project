package com.example.Child.Growth.Tracking.Repository;

import com.example.Child.Growth.Tracking.Model.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedBackRepository extends JpaRepository<FeedBack, Long> {
    List<FeedBack> findByUserId(Long userId);
}
