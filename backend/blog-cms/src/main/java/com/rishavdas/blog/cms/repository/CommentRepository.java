package com.rishavdas.blog.cms.repository;

import com.rishavdas.blog.cms.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.rishavdas.blog.cms.dto.UserCommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findCommentsByPostId(Long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.id=:postId ORDER BY c.createdAt DESC")
    List<Comment> getLatestComments(@Param("postId") Long postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id=:postId")
    Long getCommentCount(@Param("postId") Long postId);

    @Query("SELECT c.post.id, COUNT(c) FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
    List<Object[]> countCommentsByPostIds(@Param("postIds") List<Long> postIds);
    
    void deleteByPostId(Long postId);

    @Query("SELECT new com.rishavdas.blog.cms.dto.UserCommentDTO(c.id, c.content, c.createdAt, c.post.id, c.post.title) " +
           "FROM Comment c WHERE c.author.id = :userId ORDER BY c.createdAt DESC")
    Page<UserCommentDTO> findCommentsByAuthorId(@Param("userId") Long userId, Pageable pageable);
}
