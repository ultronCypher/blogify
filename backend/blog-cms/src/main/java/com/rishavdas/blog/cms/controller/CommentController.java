package com.rishavdas.blog.cms.controller;

import com.rishavdas.blog.cms.dto.CommentDTO;
import com.rishavdas.blog.cms.mapper.CommentMapper;
import com.rishavdas.blog.cms.model.Comment;
import com.rishavdas.blog.cms.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long postId,
            @RequestBody CommentDTO commentDTO,
            Authentication authentication
    ){
        String username=authentication.getName();
        Comment comment=commentService.createComment(commentDTO, postId, username);
        CommentDTO response = CommentMapper.toDTO(comment);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            Long commentId,
            @RequestBody CommentDTO commentDTO,
            Authentication authentication
    ){
        String username=authentication.getName();
        Comment comment=commentService.updateComment(commentId, commentDTO, username);
        return ResponseEntity.ok(CommentMapper.toDTO(comment));
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public void deleteComment(Long commentId, Authentication authentication){
        String username=authentication.getName();
        commentService.deleteComment(commentId, username);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable Long postId){
        List<Comment> comments=commentService.getCommentsByPostId(postId);
        List<CommentDTO> commentDTOs=comments.stream()
                .map(CommentMapper::toDTO)
                .toList();
        return ResponseEntity.ok(commentDTOs);
    }

    @GetMapping("/posts/{postId}/comments/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable Long postId) {
        Long count=commentService.getCommentCount(postId);
        return ResponseEntity.ok(count);
    }
}
