package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.dto.PostSummaryDTO;
import com.rishavdas.blog.cms.mapper.PostMapper;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.Tag;
import com.rishavdas.blog.cms.model.TagFilterMode;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MultiTagFilteringTest {

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

    private Post postJavaSpring;
    private Post postJavaReact;

    @BeforeEach
    void setUp() {
        User author = new User();
        author.setId(1L);
        author.setUsername("testuser");

        Tag tagJava = new Tag();
        tagJava.setId(1L);
        tagJava.setName("Java");
        tagJava.setSlug("java");

        Tag tagSpring = new Tag();
        tagSpring.setId(2L);
        tagSpring.setName("Spring Boot");
        tagSpring.setSlug("spring-boot");

        Tag tagReact = new Tag();
        tagReact.setId(3L);
        tagReact.setName("React");
        tagReact.setSlug("react");

        postJavaSpring = new Post();
        postJavaSpring.setId(101L);
        postJavaSpring.setTitle("Java & Spring Boot Guide");
        postJavaSpring.setContent("Content");
        postJavaSpring.setAuthor(author);
        postJavaSpring.setTags(Set.of(tagJava, tagSpring));
        postJavaSpring.setCreatedAt(LocalDateTime.now());

        postJavaReact = new Post();
        postJavaReact.setId(102L);
        postJavaReact.setTitle("Java & React Fullstack");
        postJavaReact.setContent("Content");
        postJavaReact.setAuthor(author);
        postJavaReact.setTags(Set.of(tagJava, tagReact));
        postJavaReact.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testSingleTagFiltering() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = List.of(postJavaSpring, postJavaReact);
        Page<Post> page = new PageImpl<>(posts, pageable, posts.size());

        when(postRepository.findByAnyTagSlugs(eq(List.of("java")), eq(pageable))).thenReturn(page);
        when(postLikeRepository.countLikesByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(commentRepository.countCommentsByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(postMapper.toSummary(any(), any(), any(), any())).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            PostSummaryDTO dto = new PostSummaryDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            return dto;
        });

        Page<PostSummaryDTO> result = postService.getPostsByTags(List.of("java"), TagFilterMode.ALL, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(postRepository).findByAnyTagSlugs(eq(List.of("java")), eq(pageable));
    }

    @Test
    void testMultiTagFilteringModeALL() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = List.of(postJavaSpring);
        Page<Post> page = new PageImpl<>(posts, pageable, posts.size());

        when(postRepository.findByAllTagSlugs(eq(List.of("java", "spring-boot")), eq(2L), eq(pageable))).thenReturn(page);
        when(postLikeRepository.countLikesByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(commentRepository.countCommentsByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(postMapper.toSummary(any(), any(), any(), any())).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            PostSummaryDTO dto = new PostSummaryDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            return dto;
        });

        Page<PostSummaryDTO> result = postService.getPostsByTags(List.of("Java", "spring-boot"), TagFilterMode.ALL, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(101L, result.getContent().get(0).getId());
        verify(postRepository).findByAllTagSlugs(eq(List.of("java", "spring-boot")), eq(2L), eq(pageable));
    }

    @Test
    void testMultiTagFilteringModeANY() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = List.of(postJavaSpring, postJavaReact);
        Page<Post> page = new PageImpl<>(posts, pageable, posts.size());

        when(postRepository.findByAnyTagSlugs(eq(List.of("spring-boot", "react")), eq(pageable))).thenReturn(page);
        when(postLikeRepository.countLikesByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(commentRepository.countCommentsByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(postMapper.toSummary(any(), any(), any(), any())).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            PostSummaryDTO dto = new PostSummaryDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            return dto;
        });

        Page<PostSummaryDTO> result = postService.getPostsByTags(List.of(" spring-boot ", "REACT"), TagFilterMode.ANY, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(postRepository).findByAnyTagSlugs(eq(List.of("spring-boot", "react")), eq(pageable));
    }

    @Test
    void testEmptyTagsFallbackToPostSummaries() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = List.of(postJavaSpring);
        Page<Post> page = new PageImpl<>(posts, pageable, posts.size());

        when(postRepository.findAll(pageable)).thenReturn(page);
        when(postLikeRepository.countLikesByPostIds(anyList())).thenReturn(Collections.emptyList());
        when(commentRepository.countCommentsByPostIds(anyList())).thenReturn(Collections.emptyList());

        postService.getPostsByTags(List.of("   "), TagFilterMode.ALL, pageable);

        verify(postRepository).findAll(pageable);
        verify(postRepository, never()).findByAllTagSlugs(any(), anyLong(), any());
        verify(postRepository, never()).findByAnyTagSlugs(any(), any());
    }
}
