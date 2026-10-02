package com.rishavdas.blog.cms.dto;

import java.time.LocalDateTime;

public class UserCommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long postId;
    private String postTitle;

    public UserCommentDTO() {
    }

    public UserCommentDTO(Long id, String content, LocalDateTime createdAt, Long postId, String postTitle) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.postId = postId;
        this.postTitle = postTitle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }
}
