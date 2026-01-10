package com.rishavdas.blog.cms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "author_id",nullable = false)
    private User author;

    @NotBlank(message = "The content can't be null")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Comment(){

    }
    public Comment(User user, Post post, String comment){
        this.author=user;
        this.post=post;
        this.content =comment;
    }

    public Long getId(){
        return id;
    }
    public User getAuthor(){
        return author;
    }
    public void setAuthor(User user){
        this.author=user;
    }
    public Post getPost(){
        return post;
    }
    public void setPost(Post post){
        this.post=post;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }
}
