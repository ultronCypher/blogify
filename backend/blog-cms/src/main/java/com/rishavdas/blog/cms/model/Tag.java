package com.rishavdas.blog.cms.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "tags",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_tags_name", columnNames = "name"),
                @UniqueConstraint(name="uk_tags_slug", columnNames = "slug")
        }
)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String slug;

    public Tag() {
    }

    public Tag(String name, String slug){
        this.name=name;
        this.slug=slug;
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
}
