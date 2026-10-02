package com.rishavdas.blog.cms.dto;

public class UserProfileStatsDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String avatarUrl;
    private Long totalPosts;
    private Long totalLikes;
    private Long totalViews;

    public UserProfileStatsDTO() {
    }

    public UserProfileStatsDTO(Long id, String username, String email, String role, String avatarUrl, Long totalPosts, Long totalLikes, Long totalViews) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.avatarUrl = avatarUrl;
        this.totalPosts = totalPosts;
        this.totalLikes = totalLikes;
        this.totalViews = totalViews;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Long getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(Long totalPosts) {
        this.totalPosts = totalPosts;
    }

    public Long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Long getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(Long totalViews) {
        this.totalViews = totalViews;
    }
}
