package com.example.Child.Growth.Tracking.Service;

import java.util.List;

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
    public Children findById(Long id) {
        return childrenRepository.findById(id).orElse(null);
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
    
}
