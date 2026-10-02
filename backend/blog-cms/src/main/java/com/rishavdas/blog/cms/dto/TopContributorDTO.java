package com.rishavdas.blog.cms.dto;

public class TopContributorDTO {
    private Long userId;
    private String username;
    private String avatarUrl;
    private Long postCount;

    public TopContributorDTO() {
    }

    public TopContributorDTO(Long userId, String username, String avatarUrl, Long postCount) {
        this.userId = userId;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.postCount = postCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }
}
