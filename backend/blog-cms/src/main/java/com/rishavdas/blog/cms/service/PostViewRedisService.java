package com.rishavdas.blog.cms.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostViewRedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public PostViewRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(Long postId){
        return "post:views:"+postId;
    }

    public void incrementView(Long postId){
        redisTemplate.opsForValue().increment(key(postId));
    }

    public Long getLiveViews(Long postId){
        Object value = redisTemplate.opsForValue().get(key(postId));
        if (value == null) return 0L;
        return ((Number) value).longValue();
    }
}
