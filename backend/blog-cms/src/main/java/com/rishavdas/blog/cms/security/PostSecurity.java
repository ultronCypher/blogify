package com.rishavdas.blog.cms.security;

import com.rishavdas.blog.cms.repository.PostRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("postSecurity")
public class PostSecurity {
    private final PostRepository postRepository;

    public PostSecurity(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public boolean isOwner(Long postId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }
        String username = auth.getName();
        return postRepository.findById(postId)
                .map(post -> post.getAuthor() != null && username.equals(post.getAuthor().getUsername()))
                .orElse(false);
    }
}
