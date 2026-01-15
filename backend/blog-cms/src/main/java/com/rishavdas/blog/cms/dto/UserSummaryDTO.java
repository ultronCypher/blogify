package com.rishavdas.blog.cms.dto;

import com.rishavdas.blog.cms.model.User;

import java.util.List;

public class UserSummaryDTO {
    private Long id;
    private String username;
    private String role;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    private String avatarUrl;
    private List<PostSummaryDTO> posts;

    public List<PostSummaryDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostSummaryDTO> posts) {
        this.posts = posts;
    }

    public static UserSummaryDTO from(User user){
        UserSummaryDTO dto=new UserSummaryDTO();
        dto.id=user.getId();
        dto.username=user.getUsername();
        dto.role=user.getRole().name();
        dto.avatarUrl= user.getAvatarUrl();
        return dto;
    }
    public Long getId(){
        return id;
    }
    public String getUsername(){
        return username;
    }
    public String getRole(){
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
