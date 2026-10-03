package com.rishavdas.blog.cms.dto;

public class TagWithCountDTO {
    private Long id;
    private String name;
    private String slug;
    private Long postCount;

    public TagWithCountDTO() {}

    public TagWithCountDTO(Long id, String name, String slug, Long postCount) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.postCount = postCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }
}
