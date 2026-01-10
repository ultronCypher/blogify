package com.rishavdas.blog.cms.service.impl;

import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.PostView;
import com.rishavdas.blog.cms.model.User;
import com.rishavdas.blog.cms.repository.PostRepository;
import com.rishavdas.blog.cms.repository.PostViewRepository;
import com.rishavdas.blog.cms.repository.UserRepository;
import com.rishavdas.blog.cms.service.PostViewService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PostViewServiceImpl implements PostViewService{
    private final PostViewRepository postViewRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostViewServiceImpl(PostViewRepository postViewRepository, PostRepository postRepository, UserRepository userRepository) {
        this.postViewRepository = postViewRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void recordView(Long postId, String username) {
        if(username==null){
            return;
        }
        Post post=postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user=userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean alreadyViewed =
                postViewRepository.existsByPostAndUser(post, user);
        if (alreadyViewed) return;

        PostView view = new PostView();
        view.setPost(post);
        view.setUser(user);
        postViewRepository.save(view);
    }

    @Override
    public Long getViewCount(Long postId) {
        return postViewRepository.countByPostId(postId);
    }
}
