package com.rishavdas.blog.cms.service.impl;

import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.PostLike;
import com.rishavdas.blog.cms.model.User;
import com.rishavdas.blog.cms.repository.PostLikeRepository;
import com.rishavdas.blog.cms.repository.PostRepository;
import com.rishavdas.blog.cms.service.PostLikeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PostLikeServiceImpl implements PostLikeService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public PostLikeServiceImpl(PostRepository postRepository, PostLikeRepository postLikeRepository) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
    }

    @Override
    @Transactional
    public void likePost(Long postId, User user){
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new RuntimeException("Post not found"));
        if(postLikeRepository.existsByUserAndPost(user,post)){
            throw new RuntimeException("Already Liked");
        }
        PostLike like=new PostLike();
        like.setUser(user);
        like.setPost(post);
        postLikeRepository.save(like);
    }

    @Override
    public void unlikePost(Long postId, User user) {
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new RuntimeException("Post not found"));
        postLikeRepository.deleteByUserAndPost(user,post);
    }

    @Override
    public Long getLikeCount(Long postId) {
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new RuntimeException("Post not found"));
        return postLikeRepository.countByPost(post);
    }

    @Override
    public boolean hasUserLiked(Long postId, User user) {
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new RuntimeException("Post not found"));
        return postLikeRepository.existsByUserAndPost(user, post);
    }
}
