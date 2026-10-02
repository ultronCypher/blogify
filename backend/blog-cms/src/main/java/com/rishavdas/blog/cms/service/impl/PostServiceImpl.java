package com.rishavdas.blog.cms.service.impl;

import com.rishavdas.blog.cms.dto.post_dtos.PostDTO;
import com.rishavdas.blog.cms.dto.PostLikeDTO;
import com.rishavdas.blog.cms.dto.PostSummaryDTO;
import com.rishavdas.blog.cms.dto.post_dtos.PostRequestDTO;
import com.rishavdas.blog.cms.mapper.PostMapper;
import com.rishavdas.blog.cms.model.*;
import com.rishavdas.blog.cms.repository.*;
import com.rishavdas.blog.cms.service.PostImageService;
import com.rishavdas.blog.cms.service.PostService;
import com.rishavdas.blog.cms.service.PostViewRedisService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final TagRepository tagRepository;
    private final PostViewRedisService postViewRedisService;
    private final PostMapper postMapper;
    private final PostImageService postImageService;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository, PostLikeRepository postLikeRepository, TagRepository tagRepository, PostViewRedisService postViewRedisService, PostMapper postMapper, PostImageService postImageService){
        this.postRepository=postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
        this.tagRepository = tagRepository;
        this.postViewRedisService = postViewRedisService;
        this.postMapper = postMapper;
        this.postImageService = postImageService;
    }

    @Override
    public Post createPost(PostRequestDTO postRequestDTO, List<MultipartFile>images) {
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        User author=userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user not found"));
        Set<Long> tagIds = postRequestDTO.getTagIds();
        if(tagIds!=null && !tagIds.isEmpty() && tagIds.size()>5){
            throw new IllegalArgumentException("A post can have a maximum of 5 tags.");
        }
        Post post=new Post();
        post.setTitle(postRequestDTO.getTitle());
        post.setContent(postRequestDTO.getContent());
        post.setAuthor(author);
        if(tagIds!=null && !tagIds.isEmpty()){
            List<Tag> tags = tagRepository.findAllById(tagIds);
            post.setTags(new HashSet<>(tags));
        }
        postRepository.save(post);
        if (images != null && !images.isEmpty()) {
            List<PostImage>uploadedImages = postImageService.uploadImages(images, post);
            post.getImages().addAll(uploadedImages);
        }
        return postRepository.save(post);
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findByIdWithDetails(id).orElse(null);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post updatePost(Long id, PostDTO postDTO) {
        Post existingPost = postRepository.findById(id).orElse(null);
        if (existingPost == null) {
            return null;
        }
        existingPost.setTitle(postDTO.getTitle());
        existingPost.setContent(postDTO.getContent());
        if (postDTO.getTagIds() != null) {
            if (postDTO.getTagIds().size() > 5) {
                throw new IllegalArgumentException("A post can have a maximum of 5 tags.");
            }
            List<Tag> tags = tagRepository.findAllById(postDTO.getTagIds());
            existingPost.setTags(new HashSet<>(tags));
        }
        return postRepository.save(existingPost);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deletePost(Long id) {
        commentRepository.deleteByPostId(id);
        postLikeRepository.deleteByPostId(id);
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
        List<Post> posts = postRepository.findAllWithDetails();
        if (posts.isEmpty()) return List.of();

        List<Long> postIds = posts.stream().map(Post::getId).toList();

        Map<Long, Long> likesMap = postLikeRepository.countLikesByPostIds(postIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        Map<Long, Long> commentsMap = commentRepository.countCommentsByPostIds(postIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        return posts.stream().map(post -> {
            Long likes = likesMap.getOrDefault(post.getId(), 0L);
            Long comments = commentsMap.getOrDefault(post.getId(), 0L);
            Long views = postViewRedisService.getLiveViews(post.getId());
            return postMapper.toSummary(post, likes, views, comments);
        }).toList();
    }

    @Override
    public Page<PostSummaryDTO> getPostSummaries(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        if (posts.isEmpty()) {
            return posts.map(post -> null);
        }

        List<Long> postIds = posts.stream().map(Post::getId).toList();

        Map<Long, Long> likesMap = postLikeRepository.countLikesByPostIds(postIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        Map<Long, Long> commentsMap = commentRepository.countCommentsByPostIds(postIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        return posts.map(post -> {
            Long likes = likesMap.getOrDefault(post.getId(), 0L);
            Long comments = commentsMap.getOrDefault(post.getId(), 0L);
            Long views = postViewRedisService.getLiveViews(post.getId());
            return postMapper.toSummary(post, likes, views, comments);
        });
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findByAuthor_Id(userId, pageable);
        if (posts.isEmpty()) {
            return posts.map(post -> null);
        }

        List<Long> postIds = posts.stream().map(Post::getId).toList();

        Map<Long, Long> likesMap = postLikeRepository.countLikesByPostIds(postIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        Map<Long, Long> commentsMap = commentRepository.countCommentsByPostIds(postIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        return posts.map(post -> {
            Long likes = likesMap.getOrDefault(post.getId(), 0L);
            Long comments = commentsMap.getOrDefault(post.getId(), 0L);
            Long views = postViewRedisService.getLiveViews(post.getId());
            return postMapper.toSummary(post, likes, views, comments);
        });
    }

    @Override
    public List<com.rishavdas.blog.cms.dto.TopContributorDTO> getTopContributors(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return postRepository.findTopContributors(pageable);
    }
}
