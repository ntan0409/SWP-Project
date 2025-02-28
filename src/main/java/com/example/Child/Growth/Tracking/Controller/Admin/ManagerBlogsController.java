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
        model.addAttribute("page", "manageBlogs");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();  // Lấy username của người dùng

            // Giả sử bạn có một phương thức để lấy thông tin người dùng từ username
            User user = userService.findByUsername(username).orElse(null);  // Trả về user nếu tìm thấy, nếu không trả về null

            // Thêm đối tượng user vào model
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
        
        List<BlogPost> blogs = blogService.getAllBlogs();
        model.addAttribute("blogs", blogs);
        return "admin/manageBlogs";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();  // Lấy username của người dùng

            // Giả sử bạn có một phương thức để lấy thông tin người dùng từ username
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

    @PostMapping("/update")
    public String updateBlog(@ModelAttribute BlogPost blog, RedirectAttributes redirectAttributes) {
        try {
            blogService.saveBlog(blog);
            redirectAttributes.addFlashAttribute("successMessage", "Blog updated successfully!");
            return "redirect:/manageBlogs";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating blog: " + e.getMessage());
            return "redirect:/manageBlogs";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return "redirect:/manageBlogs";
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

    @PostMapping("/create")
    public String createBlog(@ModelAttribute BlogPost blog, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();  // Lấy username của người dùng

            // Giả sử bạn có một phương thức để lấy thông tin người dùng từ username
            User user = userService.findByUsername(username).orElse(null);  // Trả về user nếu tìm thấy, nếu không trả về null

            // Thêm đối tượng user vào model
            if (user != null) {
                blog.setAuthor(user);  // Đảm bảo có author trước khi lưu
                model.addAttribute("user", user);
            }
        }

        blogService.saveBlog(blog);
        return "redirect:/manageBlogs";
    }
}
