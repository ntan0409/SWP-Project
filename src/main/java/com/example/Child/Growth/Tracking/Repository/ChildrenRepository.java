package com.example.Child.Growth.Tracking.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Child.Growth.Tracking.Model.Children;

public interface ChildrenRepository extends JpaRepository<Children, Long> {
    List<Children> findAll();
    Optional<Children> findById(Long id);
    Children save(Children children);
    void deleteById(Long id);
    Children findByFullName(String fullName);
    Children findByBirthDate(LocalDate birthDate);
    List<Children> findByUserId(Long userId);
}
