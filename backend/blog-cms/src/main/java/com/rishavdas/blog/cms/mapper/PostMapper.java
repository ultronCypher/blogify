package com.rishavdas.blog.cms.mapper;

import com.rishavdas.blog.cms.dto.post_dtos.PostDTO;
import com.rishavdas.blog.cms.dto.PostLikeDTO;
import com.rishavdas.blog.cms.dto.PostSummaryDTO;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.PostImage;
import com.rishavdas.blog.cms.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

import com.rishavdas.blog.cms.dto.TagDTO;

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
        dto.setTags(
                post.getTags() == null
                        ? List.of()
                        : post.getTags()
                        .stream()
                        .map(tag -> {
                            TagDTO tagDTO = new TagDTO();
                            tagDTO.setId(tag.getId());
                            tagDTO.setName(tag.getName());
                            tagDTO.setSlug(tag.getSlug());
                            return tagDTO;
                        })
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
        dto.setExcerpt(extractPlainTextExcerpt(post.getContent(), 400));
        dto.setAuthorUsername(post.getAuthor().getUsername());
        dto.setAuthorAvatarUrl(post.getAuthor().getAvatarUrl());
        dto.setAuthorId(post.getAuthor().getId());
        dto.setLikesCount(likes);
        dto.setViewsCount(views);
        dto.setCommentsCount(comments);
        dto.setPreviewImage(
                post.getImages() != null && !post.getImages().isEmpty()
                        ? post.getImages().get(0).getImageUrl()
                        : null
        );
        dto.setTags(
                post.getTags() == null
                        ? List.of()
                        : post.getTags()
                        .stream()
                        .map(tag -> {
                            TagDTO tagDTO = new TagDTO();
                            tagDTO.setId(tag.getId());
                            tagDTO.setName(tag.getName());
                            tagDTO.setSlug(tag.getSlug());
                            return tagDTO;
                        })
                        .toList()
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

    /**
     * Strips HTML tags and decodes common HTML entities from Quill's HTML content,
     * then trims to {@code maxLength} characters of plain text.
     */
    private static final Pattern HTML_TAGS = Pattern.compile("<[^>]*>");

    private static String extractPlainTextExcerpt(String html, int maxLength) {
        if (html == null || html.isEmpty()) return "";
        // Remove all HTML tags
        String text = HTML_TAGS.matcher(html).replaceAll("");
        // Decode common HTML entities
        text = text.replace("&amp;", "&")
                   .replace("&lt;", "<")
                   .replace("&gt;", ">")
                   .replace("&quot;", "\"")
                   .replace("&#39;", "'")
                   .replace("&nbsp;", " ");
        // Collapse extra whitespace/newlines left by block tags
        text = text.replaceAll("\\s+", " ").trim();
        return text.substring(0, Math.min(maxLength, text.length()));
    }
}
