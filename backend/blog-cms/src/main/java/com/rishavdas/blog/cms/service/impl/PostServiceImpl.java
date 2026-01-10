package com.rishavdas.blog.cms.service.impl;

import com.rishavdas.blog.cms.dto.PostDTO;
import com.rishavdas.blog.cms.dto.PostLikeDTO;
import com.rishavdas.blog.cms.dto.PostSummaryDTO;
import com.rishavdas.blog.cms.mapper.PostMapper;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.PostImage;
import com.rishavdas.blog.cms.model.TimeRange;
import com.rishavdas.blog.cms.model.User;
import com.rishavdas.blog.cms.repository.CommentRepository;
import com.rishavdas.blog.cms.repository.PostLikeRepository;
import com.rishavdas.blog.cms.repository.PostRepository;
import com.rishavdas.blog.cms.repository.UserRepository;
import com.rishavdas.blog.cms.service.PostImageService;
import com.rishavdas.blog.cms.service.PostService;
import com.rishavdas.blog.cms.service.PostViewRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostViewRedisService postViewRedisService;
    private final PostMapper postMapper;
    private final PostImageService postImageService;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository, PostLikeRepository postLikeRepository, PostViewRedisService postViewRedisService, PostMapper postMapper, PostImageService postImageService){
        this.postRepository=postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
        this.postViewRedisService = postViewRedisService;
        this.postMapper = postMapper;
        this.postImageService = postImageService;
    }

    @Override
    public Post createPost(PostDTO postDTO, List<MultipartFile>images) {
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        User author=userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user not found"));
        Post post=new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setAuthor(author);
        postRepository.save(post);
        List<PostImage>uploadedImages=postImageService.uploadImages(images,post);
        if (images != null && !images.isEmpty()) {
            uploadedImages = postImageService.uploadImages(images, post);
            post.getImages().addAll(uploadedImages);
        }
        return postRepository.save(post);
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post updatePost(Long id, PostDTO postDTO) {
        Post existingPost=postRepository.findById(id).orElse(null);
        if(existingPost == null){
            return  null;
        }
        existingPost.setTitle(postDTO.getTitle());
        existingPost.setContent(postDTO.getContent());
        return postRepository.save(existingPost);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public Post findPostByTitle(String title){
        return postRepository.findPostByTitle(title);
    }

    @Override
    public Post findPostByTitleContaining(String title){
        return postRepository.findPostByTitleContaining(title);
    }

    @Override
    public List<Post>getLatestPosts(){
        return postRepository.getLatestPosts();
    }

    @Override
    public List<Post> findPostsByUsers(Long userId){
        return postRepository.findPostsByUsers(userId);
    }

    @Override
    public List<PostSummaryDTO> getPostSummaries() {
        List<Post>posts=postRepository.findAll();
        return posts.stream().map(post -> {
            Long likes=postLikeRepository.countByPost(post);
            Long comments = commentRepository.getCommentCount(post.getId());
            Long views = postViewRedisService.getLiveViews(post.getId());
            return postMapper.toSummary(post,likes,views,comments);
        }).toList();
    }

    private LocalDateTime getStartDate(TimeRange range){
        LocalDateTime now=LocalDateTime.now();
        return switch (range){
            case DAILY -> now.minusDays(1);
            case WEEKLY -> now.minusWeeks(1);
            case MONTHLY -> now.minusMonths(1);
            case YEARLY -> now.minusYears(1);
            case ALL_TIME -> null;
        };
    }

    @Override
    public List<PostLikeDTO> getTopLikedPosts(TimeRange range,int limit) {
        LocalDateTime startDate=getStartDate(range);
        Pageable pageable= PageRequest.of(0,limit);
        return postLikeRepository.findTopLikedPosts(startDate, pageable);
    }

    @Override
    public Page<PostSummaryDTO> getPostsByUser(Long userId, int page, int size) {
        Pageable pageable=PageRequest.of(page,size);
        Page<Post>posts=postRepository.findByUserId(userId,pageable);
        return posts.map(post -> {
            Long likes=postLikeRepository.countByPost(post);
            Long comments=commentRepository.getCommentCount(post.getId());
            Long views=postViewRedisService.getLiveViews(post.getId());
            return postMapper.toSummary(post,likes,views,comments);
        });
    }
}
