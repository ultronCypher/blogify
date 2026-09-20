package com.rishavdas.blog.cms.controller;

import com.rishavdas.blog.cms.dto.PostSummaryDTO;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/posts")
public class UserPostController {
    private final PostService postService;

    public UserPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<Page<PostSummaryDTO>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(postService.getPostsByUser(userId,page,size));
    }
}
