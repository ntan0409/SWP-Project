package com.example.Child.Growth.Tracking.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.time.LocalDate;
import java.time.Period;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Child.Growth.Tracking.Model.Children;
import com.example.Child.Growth.Tracking.Repository.ChildrenRepository;

@Service
public class ChildrenService {

    private final ChildrenRepository childrenRepository;
    
    public ChildrenService(ChildrenRepository childrenRepository) {
        this.childrenRepository = childrenRepository;
    }

    public List<Children> findAll() {
        return childrenRepository.findAll();
    }
    public List<Children> findByUserId(Long userId) {
        return childrenRepository.findByUserId(userId);
    }
    public Optional<Children> findById(Long id) {
        return childrenRepository.findById(id);
    }
    public Children updateChildren(Children children) {
        try {
            System.out.println("Updating children with ID: " + children);
            Children existingChildren = childrenRepository.findById(children.getId())
                .orElseThrow(() -> new RuntimeException("Children not found"));
            existingChildren.setFullName(children.getFullName());
            existingChildren.setBirthDate(children.getBirthDate());
            existingChildren.setGender(children.getGender());
            return childrenRepository.save(existingChildren);
        } catch (Exception e) {
            throw new RuntimeException("Error updating children: " + e.getMessage());
        }
    }
    public void deleteById(Long id) {
        childrenRepository.deleteById(id);
    }
    public Children save(Children children) {
        try {
            return childrenRepository.save(children);
        } catch (Exception e) {
            throw new RuntimeException("Error creating children: " + e.getMessage());
        }
    }
    public int countByUserId(Long userId) {
        return childrenRepository.countByUserId(userId);
    }
    public Long countTotalChildren() {
        return childrenRepository.count();
    }
    public Map<String, Long> countChildrenByGender() {
        List<Children> children = childrenRepository.findAll();
        return children.stream()
                .collect(Collectors.groupingBy(
                    child -> child.getGender().toString(),
                    Collectors.counting()
                ));
    }
    public Map<String, Long> countChildrenByAgeGroup() {
        List<Children> children = childrenRepository.findAll();
        LocalDate now = LocalDate.now();
        
        return children.stream()
                .collect(Collectors.groupingBy(
                    child -> {
                        int age = Period.between(child.getBirthDate(), now).getYears();
                        if (age < 1) return "Under 1 year";
                        else if (age < 3) return "1-2 years";
                        else if (age < 5) return "3-4 years";
                        else if (age < 7) return "5-6 years";
                        else return "Over 6 years";
                    },
                    Collectors.counting()
                ));
    }
}
