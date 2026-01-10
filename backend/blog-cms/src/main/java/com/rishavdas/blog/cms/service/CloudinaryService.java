package com.rishavdas.blog.cms.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }
    public Map<String, String> uploadAvatar(MultipartFile file){
        try{
            Map<?,?>uploadResult=cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder","avatars",
                            "transformation", "w_256,h_256,c_fill"
                    )
            );
            return Map.of(
                    "url",uploadResult.get("secure_url").toString(),
                    "publicId", uploadResult.get("public_id").toString()
            );
        }catch (Exception e){
            throw new RuntimeException("Failed to upload avatar", e);
        }
    }

    public void deleteImage(String publicId){
        try{
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (Exception e) {
            System.err.println("Failed to delete old avatar: " + publicId);
        }
    }
}
