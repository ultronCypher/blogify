package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.dto.PostSummaryDTO;
import com.rishavdas.blog.cms.mapper.PostMapper;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.User;
import com.rishavdas.blog.cms.repository.CommentRepository;
import com.rishavdas.blog.cms.repository.PostLikeRepository;
import com.rishavdas.blog.cms.repository.PostRepository;
import com.rishavdas.blog.cms.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServicePaginationTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostViewRedisService postViewRedisService;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private List<Post> allPosts;

    @BeforeEach
    void setUp() {
        allPosts = new ArrayList<>();
        User author = new User();
        author.setId(1L);
        author.setUsername("testuser");

        for (long i = 1; i <= 5; i++) {
            Post post = new Post();
            post.setId(i);
            post.setTitle("Post Title " + i);
            post.setContent("Content " + i);
            post.setAuthor(author);
            post.setCreatedAt(LocalDateTime.now().minusDays(i));
            allPosts.add(post);
        }
    }

    private Page<Post> getPagedPosts(int page, int size) {
        int fromIndex = page * size;
        if (fromIndex >= allPosts.size()) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), allPosts.size());
        }
        int toIndex = Math.min(fromIndex + size, allPosts.size());
        List<Post> subList = allPosts.subList(fromIndex, toIndex);
        return new PageImpl<>(subList, PageRequest.of(page, size), allPosts.size());
    }

    @Test
    void testFirstPage() {
        int page = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        when(postRepository.findAll(pageable)).thenReturn(getPagedPosts(page, size));
        when(postLikeRepository.countLikesByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(commentRepository.countCommentsByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(postMapper.toSummary(any(), any(), any(), any())).thenAnswer(invocation -> {
            Post p = invocation.getArgument(0);
            PostSummaryDTO dto = new PostSummaryDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            return dto;
        });

        Page<PostSummaryDTO> result = postService.getPostSummaries(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getNumber());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertTrue(result.isFirst());
        assertFalse(result.isLast());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());
    }

    @Test
    void testMiddlePage() {
        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        when(postRepository.findAll(pageable)).thenReturn(getPagedPosts(page, size));
        when(postLikeRepository.countLikesByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(commentRepository.countCommentsByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(postMapper.toSummary(any(), any(), any(), any())).thenAnswer(invocation -> {
            Post p = invocation.getArgument(0);
            PostSummaryDTO dto = new PostSummaryDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            return dto;
        });

        Page<PostSummaryDTO> result = postService.getPostSummaries(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getNumber());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertFalse(result.isFirst());
        assertFalse(result.isLast());
        assertEquals(3L, result.getContent().get(0).getId());
        assertEquals(4L, result.getContent().get(1).getId());
    }

    @Test
    void testLastPage() {
        int page = 2;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        when(postRepository.findAll(pageable)).thenReturn(getPagedPosts(page, size));
        when(postLikeRepository.countLikesByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(commentRepository.countCommentsByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(postMapper.toSummary(any(), any(), any(), any())).thenAnswer(invocation -> {
            Post p = invocation.getArgument(0);
            PostSummaryDTO dto = new PostSummaryDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            return dto;
        });

        Page<PostSummaryDTO> result = postService.getPostSummaries(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getNumber());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertFalse(result.isFirst());
        assertTrue(result.isLast());
        assertEquals(5L, result.getContent().get(0).getId());
    }

    @Test
    void testPageBeyondAvailableData() {
        int page = 10;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        when(postRepository.findAll(pageable)).thenReturn(getPagedPosts(page, size));

        Page<PostSummaryDTO> result = postService.getPostSummaries(pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(10, result.getNumber());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
    }
}
