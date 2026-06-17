package com.example.dean12.repository;

import com.example.dean12.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findBySinhVienMaSV(String maSV);
}
