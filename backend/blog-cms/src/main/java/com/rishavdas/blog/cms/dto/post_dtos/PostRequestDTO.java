package com.rishavdas.blog.cms.dto.post_dtos;

import java.util.List;
import java.util.Set;

public class PostRequestDTO {
    private String title;
    private String content;
    private Set<Long> tagIds;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds;
    }
}
