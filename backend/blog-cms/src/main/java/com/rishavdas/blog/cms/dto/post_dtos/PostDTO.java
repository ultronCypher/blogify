package com.rishavdas.blog.cms.dto.post_dtos;

import com.rishavdas.blog.cms.dto.TagDTO;
import com.rishavdas.blog.cms.dto.UserDTO;

import java.util.List;
import java.util.Set;

public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private UserDTO author;
    private List<String> images;
    private List<TagDTO> tags;
    private Set<Long> tagIds;

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
    public List<TagDTO> getTags() {
        return tags;
    }
    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }
    public Set<Long> getTagIds() {
        return tagIds;
    }
    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds;
    }
}
