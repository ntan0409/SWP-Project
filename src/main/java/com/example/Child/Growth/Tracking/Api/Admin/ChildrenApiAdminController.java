package com.example.Child.Growth.Tracking.Api.Admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Child.Growth.Tracking.Model.Children;
import com.example.Child.Growth.Tracking.Service.ChildrenService;


@RestController
@RequestMapping("/api/admin/children")
public class ChildrenApiAdminController {
    private final ChildrenService childrenService;

    public ChildrenApiAdminController(ChildrenService childrenService) {
        this.childrenService = childrenService;
    }

    @GetMapping
    public ResponseEntity<List<Children>> getAllChildren() {
        List<Children> children = childrenService.findAll();
        return ResponseEntity.ok(children);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Children> deleteChild(@PathVariable Long id) {
        childrenService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
