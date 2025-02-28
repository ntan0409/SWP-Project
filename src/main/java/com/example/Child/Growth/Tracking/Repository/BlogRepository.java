package com.example.Child.Growth.Tracking.Repository;

import com.example.Child.Growth.Tracking.Model.BlogPost;
import com.example.Child.Growth.Tracking.ulti.CateBlog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByAuthorId(Long authorId);

    List<BlogPost> findByCategory(CateBlog category);
    
    List<BlogPost> findByTitleContainingIgnoreCase(String keyword);

    @Query("SELECT DISTINCT b.category FROM BlogPost b")
    List<String> findAllCategories();
}
