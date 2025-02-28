package com.example.Child.Growth.Tracking.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Child.Growth.Tracking.Model.GrowthRecords;
import com.example.Child.Growth.Tracking.Repository.GrowthRecordsRepository;

@Service
public class GrowthRecordService {

    private final GrowthRecordsRepository growthRecordsRepository;
    
    public GrowthRecordService(GrowthRecordsRepository growthRecordsRepository) {
        this.growthRecordsRepository = growthRecordsRepository;
    }

    public List<GrowthRecords> findAll() {
        return growthRecordsRepository.findAll();
    }
    public GrowthRecords findById(Long id) {
        return growthRecordsRepository.findById(id).orElse(null);
    }
    public GrowthRecords updateGrowthRecord(GrowthRecords growthRecord) {
        try {
            System.out.println("Updating growth record with ID: " + growthRecord);
            GrowthRecords existingGrowthRecord = growthRecordsRepository.findById(growthRecord.getId())
                .orElseThrow(() -> new RuntimeException("Growth record not found"));
            existingGrowthRecord.setWeight(growthRecord.getWeight());
            existingGrowthRecord.setHeight(growthRecord.getHeight());
            existingGrowthRecord.setBmi(growthRecord.getBmi());
            existingGrowthRecord.setRecordDate(growthRecord.getRecordDate());
            return growthRecordsRepository.save(existingGrowthRecord);
        } catch (Exception e) {
            throw new RuntimeException("Error updating growth record: " + e.getMessage());
        }
    }
    public void deleteById(Long id) {
        growthRecordsRepository.deleteById(id);
    }
    public GrowthRecords save(GrowthRecords growthRecord) {
        try {
            return growthRecordsRepository.save(growthRecord);
        } catch (Exception e) {
            throw new RuntimeException("Error creating growth record: " + e.getMessage());
        }
    }
    public List<GrowthRecords> findByChildId(Long childId) {
        return growthRecordsRepository.findByChildId(childId);
    }
}
