package com.example.demo1111;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    @Query("SELECT b FROM BlogPost b WHERE " +
            "(:criteria = 'date' AND CAST(b.createdAt as LocalDate) = :queryDate) OR " +
            "(:criteria = 'title' AND b.title LIKE CONCAT('%', :query, '%') AND :queryDate IS NULL) OR " +
            "(:criteria = 'content' AND b.content LIKE CONCAT('%', :query, '%') AND :queryDate IS NULL) OR " +
            "(:criteria = 'dateAndContent' AND CAST(b.createdAt as LocalDate) = :queryDate AND b.content LIKE CONCAT('%', :query, '%')) OR " +
            "(:criteria = 'dateAndTitle' AND CAST(b.createdAt as LocalDate) = :queryDate AND b.title LIKE CONCAT('%', :query, '%')) OR " +
            "(:criteria = 'all' AND CAST(b.createdAt as LocalDate) = :queryDate " +
            "AND b.title LIKE CONCAT('%', :title, '%') " +
            "AND b.content LIKE CONCAT('%', :content, '%'))")
    List<BlogPost> searchBlogPosts(@Param("query") String query,
                                   @Param("queryDate") LocalDate queryDate,
                                   @Param("criteria") String criteria,
                                   @Param("title") String title,
                                   @Param("content") String content);
}


