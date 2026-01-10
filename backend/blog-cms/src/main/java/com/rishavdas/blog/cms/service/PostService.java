package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.dto.PostDTO;
import com.rishavdas.blog.cms.dto.PostLikeDTO;
import com.rishavdas.blog.cms.dto.PostSummaryDTO;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.TimeRange;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    Post createPost(PostDTO postDTO, List<MultipartFile>images);
    Post getPostById(Long id);
    List<Post> getAllPosts();
    Post updatePost(Long id, PostDTO postDTO);
    void deletePost(Long id);
    Post findPostByTitle(String title);
    Post findPostByTitleContaining(String title);
    List<Post> getLatestPosts();
    List<Post> findPostsByUsers(Long userId);
    List<PostSummaryDTO> getPostSummaries();
    List<PostLikeDTO> getTopLikedPosts(TimeRange range, int limit);
    Page<PostSummaryDTO> getPostsByUser(Long userId, int page, int size);
}
