package com.example.Child.Growth.Tracking.Api.User;

import com.example.Child.Growth.Tracking.Model.Children;
import com.example.Child.Growth.Tracking.Service.ChildrenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

@RestController
@RequestMapping("/api/children")
public class ChildrenApiController {
    private final ChildrenService childrenService;
    private final UserService userService;

    public ChildrenApiController(ChildrenService childrenService, UserService userService) {
        this.childrenService = childrenService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Children>> getAllChildren(@RequestParam Long userId) {
        List<Children> children = childrenService.findByUserId(userId);
        return ResponseEntity.ok(children);
    }

    @PostMapping
    public ResponseEntity<?> createChildren(@RequestBody Children children) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        if (user != null) {
            int count = childrenService.countByUserId(user.getId());
            String paymentStatus = user.getPaymentStatus();
            if (paymentStatus != null && paymentStatus.equals("paid")) {
                if (count <= 9) {
                    children.setUserId(user.getId());
                    Children savedChildren = childrenService.save(children);
                    return ResponseEntity.ok(savedChildren);
                }
                else {
                    return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User has reached the maximum number of children"));
                }
            }
            else {
                if (count <= 2) {
                    children.setUserId(user.getId());
                    Children savedChildren = childrenService.save(children);
                    return ResponseEntity.ok(savedChildren);
                }
                else {
                    return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User has not paid"));
                }
            }
        }
        
        return ResponseEntity.ok(null);
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
