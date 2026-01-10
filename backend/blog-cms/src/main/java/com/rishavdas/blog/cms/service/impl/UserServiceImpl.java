package com.rishavdas.blog.cms.service.impl;

import com.rishavdas.blog.cms.dto.PostSummaryDTO;
import com.rishavdas.blog.cms.dto.UserDTO;
import com.rishavdas.blog.cms.dto.UserSummaryDTO;
import com.rishavdas.blog.cms.mapper.PostMapper;
import com.rishavdas.blog.cms.model.Post;
import com.rishavdas.blog.cms.model.User;
import com.rishavdas.blog.cms.repository.CommentRepository;
import com.rishavdas.blog.cms.repository.PostLikeRepository;
import com.rishavdas.blog.cms.repository.PostRepository;
import com.rishavdas.blog.cms.repository.UserRepository;
import com.rishavdas.blog.cms.service.CloudinaryService;
import com.rishavdas.blog.cms.service.PostViewRedisService;
import com.rishavdas.blog.cms.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final PostViewRedisService postViewRedisService;
    private final PostMapper postMapper;
    private final CloudinaryService cloudinaryService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, PostRepository postRepository, PostLikeRepository postLikeRepository, CommentRepository commentRepository, PostViewRedisService postViewRedisService, PostMapper postMapper, CloudinaryService cloudinaryService){
        this.userRepository=userRepository;
        this.passwordEncoder = passwordEncoder;
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
        this.postViewRedisService = postViewRedisService;
        this.postMapper = postMapper;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, UserDTO dto) {
        User existingUser=userRepository.findById(id).orElse(null);
        if(existingUser == null){
            return null;
        }
        existingUser.setUsername(dto.getUsername());
        existingUser.setRole(dto.getRole());
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> findUsernameContaining(String keyword){
        return userRepository.findByUsernameContaining(keyword);
    }

    @Override
    public User getByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("User not found"));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("user not found"));
    }

//    @Override
//    public UserSummaryDTO getUserProfile(String username) {
//        User user=userRepository.findByUsername(username)
//                .orElseThrow(()->new RuntimeException("User not found"));
//        List<Post>posts=postRepository.findPostsByUsers(user.getId());
//        List<PostSummaryDTO>postSummaryDTOs=posts.stream().map(post -> {
//            Long likes=postLikeRepository.countByPost(post);
//            Long comments=commentRepository.getCommentCount(post.getId());
//            Long views=postViewRedisService.getLiveViews(post.getId());
//            return postMapper.toSummary(post,likes,views,comments);
//        }).toList();
//        UserSummaryDTO dto=UserSummaryDTO.from(user);
//        dto.setPosts(postSummaryDTOs);
//        return dto;
//    }

    @Override
    public String uploadAvatar(MultipartFile file, String username) {
        User user=userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("User not found"));
        Map<String,String> upload=cloudinaryService.uploadAvatar(file);
        user.setAvatarUrl(upload.get("url"));
        user.setAvatarPublicId(upload.get("publicId"));
        userRepository.save(user);
        return  upload.get("url");
    }

    @Override
    public String updateAvatar(MultipartFile file, String username) {
        User user=userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("User not found"));
        if (user.getAvatarPublicId() != null) {
            cloudinaryService.deleteImage(user.getAvatarPublicId());
        }
        Map<String,String> upload=cloudinaryService.uploadAvatar(file);
        user.setAvatarUrl(upload.get("url"));
        user.setAvatarPublicId(upload.get("publicId"));
        userRepository.save(user);
        return  upload.get("url");
    }

    @Override
    public UserSummaryDTO getPublicProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));
        return UserSummaryDTO.from(user);
    }

    @Override
    public UserSummaryDTO getPrivateProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user not found"));

        return UserSummaryDTO.from(user);
    }

}
