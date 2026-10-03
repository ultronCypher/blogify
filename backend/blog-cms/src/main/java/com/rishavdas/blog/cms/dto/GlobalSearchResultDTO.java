package com.rishavdas.blog.cms.dto;

import java.util.List;

public class GlobalSearchResultDTO {
    private List<PostSearchResultDTO> posts;
    private List<UserSearchResultDTO> users;

    public GlobalSearchResultDTO() {}

    public GlobalSearchResultDTO(List<PostSearchResultDTO> posts, List<UserSearchResultDTO> users) {
        this.posts = posts;
        this.users = users;
    }

    public List<PostSearchResultDTO> getPosts() { return posts; }
    public void setPosts(List<PostSearchResultDTO> posts) { this.posts = posts; }
    public List<UserSearchResultDTO> getUsers() { return users; }
    public void setUsers(List<UserSearchResultDTO> users) { this.users = users; }
}
