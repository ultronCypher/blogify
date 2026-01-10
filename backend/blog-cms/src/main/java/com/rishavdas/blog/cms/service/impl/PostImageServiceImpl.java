package com.rishavdas.blog.cms.service.impl;

import com.cloudinary.Cloudinary;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.PostImage;
import com.rishavdas.blog.cms.service.PostImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PostImageServiceImpl implements PostImageService {
    private final Cloudinary cloudinary;

    public PostImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public List<PostImage> uploadImages(List<MultipartFile> files, Post post) {
        if(files==null || files.isEmpty()){
            return List.of();
        }
        if(files.size()>4){
            throw new RuntimeException("Max 4 images allowed");
        }
        List<PostImage>images=new ArrayList<>();
        for (int i=0;i<files.size();i++){
            MultipartFile file=files.get(i);
            try {
                Map<?,?>result=cloudinary.uploader().upload(
                        file.getBytes(),
                        Map.of(
                                "folder", "blog_posts",
                                "resource_type", "image"
                        )
                );
                PostImage image=new PostImage();
                image.setPost(post);
                image.setImageUrl(result.get("secure_url").toString());
                image.setPublicId(result.get("public_id").toString());
                image.setPosition(i);
                images.add(image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        return images;
    }
}
