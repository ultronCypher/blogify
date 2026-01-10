package com.rishavdas.blog.cms.service;

public interface PostViewService {
    void recordView(Long postId, String username);
    Long getViewCount(Long postId);
}
