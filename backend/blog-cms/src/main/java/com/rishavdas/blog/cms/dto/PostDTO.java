package com.rishavdas.blog.cms.dto;

import java.util.List;

public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private UserDTO author;
    private List<String> images;
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }
    public UserDTO getAuthor(){
        return author;
    }
    public void setAuthor(UserDTO author){
        this.author=author;
    }
    public List<String> getImages() {
        return images;
    }
    public void setImages(List<String> images) {
        this.images = images;
    }
}
