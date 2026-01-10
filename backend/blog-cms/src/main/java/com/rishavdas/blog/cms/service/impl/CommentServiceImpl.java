package com.rishavdas.blog.cms.service.impl;

import com.rishavdas.blog.cms.dto.CommentDTO;
import com.rishavdas.blog.cms.mapper.CommentMapper;
import com.rishavdas.blog.cms.model.Comment;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.User;
import com.rishavdas.blog.cms.repository.CommentRepository;
import com.rishavdas.blog.cms.repository.PostRepository;
import com.rishavdas.blog.cms.repository.UserRepository;
import com.rishavdas.blog.cms.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository){
        this.commentRepository=commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public Comment getCommentById(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(()->new RuntimeException("Comment not found"));
    }

    @Override
    public Comment createComment(CommentDTO commentDTO, Long postId, String username) {
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new RuntimeException("Post not found"));
        User author=userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("User not found"));
        Comment comment=CommentMapper.toEntity(commentDTO, author, post);
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long commentId, CommentDTO commentDTO, String username) {
        Comment existingComment=commentRepository.findById(commentId)
                .orElseThrow(()->new RuntimeException("Comment not found"));
        if(!existingComment.getAuthor().getUsername().equals(username)){
            throw new RuntimeException("You cant edit someone else's comment");
        }
        existingComment.setContent(commentDTO.getContent());
        return commentRepository.save(existingComment);
    }

    @Override
    public void deleteComment(Long commentId, String username) {
        Comment comment=commentRepository.findById(commentId)
                .orElseThrow(()->new RuntimeException("Comment not found"));
        if(!comment.getAuthor().getUsername().equals(username)){
            throw new RuntimeException("You are not allowed to delete the comment");
        }
        commentRepository.delete(comment);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public Long getCommentCount(Long postId) {
        return commentRepository.getCommentCount(postId);
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findCommentsByPostId(postId);
    }


}
