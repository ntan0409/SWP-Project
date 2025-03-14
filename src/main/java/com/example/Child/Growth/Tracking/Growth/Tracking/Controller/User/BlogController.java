package com.example.Child.Growth.Tracking.Controller.User;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Child.Growth.Tracking.Model.BlogPost;
import com.example.Child.Growth.Tracking.Service.BlogService;

@Controller
@RequestMapping("/blog")
public class BlogController {
    
    @Autowired
    private BlogService blogService;

    @GetMapping
    public String listBlogs(Model model, @RequestParam(value = "category", required = false) String category,
                            @RequestParam(value = "keyword", required = false) String keyword) {
        List<BlogPost> blogs;

        if (category != null && !category.isEmpty()) {
            blogs = blogService.getBlogsByCategory(category);
        } else if (keyword != null && !keyword.isEmpty()) {
            blogs = blogService.searchBlogsByTitle(keyword);
        } else {
            blogs = blogService.getAllBlogs();
        }

        model.addAttribute("page", "blog");
        model.addAttribute("blogs", blogs);
        model.addAttribute("categories", blogService.getAllCategories()); // Load danh mục từ Enum
        return "user/blog/blog";
    }

    @GetMapping("/{id}")
    public String getBlogById(@PathVariable Long id, Model model) {
        Optional<BlogPost> blogOptional = blogService.getBlogById(id);
        
        if (blogOptional.isEmpty()) { // Kiểm tra blog có tồn tại không
            return "redirect:/blogs"; // Không có thì quay về danh sách
        }
        
        model.addAttribute("page", "blog");
        model.addAttribute("blog", blogOptional.get()); // Lấy giá trị thực
        return "user/blog/blog_detail"; // Trả về template
    }
    
}
