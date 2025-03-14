package com.example.Child.Growth.Tracking.Api.User;

import com.example.Child.Growth.Tracking.Model.Children;
import com.example.Child.Growth.Tracking.Service.ChildrenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/children")
public class ChildrenApiController {
    private final ChildrenService childrenService;

    public ChildrenApiController(ChildrenService childrenService) {
        this.childrenService = childrenService;
    }

    @GetMapping
    public ResponseEntity<List<Children>> getAllChildren(@RequestParam Long userId) {
        List<Children> children = childrenService.findByUserId(userId);
        return ResponseEntity.ok(children);
    }

    @PostMapping
    public ResponseEntity<Children> createChildren(@RequestBody Children children) {
        Children savedChildren = childrenService.save(children);
        return ResponseEntity.ok(savedChildren);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Children> updateChildren(@PathVariable Long id, @RequestBody Children children) {
        try {
            System.out.println("Received data: " + children.toString());
            children.setId(id);
            Children updatedChildren = childrenService.updateChildren(children);
            return ResponseEntity.ok(updatedChildren);
        } catch (Exception e) {
            System.out.println("Error updating child: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChildren(@PathVariable Long id) {
        childrenService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Children> getChild(@PathVariable Long id) {
        Optional<Children> child = childrenService.findById(id);
        if (!child.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(child.get());
    }
}
