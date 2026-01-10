package com.rishavdas.blog.cms.repository;

import com.rishavdas.blog.cms.model.Post;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    Post findPostByTitle(String title);
    Post findPostByTitleContaining(String title);

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> getLatestPosts();

    @Query("SELECT p FROM Post p WHERE p.author.id=:userId")
    List<Post> findPostsByUsers(@Param("userId") Long userId);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:text% OR p.content LIKE %:text%")
    List<Post> searchPostsByKeyword(@Param("text") String text);

    Page<Post> findByUserId(Long userId, Pageable pageable);

}
