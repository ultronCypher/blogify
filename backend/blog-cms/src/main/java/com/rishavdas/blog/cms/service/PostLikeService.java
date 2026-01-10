package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.User;
import jakarta.transaction.Transactional;

public interface PostLikeService {
    void likePost(Long postId, User user);
    void unlikePost(Long postId, User user);
    Long getLikeCount(Long postId);
    boolean hasUserLiked(Long postId, User user);
}
