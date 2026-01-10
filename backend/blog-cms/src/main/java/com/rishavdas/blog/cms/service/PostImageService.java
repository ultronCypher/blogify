package com.rishavdas.blog.cms.service;

import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.PostImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostImageService {
    List<PostImage> uploadImages(List<MultipartFile> files, Post post);
}
