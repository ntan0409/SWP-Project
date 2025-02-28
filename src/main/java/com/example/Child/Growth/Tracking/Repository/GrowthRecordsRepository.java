package com.example.Child.Growth.Tracking.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Child.Growth.Tracking.Model.GrowthRecords;

public interface GrowthRecordsRepository extends JpaRepository<GrowthRecords, Long> {
    List<GrowthRecords> findAll();
    Optional<GrowthRecords> findById(Long id);
    GrowthRecords save(GrowthRecords growthRecord);
    void deleteById(Long id);
    List<GrowthRecords> findByChildId(Long childId);
}
