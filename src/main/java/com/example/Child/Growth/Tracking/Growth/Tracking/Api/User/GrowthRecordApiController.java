package com.example.Child.Growth.Tracking.Api.User;

import com.example.Child.Growth.Tracking.Model.GrowthRecords;
import com.example.Child.Growth.Tracking.Service.GrowthRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/growth-records")
public class GrowthRecordApiController {
    private final GrowthRecordService growthRecordService;

    public GrowthRecordApiController(GrowthRecordService growthRecordService) {
        this.growthRecordService = growthRecordService;
    }

    @GetMapping("/child/{childId}")
    public ResponseEntity<List<GrowthRecords>> getGrowthRecords(@PathVariable Long childId) {
        List<GrowthRecords> records = growthRecordService.findByChildId(childId)
            .stream()
            .sorted(Comparator.comparing(GrowthRecords::getRecordDate))
            .collect(Collectors.toList());
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{childrenId}/edit/{id}")
    public ResponseEntity<GrowthRecords> getGrowthRecord(@PathVariable Long id, @PathVariable Long childrenId) {
        GrowthRecords record = growthRecordService.findById(id);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/child/{childId}")
    public ResponseEntity<GrowthRecords> createGrowthRecord(@PathVariable Long childId, 
                                                          @RequestBody GrowthRecords record) {
        record.setId(null);
        record.setChildId(childId);
        GrowthRecords saved = growthRecordService.save(record);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrowthRecords> updateGrowthRecord(@PathVariable Long id, 
                                                          @RequestBody GrowthRecords record) {
        record.setId(id);
        GrowthRecords updated = growthRecordService.updateGrowthRecord(record);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrowthRecord(@PathVariable Long id) {
        growthRecordService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
