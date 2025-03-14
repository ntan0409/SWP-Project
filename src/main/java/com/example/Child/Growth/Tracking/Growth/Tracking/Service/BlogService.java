package com.example.Child.Growth.Tracking.Service;

import com.example.Child.Growth.Tracking.Model.BlogPost;
import com.example.Child.Growth.Tracking.Repository.BlogRepository;
import com.example.Child.Growth.Tracking.ulti.CateBlog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    @Autowired
    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    // Lấy danh sách tất cả blog
    public List<BlogPost> getAllBlogs() {
        return blogRepository.findAll();
    }

    // Tìm blog theo ID
    public Optional<BlogPost> getBlogById(Long id) {
        return blogRepository.findById(id);
    }

    // Lưu blog mới hoặc cập nhật blog
    public BlogPost saveBlog(BlogPost blogPost) {
        return blogRepository.save(blogPost);
    }

    // Xóa blog theo ID
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    public List<BlogPost> getBlogsByCategory(String category) {
        try {
            CateBlog cateEnum = CateBlog.valueOf(category.toUpperCase()); // Convert chuỗi sang enum
            return blogRepository.findByCategory(cateEnum);
        } catch (IllegalArgumentException e) {
            return List.of(); // Trả về danh sách rỗng nếu danh mục không hợp lệ
        }
    }

    public List<BlogPost> searchBlogsByTitle(String keyword) {
        return blogRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<String> getAllCategories() {
        return blogRepository.findAllCategories();
    }
}
