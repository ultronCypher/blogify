package com.rishavdas.blog.cms.controller;

import com.rishavdas.blog.cms.dto.PostDTO;
import com.rishavdas.blog.cms.dto.PostLikeDTO;
import com.rishavdas.blog.cms.dto.PostSummaryDTO;
import com.rishavdas.blog.cms.mapper.PostMapper;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.TimeRange;
import com.rishavdas.blog.cms.service.PostService;
import com.rishavdas.blog.cms.service.PostViewRedisService;
import com.rishavdas.blog.cms.service.PostViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final PostViewRedisService postViewRedisService;
    private final PostViewService postViewService;
    public PostController(PostService postService, PostViewRedisService postViewRedisService, PostViewService postViewService) {
        this.postService = postService;
        this.postViewRedisService = postViewRedisService;
        this.postViewService = postViewService;
    }

    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO> createPost(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ){
        PostDTO postDTO=new PostDTO();
        postDTO.setTitle(title);
        postDTO.setContent(content);
        Post savedPost=postService.createPost(postDTO,images);
        return ResponseEntity.ok(PostMapper.toDTO(savedPost));
    }

    @GetMapping
    public ResponseEntity<List<PostSummaryDTO>> getPostSummaries(){
        return ResponseEntity.ok(postService.getPostSummaries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(
            @PathVariable Long id,
            Authentication authentication
    ){
        postViewRedisService.incrementView(id);
        if(authentication!=null){
            postViewService.recordView(id,authentication.getName());
        }
        Post post=postService.getPostById(id);
        return ResponseEntity.ok(PostMapper.toDTO(post));
    }

    @PreAuthorize("@postSecurity.isOwner(#id) or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestBody PostDTO postDTO){
        Post updated=postService.updatePost(id,postDTO);
        return ResponseEntity.ok(PostMapper.toDTO(updated));
    }

    @PreAuthorize("@postSecurity.isOwner(#id) or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id){
        postService.deletePost(id);
    }

    @GetMapping("/by-title")
    public ResponseEntity<PostDTO> getPostByTitle(@RequestParam String title){
        Post requiredPost=postService.findPostByTitle(title);
        return ResponseEntity.ok(PostMapper.toDTO(requiredPost));
    }

    @GetMapping("/search")
    public ResponseEntity<PostDTO> getPostByTitleContaining(@RequestParam String keyword){
        Post requiredPost=postService.findPostByTitleContaining(keyword);
        return ResponseEntity.ok(PostMapper.toDTO(requiredPost));
    }

    @GetMapping("/latest")
    public List<Post> getLatestPosts(){
        return postService.getLatestPosts();
    }

    @GetMapping("/by-user/{userId}")
    public List<Post> getPostsByUser(@PathVariable Long userId){
        return  postService.findPostsByUsers(userId);
    }

    @GetMapping("/top-liked")
    public ResponseEntity<List<PostLikeDTO>> getTopLikedPosts(
            @RequestParam(defaultValue = "ALL_TIME") TimeRange range,
            @RequestParam(defaultValue = "10")int limit
    ){
        return ResponseEntity.ok(
                postService.getTopLikedPosts(range,limit)
        );
    }
}
