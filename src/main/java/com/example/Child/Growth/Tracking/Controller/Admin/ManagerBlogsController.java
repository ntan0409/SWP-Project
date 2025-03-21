package com.example.Child.Growth.Tracking.Controller.Admin;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Child.Growth.Tracking.Model.BlogPost;
import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Service.BlogService;
import com.example.Child.Growth.Tracking.Service.UserService;
import com.example.Child.Growth.Tracking.ulti.CateBlog;

@Controller
@RequestMapping("/manageBlogs")
public class ManagerBlogsController {

    private final BlogService blogService;
    private final UserService userService;

    public ManagerBlogsController(BlogService blogService, UserService userService) {
        this.blogService = blogService;
        this.userService = userService;
    }

    @GetMapping
    public String manageBlogs(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            userService.findByUsername(auth.getName())
                    .ifPresent(user -> model.addAttribute("user", user));
        }
        return "admin/manageBlogs";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();  // Lấy username của người dùng
            
            User user = userService.findByUsername(username).orElse(null);  // Trả về user nếu tìm thấy, nếu không trả về null

            // Thêm đối tượng user vào model
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
        BlogPost blog = blogService.getBlogById(id).orElseThrow(() -> new RuntimeException("Blog not found"));

        model.addAttribute("blog", blog);
        model.addAttribute("categories", CateBlog.values());
        return "admin/editBlog";
    }


    @GetMapping("/create")
    public String showCreateBlogForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
    
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
    
        if (user == null) {
            return "redirect:/manageBlogs";  // Nếu không tìm thấy user, quay về danh sách blog
        }
    
        BlogPost blog = new BlogPost();
        blog.setAuthor(user);  // Đặt user đang đăng nhập làm tác giả

        model.addAttribute("user", user);
        model.addAttribute("blog", blog);
        model.addAttribute("categories", CateBlog.values());
        return "admin/createBlog";
    }

}
