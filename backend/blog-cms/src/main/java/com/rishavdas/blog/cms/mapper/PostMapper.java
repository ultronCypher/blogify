package com.rishavdas.blog.cms.mapper;

import com.rishavdas.blog.cms.dto.PostDTO;
import com.rishavdas.blog.cms.dto.PostLikeDTO;
import com.rishavdas.blog.cms.dto.PostSummaryDTO;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.PostImage;
import com.rishavdas.blog.cms.model.PostLike;
import com.rishavdas.blog.cms.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostMapper {
    public static PostDTO toDTO(Post post){
        if(post==null) return null;
        PostDTO dto=new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthor(UserMapper.toDTO(post.getAuthor()));
        dto.setImages(
                post.getImages() == null
                        ? List.of()
                        : post.getImages()
                        .stream()
                        .map(PostImage::getImageUrl)
                        .toList()
        );
        return dto;
    }

    public static Post toEntity(PostDTO postDTO, User author){
        Post post=new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setAuthor(author);
        return post;
    }

    public PostSummaryDTO toSummary(
            Post post, Long likes, Long views, Long comments
    ){
        PostSummaryDTO dto=new PostSummaryDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setExcerpt(post.getContent().substring(0,Math.min(400,post.getContent().length())));
        dto.setAuthorUsername(post.getAuthor().getUsername());
        dto.setAuthorAvatarUrl(post.getAuthor().getAvatarUrl());
        dto.setLikesCount(likes);
        dto.setViewsCount(views);
        dto.setCommentsCount(comments);
        dto.setPreviewImage(
                post.getImages() != null && !post.getImages().isEmpty()
                        ? post.getImages().get(0).getImageUrl()
                        : null
        );
        return dto;
    }

    public PostLikeDTO toPostLikeDTO(Post post,Long likes){
        PostLikeDTO postLikeDTO=new PostLikeDTO();
        postLikeDTO.setId(post.getId());
        postLikeDTO.setTitle(post.getTitle());
        postLikeDTO.setAuthorUsername(post.getAuthor().getUsername());
        postLikeDTO.setLikesCount(likes);
        return postLikeDTO;
    }
}
