package com.rishavdas.blog.cms.repository;

import com.rishavdas.blog.cms.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findCommentsByPostId(Long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.id=:postId ORDER BY c.createdAt DESC")
    List<Comment> getLatestComments(@Param("postId") Long postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id=:postId")
    Long getCommentCount(@Param("postId") Long postId);
}
