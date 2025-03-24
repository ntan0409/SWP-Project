package com.example.Child.Growth.Tracking.Api.Admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.Child.Growth.Tracking.Model.BlogPost;
import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Service.BlogService;
import com.example.Child.Growth.Tracking.Service.UserService;

@RestController
@RequestMapping("/api/blogs")
public class BlogApiController {
    private final BlogService blogService;
    private final UserService userService;

    public BlogApiController(BlogService blogService, UserService userService) {
        this.blogService = blogService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<BlogPost>> getAllBlogs(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<BlogPost> blogs;
        if (category != null && !category.isEmpty()) {
            blogs = blogService.getBlogsByCategory(category);
        } else if (keyword != null && !keyword.isEmpty()) {
            blogs = blogService.searchBlogsByTitle(keyword);
        } else {
            blogs = blogService.getAllBlogs();
        }
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> getBlog(@PathVariable Long id) {
        return blogService.getBlogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BlogPost> createBlog(@RequestBody BlogPost blog) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        blog.setAuthor(user);
        BlogPost savedBlog = blogService.saveBlog(blog);
        return ResponseEntity.ok(savedBlog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogPost> updateBlog(@PathVariable Long id, @RequestBody BlogPost blog) {
        return blogService.getBlogById(id)
                .map(existingBlog -> {
                    blog.setId(id);
                    if (blog.getCreatedAt() == null) {
                        blog.setCreatedAt(existingBlog.getCreatedAt());
                    }
                    BlogPost updatedBlog = blogService.saveBlog(blog);
                    return ResponseEntity.ok(updatedBlog);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        if (!blogService.getBlogById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        blogService.deleteBlog(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(blogService.getAllCategories());
    }
}
