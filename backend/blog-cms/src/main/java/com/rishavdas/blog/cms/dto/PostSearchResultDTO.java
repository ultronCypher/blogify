package com.rishavdas.blog.cms.dto;

import java.time.LocalDateTime;

public class PostSearchResultDTO {
    private Long id;
    private String title;
    private String authorName;
    private LocalDateTime createdAt;
    private String imageUrl;

    public PostSearchResultDTO() {}

    public PostSearchResultDTO(Long id, String title, String authorName, LocalDateTime createdAt, String imageUrl) {
        this.id = id;
        this.title = title;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
