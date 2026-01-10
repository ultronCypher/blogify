package com.rishavdas.blog.cms.dto;

public class PostLikeDTO {
    private Long id;
    private String title;
    private String authorUsername;
    private Long likesCount;

    public PostLikeDTO(){

    }
    public PostLikeDTO(Long id, String title, String authorUsername, Long likesCount) {
        this.id = id;
        this.title = title;
        this.authorUsername = authorUsername;
        this.likesCount = likesCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public Long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }
}
