package com.rishavdas.blog.cms.security;

import com.rishavdas.blog.cms.repository.PostRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("postSecurity")
public class PostSecurity {
    private final PostRepository postRepository;

    public PostSecurity(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public boolean isOwner(Long postId){
        String username= SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return postRepository.findById(postId)
                .map(post -> post.getAuthor().getUsername().equals(username))
                .orElse(false);
    }
}
