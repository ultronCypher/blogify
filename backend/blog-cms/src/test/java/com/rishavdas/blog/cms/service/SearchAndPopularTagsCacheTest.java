package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.dto.GlobalSearchResultDTO;
import com.rishavdas.blog.cms.dto.PostSearchResultDTO;
import com.rishavdas.blog.cms.dto.TagWithCountDTO;
import com.rishavdas.blog.cms.dto.UserSearchResultDTO;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.User;
import com.rishavdas.blog.cms.repository.PostRepository;
import com.rishavdas.blog.cms.repository.TagRepository;
import com.rishavdas.blog.cms.repository.UserRepository;
import com.rishavdas.blog.cms.service.impl.SearchServiceImpl;
import com.rishavdas.blog.cms.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchAndPopularTagsCacheTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private SearchServiceImpl searchService;
    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        searchService = new SearchServiceImpl(postRepository, userRepository, redisTemplate);
        tagService = new TagServiceImpl(tagRepository);
    }

    @Test
    void testSearchCacheMissExecutesQueryAndCachesWith5MinTTL() {
        String query = " Java ";
        String expectedKey = "search:query:java";

        when(valueOperations.get(expectedKey)).thenReturn(null);

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Java Post");
        post.setCreatedAt(LocalDateTime.now());

        User user = new User();
        user.setId(2L);
        user.setUsername("java_dev");

        when(postRepository.searchPosts(eq("java"), any(Pageable.class))).thenReturn(List.of(post));
        when(userRepository.searchUsers(eq("java"), any(Pageable.class))).thenReturn(List.of(user));

        GlobalSearchResultDTO result = searchService.globalSearch(query, 5);

        assertNotNull(result);
        assertEquals(1, result.getPosts().size());
        assertEquals("Java Post", result.getPosts().get(0).getTitle());
        assertEquals(1, result.getUsers().size());

        verify(valueOperations).set(eq(expectedKey), any(GlobalSearchResultDTO.class), eq(Duration.ofMinutes(5)));
    }

    @Test
    void testSearchCacheHitReturnsCachedResultWithoutDatabaseQuery() {
        String query = "JAVA";
        String expectedKey = "search:query:java";

        PostSearchResultDTO postDto = new PostSearchResultDTO(1L, "Cached Post", "Author", LocalDateTime.now(), null);
        UserSearchResultDTO userDto = new UserSearchResultDTO(2L, "CachedUser", null);
        GlobalSearchResultDTO cachedDto = new GlobalSearchResultDTO(List.of(postDto), List.of(userDto));

        when(valueOperations.get(expectedKey)).thenReturn(cachedDto);

        GlobalSearchResultDTO result = searchService.globalSearch(query, 5);

        assertNotNull(result);
        assertEquals(1, result.getPosts().size());
        assertEquals("Cached Post", result.getPosts().get(0).getTitle());

        verify(postRepository, never()).searchPosts(anyString(), any());
        verify(userRepository, never()).searchUsers(anyString(), any());
    }

    @Test
    void testPopularTagsServiceRetrieval() {
        TagWithCountDTO tag1 = new TagWithCountDTO(1L, "Java", "java", 10L);
        TagWithCountDTO tag2 = new TagWithCountDTO(2L, "React", "react", 5L);

        when(tagRepository.findPopularTags(PageRequest.of(0, 10))).thenReturn(List.of(tag1, tag2));

        List<TagWithCountDTO> popularTags = tagService.getPopularTags(10);

        assertNotNull(popularTags);
        assertEquals(2, popularTags.size());
        assertEquals("Java", popularTags.get(0).getName());
        assertEquals(10L, popularTags.get(0).getPostCount());
    }
}
