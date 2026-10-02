package com.rishavdas.blog.cms.controller;

import com.rishavdas.blog.cms.dto.post_dtos.PostDTO;
import com.rishavdas.blog.cms.dto.PostLikeDTO;
import com.rishavdas.blog.cms.dto.PostSummaryDTO;
import com.rishavdas.blog.cms.dto.post_dtos.PostRequestDTO;
import com.rishavdas.blog.cms.dto.post_dtos.PostResponseDTO;
import com.rishavdas.blog.cms.mapper.PostMapper;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.TimeRange;
import com.rishavdas.blog.cms.service.PostService;
import com.rishavdas.blog.cms.service.PostViewRedisService;
import com.rishavdas.blog.cms.service.PostViewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

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
            @RequestPart(value = "tagIds", required = false) Set<Long> tagIds,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ){
        PostRequestDTO postRequestDTO = new PostRequestDTO();
        postRequestDTO.setTitle(title);
        postRequestDTO.setContent(content);
        postRequestDTO.setTagIds(tagIds);
        Post savedPost=postService.createPost(postRequestDTO,images);
        return ResponseEntity.ok(PostMapper.toDTO(savedPost));
    }

    @GetMapping
    public ResponseEntity<Page<PostSummaryDTO>> getPostSummaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ){
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        if (size > 50) size = 50;

        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.DESC;
        String property = sortParams[0];
        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        return ResponseEntity.ok(postService.getPostSummaries(pageable));
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

    @GetMapping("/contributors/top")
    public ResponseEntity<List<com.rishavdas.blog.cms.dto.TopContributorDTO>> getTopContributors(
            @RequestParam(defaultValue = "10") int limit
    ){
        return ResponseEntity.ok(postService.getTopContributors(limit));
    }
}
