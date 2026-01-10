package com.rishavdas.blog.cms.mapper;

import com.rishavdas.blog.cms.dto.CommentDTO;
import com.rishavdas.blog.cms.model.Comment;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.User;

public class CommentMapper {
    public static CommentDTO toDTO(Comment comment){
        CommentDTO dto=new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthorUsername(comment.getAuthor().getUsername());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
    public static Comment toEntity(CommentDTO commentDTO, User author, Post post){
        Comment comment=new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setAuthor(author);
        comment.setPost(post);
        return comment;
    }
}
