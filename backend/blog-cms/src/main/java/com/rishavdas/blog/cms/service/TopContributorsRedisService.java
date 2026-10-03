package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.dto.TopContributorDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class TopContributorsRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY = "top_contributors:top";
    private static final Duration CACHE_TTL = Duration.ofMinutes(60);

    public TopContributorsRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheTopContributors(List<TopContributorDTO> topContributors) {
        try {
            redisTemplate.opsForValue().set(
                    KEY,
                    topContributors,
                    CACHE_TTL
            );
        } catch (Exception e) {
            // Handle exception
        }
    }

    public void evictTopContributors() {
        try {
            redisTemplate.delete(KEY);
        } catch (Exception e) {
            // Handle exception
        }
    }
    public List<TopContributorDTO> getCachedTopContributors() {
        try {
            Object value = redisTemplate.opsForValue().get(KEY);
            if (value == null) {
                return null;
            }
            return (List<TopContributorDTO>) value;
        } catch (Exception e) {
            // Handle exception
            return null;
        }
    }
}
