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

    private String dedupKey(Long postId, String userIdentifier){
        return "post:view_lock:" + postId + ":" + userIdentifier;
    }

//    public void incrementView(Long postId){
//        try {
//            redisTemplate.opsForValue().increment(key(postId));
//        } catch (Exception e) {
//        }
//    }

    public boolean incrementViewDeduplicated(Long postId, String userIdentifier){
        try {
            if (userIdentifier == null || userIdentifier.isBlank()) {
                userIdentifier = "anonymous";
            }
            String lockKey = dedupKey(postId, userIdentifier);
            Boolean isFirstVisit = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", java.time.Duration.ofMinutes(3));
            if (Boolean.TRUE.equals(isFirstVisit)) {
                redisTemplate.opsForValue().increment(key(postId));
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public Long getLiveViews(Long postId){
        try {
            Object value = redisTemplate.opsForValue().get(key(postId));
            if (value == null) return 0L;
            return ((Number) value).longValue();
        } catch (Exception e) {
            return 0L;
        }
    }
}
