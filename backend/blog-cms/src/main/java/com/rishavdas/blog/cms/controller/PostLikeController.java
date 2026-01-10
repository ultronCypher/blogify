package com.rishavdas.blog.cms.controller;

import com.rishavdas.blog.cms.model.User;
import com.rishavdas.blog.cms.service.PostLikeService;
import com.rishavdas.blog.cms.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostLikeController {
    private final PostLikeService postLikeService;
    private final UserService userService;

    public PostLikeController(PostLikeService postLikeService, UserService userService) {
        this.postLikeService = postLikeService;
        this.userService = userService;
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
            ){
        User user=userService.getByUsername(userDetails.getUsername());
        postLikeService.likePost(postId,user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        User user=userService.getByUsername(userDetails.getUsername());
        postLikeService.unlikePost(postId,user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId){
        return ResponseEntity.ok(postLikeService.getLikeCount(postId));
    }

    @GetMapping("/{postId}/likes/me")
    public ResponseEntity<Boolean> hasLiked(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        User user=userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(postLikeService.hasUserLiked(postId, user));
    }
}
