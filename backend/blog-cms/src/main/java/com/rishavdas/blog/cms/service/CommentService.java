package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.dto.CommentDTO;
import com.rishavdas.blog.cms.model.Comment;

import java.util.List;

import com.rishavdas.blog.cms.dto.UserCommentDTO;
import org.springframework.data.domain.Page;

public interface CommentService {
    Comment getCommentById(Long commentId);
    Comment createComment(CommentDTO commentDTO, Long postId, String username);
    Comment updateComment(Long commentId, CommentDTO commentDTO, String username);
    void deleteComment(Long commentId, String username);
    List<Comment> getCommentsByPostId(Long postId);
    List<Comment> getAllComments();
    Long getCommentCount(Long postId);
    Page<UserCommentDTO> getUserComments(Long userId, int page, int size);
}
