package com.rishavdas.blog.cms.service.impl;

import com.rishavdas.blog.cms.dto.GlobalSearchResultDTO;
import com.rishavdas.blog.cms.dto.PostSearchResultDTO;
import com.rishavdas.blog.cms.dto.UserSearchResultDTO;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.User;
import com.rishavdas.blog.cms.repository.PostRepository;
import com.rishavdas.blog.cms.repository.UserRepository;
import com.rishavdas.blog.cms.service.SearchService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public SearchServiceImpl(PostRepository postRepository, UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public GlobalSearchResultDTO globalSearch(String query, int limit) {
        if (query == null || query.trim().length() < 2) {
            return new GlobalSearchResultDTO(List.of(), List.of());
        }

        String normalizedQuery = query.trim().toLowerCase();
        int maxLimit = Math.min(Math.max(limit, 1), 20);

        String cacheKey = "search:query:" + normalizedQuery + (maxLimit == 5 ? "" : ":" + maxLimit);

        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof GlobalSearchResultDTO) {
                return (GlobalSearchResultDTO) cached;
            }
        } catch (Exception e) {
            // Redis error fallback to DB search
        }

        Pageable pageable = PageRequest.of(0, maxLimit);
        List<Post> posts = postRepository.searchPosts(normalizedQuery, pageable);
        List<User> users = userRepository.searchUsers(normalizedQuery, pageable);

        List<PostSearchResultDTO> postDTOs = posts.stream().map(post -> {
            String authorName = post.getAuthor() != null ? post.getAuthor().getUsername() : "Anonymous";
            String imageUrl = (post.getImages() != null && !post.getImages().isEmpty())
                    ? post.getImages().get(0).getImageUrl() : null;
            return new PostSearchResultDTO(
                    post.getId(),
                    post.getTitle(),
                    authorName,
                    post.getCreatedAt(),
                    imageUrl
            );
        }).toList();

        List<UserSearchResultDTO> userDTOs = users.stream().map(user ->
            new UserSearchResultDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getAvatarUrl()
            )
        ).toList();

        GlobalSearchResultDTO result = new GlobalSearchResultDTO(postDTOs, userDTOs);

        try {
            redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(5));
        } catch (Exception e) {
            // Redis error fallback
        }

        return result;
    }
}
